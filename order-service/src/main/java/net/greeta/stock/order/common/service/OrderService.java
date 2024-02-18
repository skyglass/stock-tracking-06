package net.greeta.stock.order.common.service;

import net.greeta.stock.order.common.dto.OrderCreateRequest;
import net.greeta.stock.order.common.dto.OrderDetails;
import net.greeta.stock.order.common.dto.PurchaseOrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {

    Mono<PurchaseOrderDto> placeOrder(OrderCreateRequest request);

    Flux<PurchaseOrderDto> getAllOrders();

    Mono<OrderDetails> getOrderDetails(UUID orderId);

}
