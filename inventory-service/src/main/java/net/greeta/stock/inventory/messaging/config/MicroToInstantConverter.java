package net.greeta.stock.inventory.messaging.config;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Instant;

public class MicroToInstantConverter extends StdConverter<Instant, Long> {
  public Long convert(final Instant value) {
    return Long.valueOf(value.toEpochMilli());
  }
}
