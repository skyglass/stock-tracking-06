package net.greeta.stock.order.common.service;


import net.greeta.stock.common.domain.dto.order.PurchaseOrderDto;

public interface OrderEventListener {

    void emitOrderCreated(PurchaseOrderDto dto);

}
