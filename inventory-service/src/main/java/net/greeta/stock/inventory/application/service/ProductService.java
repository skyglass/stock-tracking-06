package net.greeta.stock.inventory.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.inventory.application.entity.OrderInventory;
import net.greeta.stock.inventory.application.entity.Product;
import net.greeta.stock.inventory.application.mapper.EntityDtoMapper;
import net.greeta.stock.inventory.application.repository.InventoryRepository;
import net.greeta.stock.inventory.application.repository.ProductRepository;
import net.greeta.stock.inventory.common.exception.OutOfStockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final Mono<Product> OUT_OF_STOCK = Mono.error(new OutOfStockException());

    private final ProductRepository productRepository;

    private final InventoryRepository inventoryRepository;

    @Transactional
    public Mono<OrderInventoryDto> deductStock(InventoryDeductRequest request) {
        log.info("ProductService.deductStock started for request {}", request);
        return updateStock(request.productId(), request.quantity())
                .then(deductInventory(request));
    }

    private Mono<OrderInventoryDto> deductInventory(InventoryDeductRequest request) {
        var orderInventory = EntityDtoMapper.toOrderInventory(request);
        return updateInventory(orderInventory, InventoryStatus.DEDUCTED);
    }

    @Transactional
    public Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request) {
        return updateStock(request.productId(), -request.quantity());
    }

    @Transactional
    public Mono<OrderInventoryDto> restore(UUID orderId) {
        return this.inventoryRepository.findByOrderIdAndStatus(orderId, InventoryStatus.DEDUCTED)
                .doOnNext(i -> updateStock(i.getProductId(), -i.getQuantity()))
                .flatMap(i -> restoreInventory(i))
                .doOnNext(i -> {
                    log.info("restored stock quantity {} for order {} and product {}",
                            i.quantity(), i.orderId(), i.productId());
                });
    }

    private Mono<OrderInventoryDto> restoreInventory(OrderInventory orderInventory) {
        return updateInventory(orderInventory, InventoryStatus.RESTORED);
    }

    private Mono<OrderInventoryDto> updateInventory(OrderInventory orderInventory, InventoryStatus status) {
        orderInventory.setStatus(status);
        return this.inventoryRepository.save(orderInventory)
                .map(EntityDtoMapper::toDto);
    }

    private Mono<ProductInventoryDto> updateStock(Integer productId, Integer updateQuantity) {
        InventoryStatus status = updateQuantity >= 0 ? InventoryStatus.DEDUCTED : InventoryStatus.RESTORED;
        return productRepository.findById(productId)
                .filter(p -> p.getAvailableQuantity() >= updateQuantity)
                .switchIfEmpty(OUT_OF_STOCK)
                .flatMap(p -> updateProductInventory(p, updateQuantity, status))
                .doOnNext(dto -> log.info(
                        "ProductService.updateStock: updated availableStock {} for product {}",
                        dto.availableStock(), dto.productId()));
    }

    private Mono<ProductInventoryDto> updateProductInventory(Product product, Integer updateQuantity, InventoryStatus status) {
        product.setAvailableQuantity(product.getAvailableQuantity() - updateQuantity);
        return this.productRepository.save(product)
                .map(p -> EntityDtoMapper.toProductInventoryDto(p, status));
    }

}
