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
        return updateStock(request.productId(), request.quantity())
                .flatMap(p -> {
                    var orderInventory = EntityDtoMapper.toOrderInventory(request);
                    orderInventory.setStatus(InventoryStatus.DEDUCTED);
                    return this.inventoryRepository.save(orderInventory);
                })
                .map(EntityDtoMapper::toDto);
    }

    @Transactional
    public Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request) {
        return updateStock(request.productId(), -request.quantity());
    }

    @Transactional
    public Mono<OrderInventoryDto> restore(UUID orderId) {
        return this.inventoryRepository.findByOrderIdAndStatus(orderId, InventoryStatus.DEDUCTED)
                .doOnNext(i -> updateStock(i.getProductId(), -i.getQuantity()))
                .flatMap(inventory -> {
                    inventory.setStatus(InventoryStatus.RESTORED);
                    return this.inventoryRepository.save(inventory);
                })
                .doOnNext(i -> {
                    log.info("restored stock quantity {} for order {} and product {}",
                            i.getQuantity(), i.getOrderId(), i.getProductId());
                })
                .map(i -> EntityDtoMapper.toDto(i));
    }

    private Mono<ProductInventoryDto> updateStock(Integer productId, Integer updateQuantity) {
        InventoryStatus status = updateQuantity >= 0 ? InventoryStatus.DEDUCTED : InventoryStatus.RESTORED;
        return productRepository.findById(productId)
                .filter(p -> p.getAvailableQuantity() >= updateQuantity)
                .switchIfEmpty(OUT_OF_STOCK)
                .flatMap(p -> {
                    p.setAvailableQuantity(p.getAvailableQuantity() - updateQuantity);
                    return productRepository.save(p);
                })
                .map(p -> EntityDtoMapper.toProductInventoryDto(p, status))
                .doOnNext(dto -> log.info(
                        "ProductService.updateStock: updated availableStock {} for product {}",
                        dto.availableStock(), dto.productId()));
    }

    private Mono<OrderInventoryDto> restore(OrderInventory orderInventory, Product product) {
        product.setAvailableQuantity(product.getAvailableQuantity() + orderInventory.getQuantity());
        orderInventory.setStatus(InventoryStatus.RESTORED);
        return this.productRepository.save(product)
                .then(this.inventoryRepository.save(orderInventory))
                .map(EntityDtoMapper::toDto);
    }

}
