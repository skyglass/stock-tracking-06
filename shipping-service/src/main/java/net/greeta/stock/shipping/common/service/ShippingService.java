package net.greeta.stock.shipping.common.service;

import net.greeta.stock.shipping.common.dto.ScheduleRequest;
import net.greeta.stock.shipping.common.dto.ShipmentDto;
import reactor.core.publisher.Mono;

public interface ShippingService {

    Mono<ShipmentDto> schedule(ScheduleRequest request);

}

