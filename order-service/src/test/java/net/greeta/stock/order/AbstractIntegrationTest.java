package net.greeta.stock.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import net.greeta.stock.common.messages.Request;
import net.greeta.stock.common.messages.inventory.InventoryRequest;
import net.greeta.stock.common.messages.inventory.InventoryResponse;
import net.greeta.stock.common.messages.payment.PaymentRequest;
import net.greeta.stock.common.messages.payment.PaymentResponse;
import net.greeta.stock.common.messages.shipping.ShippingRequest;
import net.greeta.stock.common.messages.shipping.ShippingResponse;
import net.greeta.stock.order.common.dto.OrderCreateRequest;
import net.greeta.stock.order.common.dto.OrderDetails;
import net.greeta.stock.order.common.dto.PurchaseOrderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@AutoConfigureWebTestClient
@SpringBootTest(properties = {
		"logging.level.root=ERROR",
		"logging.level.net.greeta.stock*=INFO",
		"spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest",
		"spring.cloud.function.definition=requestConsumer;orderOrchestrator",
		"spring.cloud.stream.bindings.requestConsumer-in-0.destination=payment-request,inventory-request,shipping-request"
})
@EmbeddedKafka(
		partitions = 1,
		bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@Import(AbstractIntegrationTest.TestConfig.class)
@ActiveProfiles("integration")
@Testcontainers
public abstract class AbstractIntegrationTest {

	private static final Sinks.Many<Request> resSink = Sinks.many().unicast().onBackpressureBuffer();
	private static final Flux<Request> resFlux = resSink.asFlux().cache(0);

	@Autowired
	private WebTestClient client;

	@Autowired
	private StreamBridge streamBridge;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// Customer
	private static KeycloakToken bjornTokens;
	// Customer and employee
	private static KeycloakToken isabelleTokens;



	@Container
	private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:22.0.1")
			.withRealmImportFile("test-realm-config.json");

	@Container
	static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.r2dbc.url", AbstractIntegrationTest::r2dbcUrl);
		registry.add("spring.r2dbc.username", postgresql::getUsername);
		registry.add("spring.r2dbc.password", postgresql::getPassword);
		registry.add("spring.flyway.url", postgresql::getJdbcUrl);
		registry.add("spring.datasource.url", postgresql::getJdbcUrl);

		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> keycloakContainer.getAuthServerUrl() + "realms/stock-realm");
		registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
				() -> keycloakContainer.getAuthServerUrl() + "realms/stock-realm/protocol/openid-connect/certs");
	}

	@BeforeAll
	static void generateAccessTokens() {
		WebClient webClient = WebClient.builder()
				.baseUrl(keycloakContainer.getAuthServerUrl() + "realms/stock-realm/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);
	}

	@BeforeEach
	void resetDatabase() {
		jdbcTemplate.execute("delete from order_workflow_action");
		jdbcTemplate.execute("delete from purchase_order");
	}

	private static String r2dbcUrl() {
		return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
				postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
	}

	protected void emitResponse(PaymentResponse response){
		this.streamBridge.send("payment-response", response);
	}

	protected void emitResponse(InventoryResponse response){
		this.streamBridge.send("inventory-response", response);
	}

	protected void emitResponse(ShippingResponse response){
		this.streamBridge.send("shipping-response", response);
	}

	protected UUID initiateOrder(OrderCreateRequest request){
		var orderIdRef = new AtomicReference<UUID>();
		this.client
				.post()
				.uri("/order")
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isAccepted()
				.expectBody()
				.jsonPath("$.orderId").exists()
				.jsonPath("$.orderId").value(id -> orderIdRef.set(UUID.fromString(id.toString())))
				.jsonPath("$.status").isEqualTo("PENDING");
		return orderIdRef.get();
	}

	protected void verifyOrderDetails(UUID orderId, Consumer<OrderDetails> assertion){
		this.client
				.get()
				.uri("/order/{orderId}", orderId)
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(OrderDetails.class)
				.value(r -> {
					Assertions.assertEquals(orderId, r.order().orderId());
					Assertions.assertNotNull(r.actions());
					assertion.accept(r);
				});
	}

	protected void verifyAllOrders(UUID... orderIds){
		this.client
				.get()
				.uri("/order/all")
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(new ParameterizedTypeReference<List<PurchaseOrderDto>>() {
				})
				.value(r -> {
					Assertions.assertEquals(List.of(orderIds), r.stream().map(PurchaseOrderDto::orderId).toList());
				});
	}

	protected void verifyPaymentRequest(UUID orderId, int amount){
		expectRequest(PaymentRequest.Process.class, e -> {
			Assertions.assertEquals(amount, e.amount());
			Assertions.assertEquals(orderId, e.orderId());
		});
	}

	protected void verifyInventoryRequest(UUID orderId, int quantity){
		expectRequest(InventoryRequest.Deduct.class, e -> {
			Assertions.assertEquals(quantity, e.quantity());
			Assertions.assertEquals(orderId, e.orderId());
		});
	}

	protected void verifyScheduleRequest(UUID orderId, int quantity){
		expectRequest(ShippingRequest.Schedule.class, e -> {
			Assertions.assertEquals(quantity, e.quantity());
			Assertions.assertEquals(orderId, e.orderId());
		});
	}

	protected <T> void expectRequest(Class<T> type, Consumer<T> assertion){
		resFlux
				//.next()
				.timeout(Duration.ofSeconds(2), Mono.empty())
				.cast(type)
				.as(StepVerifier::create)
				.consumeNextWith(assertion)
				.verifyComplete();
	}

	protected void expectRequests(Consumer<List<Request>> assertion){
		resFlux
				//.next()
				.timeout(Duration.ofSeconds(2), Mono.empty())
				.cast(Request.class)
				.collectList()
				.as(StepVerifier::create)
				.consumeNextWith(assertion)
				.verifyComplete();
	}

	protected void expectNoRequest(){
		resFlux
				.next()
				.timeout(Duration.ofSeconds(2), Mono.empty())
				.as(StepVerifier::create)
				.verifyComplete();
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		public Consumer<Flux<Request>> requestConsumer(){
			return f -> f.doOnNext(resSink::tryEmitNext).subscribe();
		}

	}

	private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
		return webClient
				.post()
				.body(BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "stock-app")
						.with("username", username)
						.with("password", password)
				)
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block();
	}

	private record KeycloakToken(String accessToken) {

		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}

	}

}
