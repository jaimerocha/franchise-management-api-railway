package com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.adapter;

import com.retailchain.franchise.domain.model.Branch;
import com.retailchain.franchise.domain.port.output.BranchRepository;
import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.entity.BranchEntity;
import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.repository.BranchR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepository {
    
    private final BranchR2dbcRepository repository;
    
    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = toEntity(branch);
        return repository.save(entity).map(this::toDomain);
    }
    
    @Override
    public Mono<Branch> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }
    
    @Override
    public Flux<Branch> findByFranchiseId(Long franchiseId) {
        return repository.findByFranchiseId(franchiseId).map(this::toDomain);
    }
    
    @Override
    public Mono<Branch> update(Branch branch) {
        BranchEntity entity = toEntity(branch);
        return repository.save(entity).map(this::toDomain);
    }
    
    private BranchEntity toEntity(Branch branch) {
        return BranchEntity.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
    
    private Branch toDomain(BranchEntity entity) {
        return Branch.builder()
                .id(entity.getId())
                .name(entity.getName())
                .franchiseId(entity.getFranchiseId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}