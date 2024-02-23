package net.greeta.stock.inventory.application.service;

import lombok.RequiredArgsConstructor;
import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.common.util.DuplicateEventValidator;
import net.greeta.stock.inventory.application.entity.OrderInventory;
import net.greeta.stock.inventory.application.entity.Product;
import net.greeta.stock.inventory.application.mapper.EntityDtoMapper;
import net.greeta.stock.inventory.application.repository.InventoryRepository;
import net.greeta.stock.inventory.application.repository.ProductRepository;
import net.greeta.stock.inventory.common.exception.OutOfStockException;
import net.greeta.stock.inventory.common.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceDelegate {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceDelegate.class);
    public static final Mono<Product> OUT_OF_STOCK = Mono.error(new OutOfStockException());
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public Mono<OrderInventoryDto> deduct(InventoryDeductRequest request) {
        return DuplicateEventValidator.validate(
                        this.inventoryRepository.existsByOrderId(request.orderId()),
                        this.productRepository.findById(request.productId())
                )
                .filter(p -> p.getAvailableQuantity() >= request.quantity())
                .switchIfEmpty(OUT_OF_STOCK)
                .flatMap(p -> this.deductInventory(p, request))
                .doOnNext(dto -> log.info("inventory deducted for {}", dto.orderId()));
    }

    public Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request) {
        return productRepository.findById(request.productId())
                .flatMap(p -> addProductInventory(p, request.quantity()))
                .doOnNext(dto -> log.info(
                        "InventoryServiceImpl.addStock: added quantity {} to availableStock {} for product {}",
                        request.quantity(), dto.availableStock(), dto.productId()));
    }

    private Mono<ProductInventoryDto> addProductInventory(Product product, Integer quantity) {
        product.setAvailableQuantity(product.getAvailableQuantity() + quantity);
        return this.productRepository.save(product)
                .map(p -> EntityDtoMapper.toProductInventoryDto(p, InventoryStatus.RESTORED));
    }

    private Mono<OrderInventoryDto> deductInventory(Product product, InventoryDeductRequest request) {
        var orderInventory = EntityDtoMapper.toOrderInventory(request);
        product.setAvailableQuantity(product.getAvailableQuantity() - request.quantity());
        orderInventory.setStatus(InventoryStatus.DEDUCTED);
        return this.productRepository.save(product)
                .then(this.inventoryRepository.save(orderInventory))
                .map(EntityDtoMapper::toDto);
    }

    @Transactional
    public Mono<OrderInventoryDto> restore(UUID orderId) {
        return this.inventoryRepository.findByOrderIdAndStatus(orderId, InventoryStatus.DEDUCTED)
                .zipWhen(i -> this.productRepository.findById(i.getProductId()))
                .flatMap(t -> this.restore(t.getT1(), t.getT2()))
                .doOnNext(dto -> log.info("restored quantity {} for {}", dto.quantity(), dto.orderId()));
    }

    public Mono<ProductDetails> getProductDetails(Integer productId) {
        return this.productRepository.findById(productId)
                .map(p -> EntityDtoMapper.toProductDetails(p))
                .doOnNext(dto -> log.info("get details for product {} with availableStock {}", dto.productId(), dto.availableStock()));
    }

    private Mono<OrderInventoryDto> restore(OrderInventory orderInventory, Product product) {
        product.setAvailableQuantity(product.getAvailableQuantity() + orderInventory.getQuantity());
        orderInventory.setStatus(InventoryStatus.RESTORED);
        return this.productRepository.save(product)
                .then(this.inventoryRepository.save(orderInventory))
                .map(EntityDtoMapper::toDto);
    }

}

