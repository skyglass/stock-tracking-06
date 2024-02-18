package net.greeta.stock.inventory.messaging.processor;

import net.greeta.stock.common.messages.inventory.InventoryRequest;
import net.greeta.stock.common.messages.inventory.InventoryResponse;
import net.greeta.stock.common.processor.RequestProcessor;
import reactor.core.publisher.Mono;

public interface InventoryRequestProcessor extends RequestProcessor<InventoryRequest, InventoryResponse> {

    @Override
    default Mono<InventoryResponse> process(InventoryRequest request) {
        return switch (request){
            case InventoryRequest.Deduct r -> this.handle(r);
            case InventoryRequest.Restore r -> this.handle(r);
        };
    }

    Mono<InventoryResponse> handle(InventoryRequest.Deduct request);

    Mono<InventoryResponse> handle(InventoryRequest.Restore request);

}
