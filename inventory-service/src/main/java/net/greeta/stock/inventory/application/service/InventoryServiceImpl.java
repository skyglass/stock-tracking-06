package net.greeta.stock.inventory.application.service;

import lombok.RequiredArgsConstructor;
import net.greeta.stock.common.domain.dto.inventory.*;
import net.greeta.stock.common.util.DuplicateEventValidator;
import net.greeta.stock.inventory.application.mapper.EntityDtoMapper;
import net.greeta.stock.inventory.application.repository.InventoryRepository;
import net.greeta.stock.inventory.application.repository.ProductRepository;
import net.greeta.stock.inventory.common.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRetryHelper productRetryHelper;

    @Override
    public Mono<OrderInventoryDto> deduct(InventoryDeductRequest request) {
        return DuplicateEventValidator.validate(
             this.inventoryRepository.existsByOrderId(request.orderId()),
             Mono.just(request)
        )
        .flatMap(productRetryHelper::deductStock)
        .doOnNext(dto -> log.info("inventory deducted for {}", dto.orderId()));
    }

    @Override
    public Mono<ProductInventoryDto> addStock(InventoryAddStockRequest request) {
        return this.productRetryHelper.addStock(request)
                .doOnNext(dto -> log.info("added stock availableStock {} for product {}",
                        dto.availableStock(), dto.productId()));
    }


    @Override
    @Transactional
    public Mono<OrderInventoryDto> restore(UUID orderId) {
        return this.productRetryHelper.restore(orderId)
               .doOnNext(dto -> log.info("InventoryServiceImpl.restore: restored stock quantity {} for {}",
                       dto.quantity(), dto.orderId()));
    }

    @Override
    public Mono<ProductDetails> getProductDetails(Integer productId) {
        return this.productRepository.findById(productId)
                .map(p -> EntityDtoMapper.toProductDetails(p))
                .doOnNext(dto -> log.info("get details for product {} with availableStock {}",
                        dto.productId(), dto.availableStock()));
    }

}
