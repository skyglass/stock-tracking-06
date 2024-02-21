package net.greeta.stock.order.application.service;

import net.greeta.stock.order.application.mapper.EntityDtoMapper;
import net.greeta.stock.order.application.repository.PurchaseOrderRepository;
import net.greeta.stock.common.domain.dto.order.OrderCreateRequest;
import net.greeta.stock.common.domain.dto.order.OrderDetails;
import net.greeta.stock.common.domain.dto.order.PurchaseOrderDto;
import net.greeta.stock.order.common.service.OrderEventListener;
import net.greeta.stock.order.common.service.OrderService;
import net.greeta.stock.order.common.service.WorkflowActionRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PurchaseOrderRepository repository;
    private final OrderEventListener eventListener;
    private final WorkflowActionRetriever actionRetriever;

    @Override
    public Mono<PurchaseOrderDto> placeOrder(OrderCreateRequest request) {
        var entity = EntityDtoMapper.toPurchaseOrder(request);
        return this.repository.save(entity)
                              .map(EntityDtoMapper::toPurchaseOrderDto)
                              .doOnNext(eventListener::emitOrderCreated);
    }

    @Override
    public Flux<PurchaseOrderDto> getAllOrders() {
        return this.repository.findAll()
                              .map(EntityDtoMapper::toPurchaseOrderDto);
    }

    @Override
    public Mono<OrderDetails> getOrderDetails(UUID orderId) {
        return this.repository.findById(orderId)
                              .map(EntityDtoMapper::toPurchaseOrderDto)
                              .zipWith(this.actionRetriever.retrieve(orderId).collectList())
                              .map(t -> EntityDtoMapper.toOrderDetails(t.getT1(), t.getT2()));
    }

}
