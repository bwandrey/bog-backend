package dev.digitalfoundries.bog_standard_backend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class ProductEntity extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Version
    Long version;

    @Column(unique = true,nullable = false)
    String sku;

    String name;
    String description;
    String categoryPath;

    @Column(precision = 10, scale = 2)
    BigDecimal price;
    String available;

}
