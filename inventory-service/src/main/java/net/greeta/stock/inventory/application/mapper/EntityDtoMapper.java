package net.greeta.stock.inventory.application.mapper;

import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.inventory.application.entity.OrderInventory;
import net.greeta.stock.inventory.application.entity.Product;

public class EntityDtoMapper {

    public static OrderInventory toOrderInventory(InventoryDeductRequest request) {
        return OrderInventory.builder()
                             .orderId(request.orderId())
                             .productId(request.productId())
                             .quantity(request.quantity())
                             .build();
    }

    public static OrderInventoryDto toDto(OrderInventory orderInventory) {
        return OrderInventoryDto.builder()
                                .inventoryId(orderInventory.getInventoryId())
                                .orderId(orderInventory.getOrderId())
                                .productId(orderInventory.getProductId())
                                .quantity(orderInventory.getQuantity())
                                .status(orderInventory.getStatus())
                                .build();
    }

    public static ProductInventoryDto toProductInventoryDto(Product product, InventoryStatus status) {
        return ProductInventoryDto.builder()
                .productId(product.getId())
                .availableStock(product.getAvailableQuantity())
                .status(status)
                .build();
    }

    public static ProductDetails toProductDetails(Product product) {
        return ProductDetails.builder()
                .productId(product.getId())
                .availableStock(product.getAvailableQuantity())
                .description(product.getDescription())
                .build();
    }

}
