package net.greeta.stock.payment.application.repository;

import net.greeta.stock.common.messages.payment.PaymentStatus;
import net.greeta.stock.payment.application.entity.CustomerPayment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<CustomerPayment, UUID> {

    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<CustomerPayment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);

}
