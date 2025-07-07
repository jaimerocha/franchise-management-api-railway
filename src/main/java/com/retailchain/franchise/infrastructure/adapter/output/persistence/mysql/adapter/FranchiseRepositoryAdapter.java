package com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.adapter;

import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.port.output.FranchiseRepository;
import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.entity.FranchiseEntity;
import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.repository.FranchiseR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseRepositoryAdapter implements FranchiseRepository {
    
    private final FranchiseR2dbcRepository repository;
    
    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = toEntity(franchise);
        return repository.save(entity).map(this::toDomain);
    }
    
    @Override
    public Mono<Franchise> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }
    
    @Override
    public Flux<Franchise> findAll() {
        return repository.findAll().map(this::toDomain);
    }
    
    @Override
    public Mono<Franchise> update(Franchise franchise) {
        FranchiseEntity entity = toEntity(franchise);
        return repository.save(entity).map(this::toDomain);
    }
    
    private FranchiseEntity toEntity(Franchise franchise) {
        return FranchiseEntity.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .createdAt(franchise.getCreatedAt())
                .updatedAt(franchise.getUpdatedAt())
                .build();
    }
    
    private Franchise toDomain(FranchiseEntity entity) {
        return Franchise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}