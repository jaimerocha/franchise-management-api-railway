package com.retailchain.franchise.domain.port.output;

import com.retailchain.franchise.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(Long id);
    Flux<Product> findByBranchId(Long branchId);
    Mono<Product> update(Product product);
    Mono<Void> deleteById(Long id);
    Flux<Product> findProductsWithMaxStockByBranches(Flux<Long> branchIds);
}