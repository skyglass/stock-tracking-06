package net.greeta.stock.order.application.repository;

import net.greeta.stock.order.application.entity.PurchaseOrder;
import net.greeta.stock.common.domain.dto.order.OrderStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends ReactiveCrudRepository<PurchaseOrder, UUID> {

    Mono<PurchaseOrder> findByOrderIdAndStatus(UUID orderId, OrderStatus status);

}
