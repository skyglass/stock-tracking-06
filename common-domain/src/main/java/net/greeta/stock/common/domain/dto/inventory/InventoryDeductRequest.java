package net.greeta.stock.common.domain.dto.inventory;

import lombok.Builder;

import java.util.UUID;

@Builder
public record InventoryDeductRequest(UUID orderId,
                                     Integer productId,
                                     Integer quantity) {
}
