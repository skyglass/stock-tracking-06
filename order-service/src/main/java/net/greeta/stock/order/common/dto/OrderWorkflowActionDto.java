package net.greeta.stock.order.common.dto;

import net.greeta.stock.order.common.enums.WorkflowAction;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderWorkflowActionDto(UUID id,
                                     UUID orderId,
                                     WorkflowAction action,
                                     Instant createdAt) {
}
