package com.retailchain.franchise.infrastructure.adapter.input.rest;

import com.retailchain.franchise.application.dto.ProductDto;
import com.retailchain.franchise.application.dto.StockReportDto;
import com.retailchain.franchise.application.dto.UpdateNameRequest;
import com.retailchain.franchise.application.dto.UpdateStockRequest;
import com.retailchain.franchise.domain.model.Product;
import com.retailchain.franchise.domain.port.input.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private ProductUseCase productUseCase;
    
    private Product testProduct;
    private StockReportDto testStockReport;
    
    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testStockReport = StockReportDto.builder()
                .productId(1L)
                .productName("Test Product")
                .stock(100)
                .branchId(1L)
                .branchName("Test Branch")
                .franchiseId(1L)
                .franchiseName("Test Franchise")
                .build();
    }
    
    @Test
    void addProduct_Success() {
        // Given
        ProductDto requestDto = ProductDto.builder()
                .name("New Product")
                .stock(50)
                .build();
        
        when(productUseCase.addProductToBranch(eq(1L), any(Product.class)))
                .thenReturn(Mono.just(testProduct));
        
        // When & Then
        webTestClient.post()
                .uri("/api/v1/branches/1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.stock").isEqualTo(100);
    }
    
    @Test
    void addProduct_ValidationError_NegativeStock() {
        // Given
        ProductDto requestDto = ProductDto.builder()
                .name("New Product")
                .stock(-10)  // Invalid: negative stock
                .build();
        
        // When & Then
        webTestClient.post()
                .uri("/api/v1/branches/1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }
    
    @Test
    void deleteProduct_Success() {
        // Given
        when(productUseCase.deleteProductFromBranch(1L))
                .thenReturn(Mono.empty());
        
        // When & Then
        webTestClient.delete()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }
    
    @Test
    void updateProductStock_Success() {
        // Given
        UpdateStockRequest request = new UpdateStockRequest(150);
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(150)
                .branchId(1L)
                .createdAt(testProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(productUseCase.updateProductStock(eq(1L), eq(150)))
                .thenReturn(Mono.just(updatedProduct));
        
        // When & Then
        webTestClient.patch()
                .uri("/api/v1/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(150);
    }
    
    @Test
    void updateProductName_Success() {
        // Given
        UpdateNameRequest request = new UpdateNameRequest("Updated Product");
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .stock(100)
                .branchId(1L)
                .createdAt(testProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(productUseCase.updateProductName(eq(1L), eq("Updated Product")))
                .thenReturn(Mono.just(updatedProduct));
        
        // When & Then
        webTestClient.patch()
                .uri("/api/v1/products/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Product");
    }
    
    @Test
    void getMaxStockProducts_Success() {
        // Given
        StockReportDto report2 = StockReportDto.builder()
                .productId(2L)
                .productName("Product 2")
                .stock(200)
                .branchId(2L)
                .branchName("Branch 2")
                .franchiseId(1L)
                .franchiseName("Test Franchise")
                .build();
        
        when(productUseCase.getMaxStockProductsByFranchise(1L))
                .thenReturn(Flux.just(testStockReport, report2));
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises/1/max-stock-products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].productId").isEqualTo(1)
                .jsonPath("$[0].productName").isEqualTo("Test Product")
                .jsonPath("$[0].stock").isEqualTo(100)
                .jsonPath("$[0].branchName").isEqualTo("Test Branch")
                .jsonPath("$[1].productId").isEqualTo(2)
                .jsonPath("$[1].stock").isEqualTo(200);
    }
    
    @Test
    void getMaxStockProducts_EmptyList() {
        // Given
        when(productUseCase.getMaxStockProductsByFranchise(999L))
                .thenReturn(Flux.empty());
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises/999/max-stock-products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }
}