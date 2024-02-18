package net.greeta.stock.order.application.mapper;

import net.greeta.stock.order.application.entity.OrderWorkflowAction;
import net.greeta.stock.order.application.entity.PurchaseOrder;
import net.greeta.stock.order.common.dto.OrderCreateRequest;
import net.greeta.stock.order.common.dto.OrderDetails;
import net.greeta.stock.order.common.dto.OrderWorkflowActionDto;
import net.greeta.stock.order.common.dto.PurchaseOrderDto;
import net.greeta.stock.order.common.enums.OrderStatus;
import net.greeta.stock.order.common.enums.WorkflowAction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EntityDtoMapper {

    public static PurchaseOrder toPurchaseOrder(OrderCreateRequest request) {
        return PurchaseOrder.builder()
                            .status(OrderStatus.PENDING)
                            .customerId(request.customerId())
                            .productId(request.productId())
                            .quantity(request.quantity())
                            .unitPrice(request.unitPrice())
                            .amount(request.quantity() * request.unitPrice())
                            .build();
    }

    public static PurchaseOrderDto toPurchaseOrderDto(PurchaseOrder purchaseOrder) {
        return PurchaseOrderDto.builder()
                               .orderId(purchaseOrder.getOrderId())
                               .unitPrice(purchaseOrder.getUnitPrice())
                               .quantity(purchaseOrder.getQuantity())
                               .productId(purchaseOrder.getProductId())
                               .amount(purchaseOrder.getAmount())
                               .customerId(purchaseOrder.getCustomerId())
                               .status(purchaseOrder.getStatus())
                               .deliveryDate(purchaseOrder.getDeliveryDate())
                               .build();
    }

    public static OrderWorkflowAction toOrderWorkflowAction(UUID orderId, WorkflowAction action) {
        return OrderWorkflowAction.builder()
                                  .orderId(orderId)
                                  .action(action)
                                  .createdAt(Instant.now())
                                  .build();
    }

    public static OrderWorkflowActionDto toOrderWorkflowActionDto(OrderWorkflowAction orderWorkflowAction) {
        return OrderWorkflowActionDto.builder()
                                     .id(orderWorkflowAction.getId())
                                     .orderId(orderWorkflowAction.getOrderId())
                                     .createdAt(orderWorkflowAction.getCreatedAt())
                                     .action(orderWorkflowAction.getAction())
                                     .build();
    }

    public static OrderDetails toOrderDetails(PurchaseOrderDto order, List<OrderWorkflowActionDto> actions) {
        return OrderDetails.builder()
                           .order(order)
                           .actions(actions)
                           .build();
    }

}
