package net.greeta.stock.order.common.service;

import net.greeta.stock.common.domain.dto.order.OrderCreateRequest;
import net.greeta.stock.common.domain.dto.order.OrderDetails;
import net.greeta.stock.common.domain.dto.order.PurchaseOrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {

    Mono<PurchaseOrderDto> placeOrder(OrderCreateRequest request);

    Flux<PurchaseOrderDto> getAllOrders();

    Mono<OrderDetails> getOrderDetails(UUID orderId);

}
