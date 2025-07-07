package com.retailchain.franchise.infrastructure.adapter.input.rest;

import com.retailchain.franchise.application.dto.ProductDto;
import com.retailchain.franchise.application.dto.StockReportDto;
import com.retailchain.franchise.application.dto.UpdateNameRequest;
import com.retailchain.franchise.application.dto.UpdateStockRequest;
import com.retailchain.franchise.domain.model.Product;
import com.retailchain.franchise.domain.port.input.ProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductUseCase productUseCase;
    
    @PostMapping("/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> addProduct(
            @PathVariable Long branchId,
            @Valid @RequestBody ProductDto productDto) {
        log.info("Adding product to branch: {}", branchId);
        Product product = Product.builder()
                .name(productDto.getName())
                .stock(productDto.getStock())
                .build();
        
        return productUseCase.addProductToBranch(branchId, product)
                .map(this::toDto);
    }
    
    @DeleteMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long productId) {
        log.info("Deleting product: {}", productId);
        return productUseCase.deleteProductFromBranch(productId);
    }
    
    @PatchMapping("/products/{productId}/stock")
    public Mono<ProductDto> updateProductStock(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockRequest request) {
        log.info("Updating product {} stock to: {}", productId, request.getStock());
        return productUseCase.updateProductStock(productId, request.getStock())
                .map(this::toDto);
    }
    
    @PatchMapping("/products/{productId}/name")
    public Mono<ProductDto> updateProductName(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateNameRequest request) {
        log.info("Updating product {} name to: {}", productId, request.getName());
        return productUseCase.updateProductName(productId, request.getName())
                .map(this::toDto);
    }
    
    @GetMapping("/franchises/{franchiseId}/max-stock-products")
    public Flux<StockReportDto> getMaxStockProducts(@PathVariable Long franchiseId) {
        log.info("Getting max stock products for franchise: {}", franchiseId);
        return productUseCase.getMaxStockProductsByFranchise(franchiseId);
    }
    
    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}