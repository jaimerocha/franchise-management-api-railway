package com.retailchain.franchise.domain.port.input;

import com.retailchain.franchise.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchUseCase {
    Mono<Branch> addBranchToFranchise(Long franchiseId, Branch branch);
    Mono<Branch> updateBranchName(Long id, String name);
    Flux<Branch> getBranchesByFranchiseId(Long franchiseId);
}