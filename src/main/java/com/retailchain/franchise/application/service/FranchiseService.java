package com.retailchain.franchise.application.service;

import com.retailchain.franchise.domain.exception.ResourceNotFoundException;
import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.port.input.FranchiseUseCase;
import com.retailchain.franchise.domain.port.output.CacheRepository;
import com.retailchain.franchise.domain.port.output.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseService implements FranchiseUseCase {
    
    private final FranchiseRepository franchiseRepository;
    private final CacheRepository cacheRepository;
    
    private static final String CACHE_KEY_PREFIX = "franchise:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    
    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        franchise.setCreatedAt(LocalDateTime.now());
        franchise.setUpdatedAt(LocalDateTime.now());
        
        return franchiseRepository.save(franchise)
            .doOnSuccess(saved -> log.info("Created franchise with id: {}", saved.getId()))
            .flatMap(saved -> cacheRepository.set(CACHE_KEY_PREFIX + saved.getId(), saved, CACHE_TTL)
                .thenReturn(saved));
    }
    
    @Override
    public Mono<Franchise> updateFranchiseName(Long id, String name) {
        return franchiseRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise", id)))
            .map(franchise -> {
                franchise.setName(name);
                franchise.setUpdatedAt(LocalDateTime.now());
                return franchise;
            })
            .flatMap(franchiseRepository::update)
            .doOnSuccess(updated -> log.info("Updated franchise {} name to: {}", id, name))
            .flatMap(updated -> cacheRepository.delete(CACHE_KEY_PREFIX + id)
                .then(cacheRepository.set(CACHE_KEY_PREFIX + id, updated, CACHE_TTL))
                .thenReturn(updated));
    }
    
    @Override
    public Mono<Franchise> getFranchiseById(Long id) {
        return cacheRepository.get(CACHE_KEY_PREFIX + id, Franchise.class)
            .doOnNext(cached -> log.debug("Cache hit for franchise: {}", id))
            .switchIfEmpty(
                franchiseRepository.findById(id)
                    .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise", id)))
                    .flatMap(franchise -> cacheRepository.set(CACHE_KEY_PREFIX + id, franchise, CACHE_TTL)
                        .thenReturn(franchise))
            );
    }
    
    @Override
    public Flux<Franchise> getAllFranchises() {
        return franchiseRepository.findAll()
            .doOnComplete(() -> log.debug("Retrieved all franchises"));
    }
}