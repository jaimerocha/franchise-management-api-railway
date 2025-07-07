package com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.adapter;

import com.retailchain.franchise.domain.model.Product;
import com.retailchain.franchise.domain.port.output.ProductRepository;
import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.entity.ProductEntity;
import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.repository.ProductR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {
    
    private final ProductR2dbcRepository repository;
    
    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = toEntity(product);
        return repository.save(entity).map(this::toDomain);
    }
    
    @Override
    public Mono<Product> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }
    
    @Override
    public Flux<Product> findByBranchId(Long branchId) {
        return repository.findByBranchId(branchId).map(this::toDomain);
    }
    
    @Override
    public Mono<Product> update(Product product) {
        ProductEntity entity = toEntity(product);
        return repository.save(entity).map(this::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
    
    @Override
    public Flux<Product> findProductsWithMaxStockByBranches(Flux<Long> branchIds) {
        return repository.findProductsWithMaxStockByBranches(branchIds).map(this::toDomain);
    }
    
    private ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
    
    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .stock(entity.getStock())
                .branchId(entity.getBranchId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}