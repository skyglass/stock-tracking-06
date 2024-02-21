package net.greeta.stock.order;

import net.greeta.stock.common.domain.dto.order.OrderCreateRequest;

public class TestDataUtil {

    public static OrderCreateRequest toRequest(int customerId, int productId, int unitPrice, int quantity) {
        return OrderCreateRequest.builder()
                                      .unitPrice(unitPrice)
                                      .quantity(quantity)
                                      .customerId(customerId)
                                      .productId(productId)
                                      .build();
    }

}
