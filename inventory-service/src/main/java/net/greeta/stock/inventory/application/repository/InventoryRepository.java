package net.greeta.stock.inventory.application.repository;


import net.greeta.stock.common.domain.dto.inventory.InventoryStatus;
import net.greeta.stock.inventory.application.entity.OrderInventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface InventoryRepository extends ReactiveCrudRepository<OrderInventory, UUID> {

    Mono<Boolean> existsByOrderId(UUID orderId);

    Mono<OrderInventory> findByOrderIdAndStatus(UUID orderId, InventoryStatus status);

}
