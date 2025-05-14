package dev.digitalfoundries.bog_standard_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    Long id;

    @NotBlank(message = "SKU is required")
    String sku;
    String name;
    String description;
    String categoryPath;
    BigDecimal price;
    String available;
}
