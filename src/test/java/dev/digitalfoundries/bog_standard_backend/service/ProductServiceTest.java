package dev.digitalfoundries.bog_standard_backend.service;

import dev.digitalfoundries.bog_standard_backend.dto.ProductDto;
import dev.digitalfoundries.bog_standard_backend.entity.ProductEntity;
import dev.digitalfoundries.bog_standard_backend.exception.DuplicateSkuException;
import dev.digitalfoundries.bog_standard_backend.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService productService;

    private final ProductEntity entity = new ProductEntity();
    private final ProductDto dto = new ProductDto(1L, "SKU123", "Test", "Desc", "Cat", new BigDecimal("9.99"), "true");

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        entity.setId(1L);
        entity.setSku("SKU123");
        entity.setName("Test");
        entity.setDescription("Desc");
        entity.setCategoryPath("Cat");
        entity.setPrice(new BigDecimal("9.99"));
        entity.setAvailable("true");
    }

    @Test
    void getAllProducts_shouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(entity));

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("SKU123", result.get(0).getSku());
    }

    @Test
    void createProduct_shouldSaveEntity() {
        when(productRepository.save(any())).thenReturn(entity);

        ProductDto result = productService.createProduct(dto);

        assertEquals("SKU123", result.getSku());
    }

    @Test
    void createProduct_shouldThrowDuplicateSku() {
        when(productRepository.save(any())).thenThrow(new DuplicateSkuException("duplicate", new DataIntegrityViolationException("duplicate")));

        assertThrows(DuplicateSkuException.class, () -> productService.createProduct(dto));
    }

    @Test
    void getProductById_shouldReturnDto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<ProductDto> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("SKU123", result.get().getSku());
    }

    @Test
    void getProductById_shouldReturnEmpty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertTrue(productService.getProductById(1L).isEmpty());
    }

    @Test
    void getProductBySku_shouldReturnDto() {
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(entity));

        Optional<ProductDto> result = productService.getProductBySku("SKU123");

        assertTrue(result.isPresent());
        assertEquals("SKU123", result.get().getSku());
    }

    @Test
    void deleteProduct_whenExists_shouldReturnTrue() {
        doNothing().when(productRepository).deleteById(1L);

        assertTrue(productService.deleteProduct(1L));
    }

    @Test
    void deleteProduct_whenNotFound_shouldReturnFalse() {
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(productRepository).deleteById(1L);

        assertFalse(productService.deleteProduct(1L));
    }

    @Test
    void updateWithRetry_shouldSucceed() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productRepository.save(any())).thenReturn(entity);

        Optional<ProductDto> result = productService.updateWithRetry(dto);

        assertTrue(result.isPresent());
    }

    @Test
    void updateWithRetry_shouldRetryOnOptimisticLock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(productRepository.save(any()))
                .thenThrow(new OptimisticLockException())
                .thenThrow(new OptimisticLockException())
                .thenReturn(entity);

        Optional<ProductDto> result = productService.updateWithRetry(dto);

        assertTrue(result.isPresent());
    }

    @Test
    void getFilteredProducts_shouldBuildQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductEntity> entityPage = new PageImpl<>(List.of(entity));

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);

        Page<ProductDto> result = productService.getFilteredProducts(
                "SKU123", null, null, null, null, null, pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("SKU123", result.getContent().get(0).getSku());
    }
}
