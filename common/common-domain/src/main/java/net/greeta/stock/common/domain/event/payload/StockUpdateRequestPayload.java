package net.greeta.stock.common.domain.event.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StockUpdateRequestPayload {

    @JsonProperty
    private String orderId;
    @JsonProperty
    private String productId;
    @JsonProperty
    private Integer quantity;
    @JsonProperty
    private ZonedDateTime createdAt;
    @JsonProperty
    private StockUpdateStatus stockUpdateStatus;
}
