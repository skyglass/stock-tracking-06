package net.greeta.stock.order.application.repository;

import net.greeta.stock.order.application.entity.OrderWorkflowAction;
import net.greeta.stock.order.common.enums.WorkflowAction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface OrderWorkflowActionRepository extends ReactiveCrudRepository<OrderWorkflowAction, UUID> {

    Mono<Boolean> existsByOrderIdAndAction(UUID orderId, WorkflowAction action);

    Flux<OrderWorkflowAction> findByOrderIdOrderByCreatedAt(UUID orderId);

}
