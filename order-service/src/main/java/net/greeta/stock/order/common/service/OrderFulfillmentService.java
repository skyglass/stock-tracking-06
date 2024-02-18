package net.greeta.stock.order.common.service;

import net.greeta.stock.order.common.dto.OrderShipmentSchedule;
import net.greeta.stock.order.common.dto.PurchaseOrderDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderFulfillmentService {

    Mono<PurchaseOrderDto> get(UUID orderId);

    Mono<PurchaseOrderDto> schedule(OrderShipmentSchedule shipmentSchedule);

    Mono<PurchaseOrderDto> complete(UUID orderId);

    Mono<PurchaseOrderDto> cancel(UUID orderId);

}
