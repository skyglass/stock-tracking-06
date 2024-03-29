package net.greeta.stock.common.domain.dto.order.events;

import net.greeta.stock.common.domain.dto.order.OrderId;
import net.greeta.stock.common.domain.dto.order.OrderItem;
import net.greeta.stock.common.domain.dto.order.base.DomainEvent;

import java.util.List;
import java.util.UUID;

/**
 * Event used when the grace period order is confirmed.
 */
public record OrderStatusChangedToAwaitingValidationDomainEvent(
    OrderId orderId,
    List<OrderItem> orderItems,
    UUID requestId) implements DomainEvent {
}
