package net.greeta.stock.order.messaging.orchestrator.impl;

import net.greeta.stock.common.messages.Request;
import net.greeta.stock.common.messages.inventory.InventoryResponse;
import net.greeta.stock.common.messages.payment.PaymentResponse;
import net.greeta.stock.common.messages.shipping.ShippingResponse;
import net.greeta.stock.common.publisher.EventPublisher;
import net.greeta.stock.order.common.service.OrderFulfillmentService;
import net.greeta.stock.order.messaging.orchestrator.InventoryStep;
import net.greeta.stock.order.messaging.orchestrator.OrderFulfillmentOrchestrator;
import net.greeta.stock.order.messaging.orchestrator.PaymentStep;
import net.greeta.stock.order.messaging.orchestrator.ShippingStep;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentOrchestratorImpl implements OrderFulfillmentOrchestrator {

    private final PaymentStep paymentStep;
    private final InventoryStep inventoryStep;
    private final ShippingStep shippingStep;
    private final OrderFulfillmentService service;
    private final EventPublisher<UUID> eventPublisher;
    private Workflow workflow;

    @PostConstruct
    private void init() {
        this.workflow = Workflow.startWith(paymentStep)
                                .thenNext(inventoryStep)
                                .thenNext(shippingStep)
                                .doOnFailure(id -> this.service.cancel(id).then())
                                .doOnSuccess(id -> this.service.complete(id).then()); // last step. or create it as builder

//        this.paymentStep.setPreviousStep(id -> this.service.cancel(id).then(Mono.empty()));
//        this.paymentStep.setNextStep(inventoryStep);
//        this.inventoryStep.setPreviousStep(paymentStep);
//        this.inventoryStep.setNextStep(shippingStep);
//        this.shippingStep.setPreviousStep(inventoryStep);
//        this.shippingStep.setNextStep(id -> this.service.complete(id).then(Mono.empty()));
    }

    @Override
    public Publisher<Request> orderInitialRequests() {
        return this.eventPublisher.publish()
                                  .flatMap(this.workflow.getFirstStep()::send);
    }

    @Override
    public Publisher<Request> handle(PaymentResponse response) {
        return this.paymentStep.process(response);
    }

    @Override
    public Publisher<Request> handle(InventoryResponse response) {
        return this.inventoryStep.process(response);
    }

    @Override
    public Publisher<Request> handle(ShippingResponse response) {
        return this.shippingStep.process(response);
    }

}
