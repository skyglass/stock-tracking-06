package net.greeta.stock.shipping;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(properties = {
		"logging.level.root=ERROR",
		"logging.level.net.greeta.stock*=INFO",
		"spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest"
})
@EmbeddedKafka(
		partitions = 1,
		bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public abstract class AbstractIntegrationTest {


}
