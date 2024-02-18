package net.greeta.stock.order.messaging.config;

import net.greeta.stock.order.common.service.OrderEventListener;
import net.greeta.stock.order.messaging.publisher.OrderEventListenerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Configuration
public class OrderEventListenerConfig {

    @Bean
    public OrderEventListener orderEventListener(){
        var sink = Sinks.many().unicast().<UUID>onBackpressureBuffer();
        var flux = sink.asFlux();
        return new OrderEventListenerImpl(sink, flux);
    }

}
