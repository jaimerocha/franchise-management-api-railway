package com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.repository;

import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductR2dbcRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Flux<ProductEntity> findByBranchId(Long branchId);
    
    @Query("""
        SELECT p.* FROM products p
        INNER JOIN (
            SELECT branch_id, MAX(stock) as max_stock
            FROM products
            WHERE branch_id IN (:branchIds)
            GROUP BY branch_id
        ) max_products
        ON p.branch_id = max_products.branch_id 
        AND p.stock = max_products.max_stock
        """)
    Flux<ProductEntity> findProductsWithMaxStockByBranches(Flux<Long> branchIds);
}