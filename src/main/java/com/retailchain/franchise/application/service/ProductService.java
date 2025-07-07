package com.retailchain.franchise.application.service;

import com.retailchain.franchise.application.dto.StockReportDto;
import com.retailchain.franchise.domain.exception.ResourceNotFoundException;
import com.retailchain.franchise.domain.model.Product;
import com.retailchain.franchise.domain.port.input.ProductUseCase;
import com.retailchain.franchise.domain.port.output.BranchRepository;
import com.retailchain.franchise.domain.port.output.ProductRepository;
import com.retailchain.franchise.domain.port.output.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;
    
    @Override
    public Mono<Product> addProductToBranch(Long branchId, Product product) {
        return branchRepository.findById(branchId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Branch", branchId)))
            .flatMap(branch -> {
                product.setBranchId(branchId);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                return productRepository.save(product);
            })
            .doOnSuccess(saved -> log.info("Added product {} to branch {}", saved.getId(), branchId));
    }
    
    @Override
    public Mono<Void> deleteProductFromBranch(Long productId) {
        return productRepository.findById(productId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", productId)))
            .flatMap(product -> productRepository.deleteById(productId))
            .doOnSuccess(v -> log.info("Deleted product: {}", productId));
    }
    
    @Override
    public Mono<Product> updateProductStock(Long productId, Integer newStock) {
        return productRepository.findById(productId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", productId)))
            .map(product -> {
                product.setStock(newStock);
                product.setUpdatedAt(LocalDateTime.now());
                return product;
            })
            .flatMap(productRepository::update)
            .doOnSuccess(updated -> log.info("Updated product {} stock to: {}", productId, newStock));
    }
    
    @Override
    public Mono<Product> updateProductName(Long productId, String name) {
        return productRepository.findById(productId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Product", productId)))
            .map(product -> {
                product.setName(name);
                product.setUpdatedAt(LocalDateTime.now());
                return product;
            })
            .flatMap(productRepository::update)
            .doOnSuccess(updated -> log.info("Updated product {} name to: {}", productId, name));
    }
    
    @Override
    public Flux<StockReportDto> getMaxStockProductsByFranchise(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise", franchiseId)))
            .flatMapMany(franchise -> 
                branchRepository.findByFranchiseId(franchiseId)
                    .map(branch -> branch.getId())
                    .collectList()
                    .flatMapMany(branchIds -> 
                        productRepository.findProductsWithMaxStockByBranches(Flux.fromIterable(branchIds))
                            .flatMap(product -> 
                                branchRepository.findById(product.getBranchId())
                                    .map(branch -> StockReportDto.builder()
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .stock(product.getStock())
                                        .branchId(branch.getId())
                                        .branchName(branch.getName())
                                        .franchiseId(franchise.getId())
                                        .franchiseName(franchise.getName())
                                        .build()
                                    )
                            )
                    )
            )
            .doOnComplete(() -> log.debug("Generated stock report for franchise: {}", franchiseId));
    }
}