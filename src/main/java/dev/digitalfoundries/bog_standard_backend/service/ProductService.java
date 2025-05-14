package dev.digitalfoundries.bog_standard_backend.service;

import dev.digitalfoundries.bog_standard_backend.dto.ProductDto;
import dev.digitalfoundries.bog_standard_backend.entity.ProductEntity;
import dev.digitalfoundries.bog_standard_backend.exception.DuplicateSkuException;
import dev.digitalfoundries.bog_standard_backend.repository.ProductRepository;
import dev.digitalfoundries.bog_standard_backend.transformer.ProductTransformer;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<ProductDto> getAllProducts() {

        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(ProductTransformer::toDto).toList();
    }


    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        try {
            ProductEntity saved = productRepository.save(ProductTransformer.toEntity(productDto));
            log.info("Product created with id: " + saved.getId());
            return ProductTransformer.toDto(saved);
        }catch (DuplicateSkuException e) {
            log.error(e.getMessage());
            throw new DuplicateSkuException("SKU already exists: " + productDto.getSku(), e);
        }

    }


    public Optional<ProductDto> getProductById(Long id) {
        Optional<ProductEntity> product = productRepository.findById(id);
        log.info("Product found with id: " + id);
        return product.map(ProductTransformer::toDto);
    }

    public Optional<ProductDto> getProductBySku(String sku) {
        log.info("Looking for product with sku:" + sku);
        Optional<ProductEntity> product = productRepository.findBySku(sku);
        log.info("Found product for sku " + sku + " ? " + product.isPresent());
        return product.map(ProductTransformer::toDto);
    }


    @Transactional
    Optional<ProductDto> updateProductOptimistically(ProductDto dto) {
        log.info("Updating product with id: " + dto.getId());
        return productRepository.findById(dto.getId())
                .map(entity -> {
                    ProductTransformer.updateEntityFromDto(dto, entity);
                    log.info("Updated product with id: " + dto.getId());
                    return ProductTransformer.toDto(productRepository.save(entity));
                });
    }
    @Transactional
    public Optional<ProductDto> updateWithRetry(ProductDto dto) {
        log.info("Updating product with id: " + dto.getId());
        final int maxRetries = 3;
        int attempts = 0;

        while (true) {
            try {
                Optional<ProductDto> optionalProduct =  updateProductOptimistically(dto);
                log.info("Updated product with id: " + dto.getId());
                return optionalProduct;
            } catch (OptimisticLockException e) {
                if (++attempts >= maxRetries) throw e;
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }
        }
    }


    public boolean deleteProduct(Long id) {
        log.info("Deleting product with id: " + id);
        try {
            productRepository.deleteById(id);
            log.info("Deleted product with id: " + id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.error(e.getMessage());
            return false;
        }
    }
    public Page<ProductDto> getFilteredProducts(
            String sku, String name, String categoryPath, String available,
            BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable
    ) {
        log.info("Filtering products with sku: " + sku + ", name: " + name + ", categoryPath: " + categoryPath +
                ", available: " + available + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice);

        Specification<ProductEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (sku != null) predicates.add(cb.equal(root.get("sku"), sku));
            if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (categoryPath != null) {
                predicates.add(cb.like(cb.lower(root.get("categoryPath")), "%" + categoryPath.toLowerCase() + "%"));
            }
            if (available != null) predicates.add(cb.equal(root.get("available"), available));

            if (minPrice != null) predicates.add(cb.ge(root.get("price"), minPrice));
            if (maxPrice != null) predicates.add(cb.le(root.get("price"), maxPrice));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        log.info("Executing filtered query with page: " + pageable);
        return productRepository.findAll(spec, pageable)
                .map(ProductTransformer::toDto);
    }

}
