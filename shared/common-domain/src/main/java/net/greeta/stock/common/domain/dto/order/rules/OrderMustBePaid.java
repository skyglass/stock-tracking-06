package net.greeta.stock.common.domain.dto.order.rules;

import net.greeta.stock.common.domain.dto.order.OrderStatus;
import net.greeta.stock.common.domain.dto.order.base.BusinessRule;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderMustBePaid implements BusinessRule {
  private final OrderStatus currentStatus;

  @Override
  public boolean broken() {
    return !OrderStatus.Paid.equals(currentStatus);
  }

  @Override
  public String message() {
    return "It's not possible to ship order that's not paid. The status of the order is %s.".formatted(currentStatus);
  }
}
