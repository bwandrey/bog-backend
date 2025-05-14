package dev.digitalfoundries.bog_standard_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.digitalfoundries.bog_standard_backend.dto.ProductDto;
import dev.digitalfoundries.bog_standard_backend.security.service.CustomUserDetailsService;
import dev.digitalfoundries.bog_standard_backend.security.util.JwtUtil;
import dev.digitalfoundries.bog_standard_backend.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestJwtUtilConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @TestConfiguration
    static class TestJwtUtilConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return mock(JwtUtil.class); // Mockito mock or a dummy implementation
        }
    }
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomUserDetailsService userDetailsService;
    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private final ProductDto sampleProduct = new ProductDto(
            1L, "SKU123", "Product A", "Description", "Category/Sub", new BigDecimal("19.99"), "true"
    );

    @Test
    public void getAllProducts_shouldReturnPage() throws Exception {
        Page<ProductDto> page = new PageImpl<>(List.of(sampleProduct));
        when(productService.getFilteredProducts(
                any(), any(), any(), any(), any(), any(), any(Pageable.class))
        ).thenReturn(page);

        mockMvc.perform(get("/api/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sku").value("SKU123"));
    }

    @Test
    public  void getProductById_whenFound_returnsProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product A"));
    }

    @Test
    public void getProductById_whenNotFound_returns404() throws Exception {
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public  void createProduct_shouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(ProductDto.class))).thenReturn(sampleProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU123"));
    }

    @Test
    public  void updateProduct_whenFound_shouldReturnUpdated() throws Exception {
        when(productService.updateWithRetry(any(ProductDto.class))).thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU123"));
    }

    @Test
    public  void updateProduct_whenNotFound_shouldReturn404() throws Exception {
        when(productService.updateWithRetry(any(ProductDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteProduct_whenFound_returns204() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public  void deleteProduct_whenNotFound_returns404() throws Exception {
        when(productService.deleteProduct(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
