package net.greeta.stock.common.domain.event.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StockUpdateResponsePayload {
    @JsonProperty
    private String stockUpdateId;

    @JsonProperty
    private String productId;

    @JsonProperty
    private String orderId;

    @JsonProperty
    private Integer quantity;

    @JsonProperty
    private ZonedDateTime createdAt;

    @JsonProperty
    private StockUpdateStatus stockUpdateStatus;

    @JsonProperty
    private List<String> failureMessages;
}
