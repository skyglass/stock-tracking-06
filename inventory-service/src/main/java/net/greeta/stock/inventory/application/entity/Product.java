package net.greeta.stock.inventory.application.entity;

import jakarta.persistence.Convert;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.time.Instant;

@Data
public class Product {

    @Id
    private Integer id;
    @Version
    private Integer version;
    private String description;
    private Integer availableQuantity;

}
