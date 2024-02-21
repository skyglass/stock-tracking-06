package net.greeta.stock.payment.common.service;

import net.greeta.stock.payment.common.dto.PaymentDto;
import net.greeta.stock.payment.common.dto.PaymentProcessRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentService {

    Mono<PaymentDto> process(PaymentProcessRequest request);

    Mono<PaymentDto> refund(UUID orderId);

}
