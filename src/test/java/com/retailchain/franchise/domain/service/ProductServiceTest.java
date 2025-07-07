package com.retailchain.franchise.domain.service;

import com.retailchain.franchise.application.service.ProductService;
import com.retailchain.franchise.domain.exception.ResourceNotFoundException;
import com.retailchain.franchise.domain.model.Branch;
import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.model.Product;
import com.retailchain.franchise.domain.port.output.BranchRepository;
import com.retailchain.franchise.domain.port.output.FranchiseRepository;
import com.retailchain.franchise.domain.port.output.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BranchRepository branchRepository;
    
    @Mock
    private FranchiseRepository franchiseRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private Branch testBranch;
    private Franchise testFranchise;
    
    @BeforeEach
    void setUp() {
        testFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
        
        testBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();
        
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void addProductToBranch_Success() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .stock(50)
                .build();
        
        Product savedProduct = Product.builder()
                .id(2L)
                .name("New Product")
                .stock(50)
                .branchId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(branchRepository.findById(1L)).thenReturn(Mono.just(testBranch));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));
        
        // When & Then
        StepVerifier.create(productService.addProductToBranch(1L, newProduct))
                .expectNext(savedProduct)
                .verifyComplete();
        
        verify(branchRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void addProductToBranch_BranchNotFound() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .stock(50)
                .build();
        
        when(branchRepository.findById(999L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(productService.addProductToBranch(999L, newProduct))
                .expectError(ResourceNotFoundException.class)
                .verify();
        
        verify(branchRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }
    
    @Test
    void deleteProductFromBranch_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Mono.just(testProduct));
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(productService.deleteProductFromBranch(1L))
                .verifyComplete();
        
        verify(productRepository).findById(1L);
        verify(productRepository).deleteById(1L);
    }
    
    @Test
    void deleteProductFromBranch_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(productService.deleteProductFromBranch(999L))
                .expectError(ResourceNotFoundException.class)
                .verify();
        
        verify(productRepository).findById(999L);
        verify(productRepository, never()).deleteById(any());
    }
    
    @Test
    void updateProductStock_Success() {
        // Given
        Integer newStock = 200;
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(newStock)
                .branchId(1L)
                .createdAt(testProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(productRepository.findById(1L)).thenReturn(Mono.just(testProduct));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updatedProduct));
        
        // When & Then
        StepVerifier.create(productService.updateProductStock(1L, newStock))
                .expectNext(updatedProduct)
                .verifyComplete();
        
        verify(productRepository).findById(1L);
        verify(productRepository).update(any(Product.class));
    }
    
    @Test
    void updateProductName_Success() {
        // Given
        String newName = "Updated Product";
        Product updatedProduct = Product.builder()
                .id(1L)
                .name(newName)
                .stock(100)
                .branchId(1L)
                .createdAt(testProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(productRepository.findById(1L)).thenReturn(Mono.just(testProduct));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updatedProduct));
        
        // When & Then
        StepVerifier.create(productService.updateProductName(1L, newName))
                .expectNext(updatedProduct)
                .verifyComplete();
        
        verify(productRepository).findById(1L);
        verify(productRepository).update(any(Product.class));
    }
    
    @Test
    void getMaxStockProductsByFranchise_Success() {
        // Given
        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .stock(150)
                .branchId(1L)
                .build();
        
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(testFranchise));
        when(branchRepository.findByFranchiseId(1L)).thenReturn(Flux.just(testBranch));
        when(productRepository.findProductsWithMaxStockByBranches(any()))
                .thenReturn(Flux.just(product2));
        when(branchRepository.findById(1L)).thenReturn(Mono.just(testBranch));
        
        // When & Then
        StepVerifier.create(productService.getMaxStockProductsByFranchise(1L))
                .expectNextMatches(dto -> 
                    dto.getProductId().equals(2L) &&
                    dto.getProductName().equals("Product 2") &&
                    dto.getStock().equals(150) &&
                    dto.getBranchId().equals(1L) &&
                    dto.getBranchName().equals("Test Branch") &&
                    dto.getFranchiseId().equals(1L) &&
                    dto.getFranchiseName().equals("Test Franchise")
                )
                .verifyComplete();
        
        verify(franchiseRepository).findById(1L);
        verify(branchRepository).findByFranchiseId(1L);
    }
    
    @Test
    void getMaxStockProductsByFranchise_FranchiseNotFound() {
        // Given
        when(franchiseRepository.findById(999L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(productService.getMaxStockProductsByFranchise(999L))
                .expectError(ResourceNotFoundException.class)
                .verify();
        
        verify(franchiseRepository).findById(999L);
        verify(branchRepository, never()).findByFranchiseId(any());
    }
}