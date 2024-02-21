package net.greeta.stock.order.common.service;

import net.greeta.stock.common.domain.dto.order.OrderWorkflowActionDto;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface WorkflowActionRetriever {

    Flux<OrderWorkflowActionDto> retrieve(UUID orderId);

}
