package net.greeta.stock.inventory.application.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import java.time.Instant;

@Data
public class Product {

    @Id
    private Integer id;
    @Version
    private Instant version;
    private String description;
    private Integer availableQuantity;

}
