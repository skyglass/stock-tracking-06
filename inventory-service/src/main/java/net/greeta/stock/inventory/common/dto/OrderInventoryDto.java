package net.greeta.stock.inventory.common.dto;

import net.greeta.stock.common.messages.inventory.InventoryStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderInventoryDto(UUID inventoryId,
                                UUID orderId,
                                Integer productId,
                                Integer quantity,
                                InventoryStatus status) {
}
