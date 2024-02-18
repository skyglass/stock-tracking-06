package net.greeta.stock.shipping.application.mapper;


import net.greeta.stock.shipping.application.entity.Shipment;
import net.greeta.stock.shipping.common.dto.ScheduleRequest;
import net.greeta.stock.shipping.common.dto.ShipmentDto;

public class EntityDtoMapper {

    public static Shipment toShipment(ScheduleRequest request) {
        return Shipment.builder()
                       .customerId(request.customerId())
                       .orderId(request.orderId())
                       .productId(request.productId())
                       .quantity(request.quantity())
                       .build();
    }

    public static ShipmentDto toDto(Shipment shipment) {
        return ShipmentDto.builder()
                          .shipmentId(shipment.getId())
                          .customerId(shipment.getCustomerId())
                          .quantity(shipment.getQuantity())
                          .productId(shipment.getProductId())
                          .orderId(shipment.getOrderId())
                          .status(shipment.getStatus())
                          .deliveryDate(shipment.getDeliveryDate())
                          .build();
    }

}
