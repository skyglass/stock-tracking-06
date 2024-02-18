package net.greeta.stock.payment.common.dto;

import net.greeta.stock.common.messages.payment.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PaymentDto(UUID paymentId,
                         UUID orderId,
                         Integer customerId,
                         Integer amount,
                         PaymentStatus status) {
}
