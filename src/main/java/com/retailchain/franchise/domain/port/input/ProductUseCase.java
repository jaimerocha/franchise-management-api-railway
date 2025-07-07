package com.retailchain.franchise.domain.port.input;

import com.retailchain.franchise.application.dto.StockReportDto;
import com.retailchain.franchise.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductUseCase {
    Mono<Product> addProductToBranch(Long branchId, Product product);
    Mono<Void> deleteProductFromBranch(Long productId);
    Mono<Product> updateProductStock(Long productId, Integer newStock);
    Mono<Product> updateProductName(Long productId, String name);
    Flux<StockReportDto> getMaxStockProductsByFranchise(Long franchiseId);
}