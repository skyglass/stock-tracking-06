package net.greeta.stock.order.common.service;

import net.greeta.stock.order.common.dto.PurchaseOrderDto;

public interface OrderEventListener {

    void emitOrderCreated(PurchaseOrderDto dto);

}
