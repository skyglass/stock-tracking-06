package net.greeta.stock.order.application.service;

import net.greeta.stock.order.application.entity.PurchaseOrder;
import net.greeta.stock.order.application.mapper.EntityDtoMapper;
import net.greeta.stock.order.application.repository.PurchaseOrderRepository;
import net.greeta.stock.order.common.dto.OrderShipmentSchedule;
import net.greeta.stock.order.common.dto.PurchaseOrderDto;
import net.greeta.stock.order.common.enums.OrderStatus;
import net.greeta.stock.order.common.service.OrderFulfillmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentServiceImpl implements OrderFulfillmentService {

    private final PurchaseOrderRepository repository;

    @Override
    public Mono<PurchaseOrderDto> get(UUID orderId) {
        return this.repository.findById(orderId)
                              .map(EntityDtoMapper::toPurchaseOrderDto);
    }

    @Override
    public Mono<PurchaseOrderDto> schedule(OrderShipmentSchedule shipmentSchedule) {
        return this.update(shipmentSchedule.orderId(), e -> e.setDeliveryDate(shipmentSchedule.deliveryDate()));
    }

    @Override
    public Mono<PurchaseOrderDto> complete(UUID orderId) {
        return this.update(orderId, e -> e.setStatus(OrderStatus.COMPLETED));
    }

    @Override
    public Mono<PurchaseOrderDto> cancel(UUID orderId) {
        return this.update(orderId, e -> e.setStatus(OrderStatus.CANCELLED));
    }

    private Mono<PurchaseOrderDto> update(UUID orderId, Consumer<PurchaseOrder> consumer) {
        return this.repository.findByOrderIdAndStatus(orderId, OrderStatus.PENDING)
                              .doOnNext(consumer)
                              .flatMap(this.repository::save)
                              .map(EntityDtoMapper::toPurchaseOrderDto);
    }

}
