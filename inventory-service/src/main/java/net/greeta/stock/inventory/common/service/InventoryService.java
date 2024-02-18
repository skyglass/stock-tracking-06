package net.greeta.stock.inventory.common.service;

import net.greeta.stock.inventory.common.dto.InventoryDeductRequest;
import net.greeta.stock.inventory.common.dto.OrderInventoryDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface InventoryService {

    Mono<OrderInventoryDto> deduct(InventoryDeductRequest request);

    Mono<OrderInventoryDto> restore(UUID orderId);

}
