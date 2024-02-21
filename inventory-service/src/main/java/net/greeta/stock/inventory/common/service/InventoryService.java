package net.greeta.stock.inventory.common.service;

import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.common.domain.dto.order.OrderDetails;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InventoryService {

    Mono<OrderInventoryDto> deduct(InventoryDeductRequest request);

    Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request);

    Mono<OrderInventoryDto> restore(UUID orderId);

    Mono<ProductDetails> getProductDetails(Integer productId);

}
