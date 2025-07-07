package com.retailchain.franchise.domain.port.input;

import com.retailchain.franchise.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseUseCase {
    Mono<Franchise> createFranchise(Franchise franchise);
    Mono<Franchise> updateFranchiseName(Long id, String name);
    Mono<Franchise> getFranchiseById(Long id);
    Flux<Franchise> getAllFranchises();
}