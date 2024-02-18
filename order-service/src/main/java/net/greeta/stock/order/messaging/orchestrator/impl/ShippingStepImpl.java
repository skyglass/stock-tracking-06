package net.greeta.stock.order.messaging.orchestrator.impl;

import net.greeta.stock.common.messages.Request;
import net.greeta.stock.common.messages.shipping.ShippingResponse;
import net.greeta.stock.common.orchestrator.RequestCompensator;
import net.greeta.stock.common.orchestrator.RequestSender;
import net.greeta.stock.order.common.enums.WorkflowAction;
import net.greeta.stock.order.common.service.OrderFulfillmentService;
import net.greeta.stock.order.common.service.WorkflowActionTracker;
import net.greeta.stock.order.messaging.mapper.MessageDtoMapper;
import net.greeta.stock.order.messaging.orchestrator.ShippingStep;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingStepImpl implements ShippingStep {

    private final WorkflowActionTracker tracker;
    private final OrderFulfillmentService service;
    private RequestCompensator previousStep;
    private RequestSender nextStep;

    @Override
    public Publisher<Request> compensate(UUID orderId) {
        return this.previousStep.compensate(orderId);
    }

    @Override
    public Publisher<Request> send(UUID orderId) {
        return this.tracker.track(orderId, WorkflowAction.SHIPPING_SCHEDULE_INITIATED)
                           .then(this.service.get(orderId))
                           .map(MessageDtoMapper::toShippingScheduleRequest);
    }

    @Override
    public void setPreviousStep(RequestCompensator previousStep) {
        this.previousStep = previousStep;
    }

    @Override
    public void setNextStep(RequestSender nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public Publisher<Request> onSuccess(ShippingResponse.Scheduled response) {
        return this.tracker.track(response.orderId(), WorkflowAction.SHIPPING_SCHEDULED)
                           .thenReturn(MessageDtoMapper.toShipmentSchedule(response))
                           .flatMap(this.service::schedule)
                           .thenMany(this.nextStep.send(response.orderId()));
    }

    @Override
    public Publisher<Request> onFailure(ShippingResponse.Declined response) {
        return this.tracker.track(response.orderId(), WorkflowAction.SHIPPING_DECLINED)
                           .thenMany(this.previousStep.compensate(response.orderId()));
    }
}
