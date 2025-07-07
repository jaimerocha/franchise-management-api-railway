package com.retailchain.franchise.application.service;

import com.retailchain.franchise.domain.exception.ResourceNotFoundException;
import com.retailchain.franchise.domain.model.Branch;
import com.retailchain.franchise.domain.port.input.BranchUseCase;
import com.retailchain.franchise.domain.port.output.BranchRepository;
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
public class BranchService implements BranchUseCase {
    
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;
    
    @Override
    public Mono<Branch> addBranchToFranchise(Long franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise", franchiseId)))
            .flatMap(franchise -> {
                branch.setFranchiseId(franchiseId);
                branch.setCreatedAt(LocalDateTime.now());
                branch.setUpdatedAt(LocalDateTime.now());
                return branchRepository.save(branch);
            })
            .doOnSuccess(saved -> log.info("Added branch {} to franchise {}", saved.getId(), franchiseId));
    }
    
    @Override
    public Mono<Branch> updateBranchName(Long id, String name) {
        return branchRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Branch", id)))
            .map(branch -> {
                branch.setName(name);
                branch.setUpdatedAt(LocalDateTime.now());
                return branch;
            })
            .flatMap(branchRepository::update)
            .doOnSuccess(updated -> log.info("Updated branch {} name to: {}", id, name));
    }
    
    @Override
    public Flux<Branch> getBranchesByFranchiseId(Long franchiseId) {
        return branchRepository.findByFranchiseId(franchiseId)
            .doOnComplete(() -> log.debug("Retrieved branches for franchise: {}", franchiseId));
    }
}