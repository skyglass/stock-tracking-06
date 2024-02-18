package net.greeta.stock.common.domain.event.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.greeta.stock.common.domain.exception.OrderingDomainException;
import org.springframework.lang.NonNull;

import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StockUpdateStatus {
    AwaitingConfirmation("AwaitingConfirmation"),
    StockRejected("StockRejected"),
    StockConfirmed("StockConfirmed");

    @Getter
    private final String status;

    public static StockUpdateStatus of(@NonNull String status) {
        return Stream.of(values()).filter(s -> s.getStatus().equals(status))
                .findFirst()
                .orElseThrow(() -> new OrderingDomainException("Invalid name for StockUpdateStatus: %s".formatted(status)));
    }

}
