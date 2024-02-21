package net.greeta.stock.inventory.application.controller;

import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.common.domain.dto.order.OrderDetails;
import net.greeta.stock.inventory.application.entity.Product;
import net.greeta.stock.inventory.common.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class InventoryController {

    @Autowired
    private InventoryService service;

    @PostMapping("/deduct")
    public Mono<ResponseEntity<OrderInventoryDto>> deduct(@RequestBody Mono<InventoryDeductRequest> mono){
        return mono.flatMap(this.service::deduct)
                .map(ResponseEntity.accepted()::body);
    }

    @PostMapping("/add/{orderId}")
    public  Mono<ResponseEntity<OrderInventoryDto>> add(@PathVariable UUID orderId){
        return this.service.restore(orderId)
                .map(ResponseEntity.accepted()::body);
    }

    @PostMapping("/add-stock")
    public  Mono<ResponseEntity<ProductInventoryDto>> addStock(@RequestBody Mono<InventoryAddStockRequest> mono){
        return mono.flatMap(this.service::addStock)
                .map(ResponseEntity.accepted()::body);
    }

    @GetMapping("{productId}")
    public Mono<ProductDetails> getProductDetails(@PathVariable Integer productId) {
        return this.service.getProductDetails(productId);
    }

}
