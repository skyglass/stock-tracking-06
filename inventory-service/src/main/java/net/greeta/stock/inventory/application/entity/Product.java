package net.greeta.stock.inventory.application.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data
public class Product {

    @Id
    private Integer id;
    private String description;
    private Integer availableQuantity;

}
