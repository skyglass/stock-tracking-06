package net.greeta.stock.payment;

import org.junit.BeforeClass;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DirtiesContext
@SpringBootTest(properties = {
		"logging.level.root=ERROR",
		"logging.level.net.greeta.stock*=INFO",
		"spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest",
		"spring.cloud.stream.kafka.binder.brokers=localhost:9092",
		"spring.cloud.stream.kafka.binder.replicationFactor=1"
})
@EmbeddedKafka
@Testcontainers
public abstract class AbstractIntegrationTest {

	@Container
	static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.r2dbc.url", AbstractIntegrationTest::r2dbcUrl);
		registry.add("spring.r2dbc.username", postgresql::getUsername);
		registry.add("spring.r2dbc.password", postgresql::getPassword);
		registry.add("spring.flyway.url", postgresql::getJdbcUrl);
	}

	private static String r2dbcUrl() {
		return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
				postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
	}



}
