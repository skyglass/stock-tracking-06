package net.greeta.stock.shipping.messaging.processor;

import net.greeta.stock.common.messages.shipping.ShippingRequest;
import net.greeta.stock.common.messages.shipping.ShippingResponse;
import net.greeta.stock.common.processor.RequestProcessor;
import reactor.core.publisher.Mono;

public interface ShippingRequestProcessor extends RequestProcessor<ShippingRequest, ShippingResponse> {

    @Override
    default Mono<ShippingResponse> process(ShippingRequest request) {
        return switch (request){
            case ShippingRequest.Schedule s -> this.handle(s);
        };
    }

    Mono<ShippingResponse> handle(ShippingRequest.Schedule request);

}
