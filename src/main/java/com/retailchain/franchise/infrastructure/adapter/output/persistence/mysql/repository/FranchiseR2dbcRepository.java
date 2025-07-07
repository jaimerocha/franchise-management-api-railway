package com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.repository;

import com.retailchain.franchise.infrastructure.adapter.output.persistence.mysql.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranchiseR2dbcRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
}