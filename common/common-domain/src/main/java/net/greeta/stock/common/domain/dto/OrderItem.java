package net.greeta.stock.common.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderItem {
  @NotNull
  private final UUID productId;
  @NotNull
  private final Integer quantity;
}
