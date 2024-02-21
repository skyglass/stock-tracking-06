package net.greeta.stock.payment.messaging.processor;

import net.greeta.stock.common.messages.payment.PaymentRequest;
import net.greeta.stock.common.messages.payment.PaymentResponse;
import net.greeta.stock.common.processor.RequestProcessor;
import reactor.core.publisher.Mono;

public interface PaymentRequestProcessor extends RequestProcessor<PaymentRequest, PaymentResponse> {

    @Override
    default Mono<PaymentResponse> process(PaymentRequest request) {
        return switch (request){
            case PaymentRequest.Process p -> this.handle(p);
            case PaymentRequest.Refund p -> this.handle(p);
        };
    }

    Mono<PaymentResponse> handle(PaymentRequest.Process request);

    Mono<PaymentResponse> handle(PaymentRequest.Refund request);

}
