package net.greeta.stock.common.domain.valueobject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.greeta.stock.common.domain.exception.OrderingDomainException;
import org.springframework.lang.NonNull;

import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderStatus {
    AwaitingConfirmation("AwaitingConfirmation"),
    Confirmed("OrderConfirmed"),
    Cancelled("Cancelled");

    @Getter
    private final String status;

    public static OrderStatus of(@NonNull String status) {
        return Stream.of(values()).filter(s -> s.getStatus().equals(status))
                .findFirst()
                .orElseThrow(() -> new OrderingDomainException("Invalid name for OrderStatus: %s".formatted(status)));
    }

}
