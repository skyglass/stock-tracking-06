package net.greeta.stock.inventory.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.inventory.application.entity.Product;
import net.greeta.stock.inventory.common.exception.OutOfStockException;
import net.greeta.stock.inventory.common.service.InventoryService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceRetryHelper implements InventoryService {

    public static final Mono<OrderInventoryDto> OUT_OF_STOCK = Mono.error(new OutOfStockException());

    private final InventoryServiceDelegate inventoryService;

    @Override
    public Mono<OrderInventoryDto> deduct(InventoryDeductRequest request) {
        return Mono.defer(() -> inventoryService.deduct(request))
                .retryWhen(
                        Retry.backoff(5, Duration.ofSeconds(1))
                        .filter(e -> {
                            log.info("test filter");
                            return e instanceof DataAccessException ||  e instanceof OutOfStockException;
                        })
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new OutOfStockException();
                        })
                )
                .onErrorResume(OutOfStockException.class, e -> {
                    log.info("test");
                    return OUT_OF_STOCK;
                });
    }

    @Override
    public Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request) {
        return inventoryService.addStock(request)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .filter(e -> e instanceof DataAccessException)
                );
    }

    @Override
    public Mono<OrderInventoryDto> restore(UUID orderId) {
        return inventoryService.restore(orderId)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .filter(e -> e instanceof DataAccessException)
                );
    }

    @Override
    public Mono<ProductDetails> getProductDetails(Integer productId) {
        return inventoryService.getProductDetails(productId);
    }
}
