package net.greeta.stock.inventory.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.inventory.common.exception.OutOfStockException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRetryHelper {

    private final ProductService productService;

    private final AtomicInteger counter = new AtomicInteger(0);

    public Mono<OrderInventoryDto> deductStock(InventoryDeductRequest request) {
        log.info("RetryHelper.deductStock started retry {} for product {}", counter.getAndIncrement(), request.productId());
        return productService.deductStock(request)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .filter(e -> e instanceof DataAccessException ||  e instanceof OutOfStockException)
                );
    }

    public Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request) {
        log.info("RetryHelper.addStock started retry {} for product {}", counter.getAndIncrement(), request.productId());
        return productService.addStock(request)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .filter(e -> e instanceof DataAccessException)
                );
    }

    public Mono<OrderInventoryDto> restore(UUID orderId) {
        log.info("RetryHelper.restore started retry {} for order {}", counter.getAndIncrement(), orderId);
        return productService.restore(orderId)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .filter(e -> e instanceof DataAccessException)
                );
    }


}
