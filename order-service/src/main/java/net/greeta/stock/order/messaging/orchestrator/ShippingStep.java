package net.greeta.stock.order.messaging.orchestrator;

import net.greeta.stock.common.messages.Request;
import net.greeta.stock.common.messages.shipping.ShippingResponse;
import net.greeta.stock.common.orchestrator.WorkflowStep;
import org.reactivestreams.Publisher;

public interface ShippingStep extends WorkflowStep<ShippingResponse> {

    @Override
    default Publisher<Request> process(ShippingResponse response) {
        return switch (response){
            case ShippingResponse.Scheduled r -> this.onSuccess(r);
            case ShippingResponse.Declined r -> this.onFailure(r);
        };
    }

    Publisher<Request> onSuccess(ShippingResponse.Scheduled response);

    Publisher<Request> onFailure(ShippingResponse.Declined response);

}
