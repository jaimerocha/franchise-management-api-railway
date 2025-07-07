package com.retailchain.franchise.domain.port.output;

import com.retailchain.franchise.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(Long id);
    Flux<Branch> findByFranchiseId(Long franchiseId);
    Mono<Branch> update(Branch branch);
}