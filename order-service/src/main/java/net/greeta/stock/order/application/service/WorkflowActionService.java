package net.greeta.stock.order.application.service;

import net.greeta.stock.common.util.DuplicateEventValidator;
import net.greeta.stock.order.application.mapper.EntityDtoMapper;
import net.greeta.stock.order.application.repository.OrderWorkflowActionRepository;
import net.greeta.stock.order.common.dto.OrderWorkflowActionDto;
import net.greeta.stock.order.common.enums.WorkflowAction;
import net.greeta.stock.order.common.service.WorkflowActionRetriever;
import net.greeta.stock.order.common.service.WorkflowActionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowActionService implements WorkflowActionTracker, WorkflowActionRetriever {

    private final OrderWorkflowActionRepository repository;

    @Override
    public Flux<OrderWorkflowActionDto> retrieve(UUID orderId) {
        return this.repository.findByOrderIdOrderByCreatedAt(orderId)
                              .map(EntityDtoMapper::toOrderWorkflowActionDto);
    }

    @Override
    public Mono<Void> track(UUID orderId, WorkflowAction action) {
        return DuplicateEventValidator.validate(
                this.repository.existsByOrderIdAndAction(orderId, action),
                this.repository.save(EntityDtoMapper.toOrderWorkflowAction(orderId, action)) // defer if required
        ).then();
    }
}
