package com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.repository;

import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BranchR2dbcRepository extends ReactiveCrudRepository<BranchEntity, Long> {
    Flux<BranchEntity> findByFranchiseId(Long franchiseId);
}