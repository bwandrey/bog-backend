package dev.digitalfoundries.bog_standard_backend.transformer;

import dev.digitalfoundries.bog_standard_backend.dto.ProductDto;
import dev.digitalfoundries.bog_standard_backend.entity.ProductEntity;

public class ProductTransformer {



    public static ProductDto toDto(ProductEntity productEntity){
        ProductDto productDto = new ProductDto();
        productDto.setId(productEntity.getId());
        productDto.setSku(productEntity.getSku());
        productDto.setDescription(productEntity.getDescription());
        productDto.setName(productEntity.getName());
        productDto.setPrice(productEntity.getPrice());
        productDto.setAvailable(productEntity.getAvailable());
        productDto.setCategoryPath(productEntity.getCategoryPath());
        return productDto;

    }

    public static ProductEntity toEntity(ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setSku(productDto.getSku());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setName(productDto.getName());
        productEntity.setPrice(productDto.getPrice());
        productEntity.setAvailable(productDto.getAvailable());
        productEntity.setCategoryPath(productDto.getCategoryPath());
        return productEntity;

    }

    public static void updateEntityFromDto(ProductDto dto, ProductEntity entity) {
        entity.setSku(dto.getSku() == null ? entity.getSku() : dto.getSku());
        entity.setName(dto.getName() == null ? entity.getName() : dto.getName());
        entity.setDescription(dto.getDescription() == null ? entity.getDescription() : dto.getDescription());
        entity.setCategoryPath(dto.getCategoryPath() == null ? entity.getCategoryPath() : dto.getCategoryPath());
        entity.setPrice(dto.getPrice() == null ? entity.getPrice() : dto.getPrice());
        entity.setAvailable(dto.getAvailable() == null ? entity.getAvailable() : dto.getAvailable());
    }
}
