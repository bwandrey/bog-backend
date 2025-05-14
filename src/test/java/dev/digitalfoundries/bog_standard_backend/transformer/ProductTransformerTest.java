package dev.digitalfoundries.bog_standard_backend.transformer;

import dev.digitalfoundries.bog_standard_backend.dto.ProductDto;
import dev.digitalfoundries.bog_standard_backend.entity.ProductEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTransformerTest {

    @Test
    void toDto_shouldMapFieldsCorrectly() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setSku("SKU123");
        entity.setName("Test Name");
        entity.setDescription("Description");
        entity.setCategoryPath("Cat/Path");
        entity.setPrice(new BigDecimal("19.99"));
        entity.setAvailable("true");

        ProductDto dto = ProductTransformer.toDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getSku(), dto.getSku());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getCategoryPath(), dto.getCategoryPath());
        assertEquals(entity.getPrice(), dto.getPrice());
        assertEquals(entity.getAvailable(), dto.getAvailable());
    }

    @Test
    void toEntity_shouldMapFieldsCorrectly() {
        ProductDto dto = new ProductDto();
        dto.setSku("SKU456");
        dto.setName("Another Product");
        dto.setDescription("Nice and shiny");
        dto.setCategoryPath("Electronics/Phones");
        dto.setPrice(new BigDecimal("19.99"));
        dto.setAvailable("false");

        ProductEntity entity = ProductTransformer.toEntity(dto);

        assertEquals(dto.getSku(), entity.getSku());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getCategoryPath(), entity.getCategoryPath());
        assertEquals(dto.getPrice(), entity.getPrice());
        assertEquals(dto.getAvailable(), entity.getAvailable());
    }

    @Test
    void updateEntityFromDto_shouldOnlyUpdateNonNullFields() {
        ProductEntity entity = new ProductEntity();
        entity.setSku("SKU1");
        entity.setName("Original Name");
        entity.setDescription("Old desc");
        entity.setCategoryPath("Old/Path");
        entity.setPrice(new BigDecimal("10.00"));
        entity.setAvailable("true");

        ProductDto dto = new ProductDto();
        dto.setName("Updated Name");
        dto.setPrice(null); // leave unchanged
        dto.setDescription(null); // leave unchanged
        dto.setAvailable("false");

        ProductTransformer.updateEntityFromDto(dto, entity);

        assertEquals("SKU1", entity.getSku()); // unchanged
        assertEquals("Updated Name", entity.getName()); // updated
        assertEquals("Old desc", entity.getDescription()); // unchanged
        assertEquals("Old/Path", entity.getCategoryPath()); // unchanged
        assertEquals(new BigDecimal("10.00"), entity.getPrice()); // unchanged
        assertEquals("false", entity.getAvailable()); // updated
    }
}
