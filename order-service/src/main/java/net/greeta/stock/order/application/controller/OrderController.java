package net.greeta.stock.order.application.controller;

import net.greeta.stock.order.common.dto.OrderCreateRequest;
import net.greeta.stock.order.common.dto.OrderDetails;
import net.greeta.stock.order.common.dto.PurchaseOrderDto;
import net.greeta.stock.order.common.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public Mono<ResponseEntity<PurchaseOrderDto>> placeOrder(@RequestBody Mono<OrderCreateRequest> mono) {
        return mono.flatMap(this.service::placeOrder)
                   .map(ResponseEntity.accepted()::body);
    }

    @GetMapping("all")
    public Flux<PurchaseOrderDto> getAllOrders(){
        return this.service.getAllOrders();
    }

    @GetMapping("{orderId}")
    public Mono<OrderDetails> getOrderDetails(@PathVariable UUID orderId){
        return this.service.getOrderDetails(orderId);
    }

}
