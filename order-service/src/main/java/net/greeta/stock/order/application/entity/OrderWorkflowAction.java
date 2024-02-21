package net.greeta.stock.order.application.entity;

import net.greeta.stock.common.domain.dto.order.WorkflowAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderWorkflowAction {

    @Id
    private UUID id;
    private UUID orderId;
    private WorkflowAction action;
    private Instant createdAt;

}
