package dev.digitalfoundries.bog_standard_backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.digitalfoundries.bog_standard_backend.controller.ProductController;
import dev.digitalfoundries.bog_standard_backend.dto.ProductDto;
import dev.digitalfoundries.bog_standard_backend.exception.DuplicateSkuException;
import dev.digitalfoundries.bog_standard_backend.security.service.CustomUserDetailsService;
import dev.digitalfoundries.bog_standard_backend.security.util.JwtUtil;
import dev.digitalfoundries.bog_standard_backend.service.ProductService;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({GlobalExceptionHandler.class,
        ValidationAutoConfiguration.class,
        GlobalExceptionHandlerTest.TestJwtUtilConfig.class,
        GlobalExceptionHandlerTest.ValidatorConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @TestConfiguration
    static class TestJwtUtilConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return mock(JwtUtil.class); // mock for JwtAuthFilter dependencies
        }
    }
    @TestConfiguration
    static class ValidatorConfig {
        @Bean
        public Validator validator() {
            return Validation.buildDefaultValidatorFactory().getValidator();
        }
    }
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void whenInvalidInput_thenReturns400AndValidationMessages() throws Exception {
        ProductDto invalidProduct = new ProductDto(); // missing required fields

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void whenDuplicateSku_thenReturns409Conflict() throws Exception {
        ProductDto dto = validProduct();
        when(productService.createProduct(any())).thenThrow(
                new DuplicateSkuException("SKU already exists", new DataIntegrityViolationException("test"))
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("SKU already exists"));
    }

    @Test
    public void whenOptimisticLock_thenReturns409Conflict() throws Exception {
        ProductDto dto = validProduct();
        when(productService.updateWithRetry(any())).thenThrow(new OptimisticLockException());

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Could not acquire lock to update the product."));
    }

    @Test
    public void whenUnexpectedException_thenReturns500() throws Exception {
        ProductDto dto = validProduct();
        when(productService.createProduct(any())).thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred: Boom"));
    }

    private ProductDto validProduct() {
        return new ProductDto(
                1L,
                "SKU123",
                "Test Product",
                "Test Description",
                "Category/Sub",
                new BigDecimal("19.99"),
                "true"
        );
    }
}
