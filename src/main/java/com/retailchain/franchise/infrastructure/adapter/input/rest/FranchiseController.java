package com.retailchain.franchise.infrastructure.adapter.input.rest;

import com.retailchain.franchise.application.dto.FranchiseDto;
import com.retailchain.franchise.application.dto.UpdateNameRequest;
import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.port.input.FranchiseUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/franchises")
@RequiredArgsConstructor
public class FranchiseController {
    
    private final FranchiseUseCase franchiseUseCase;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseDto> createFranchise(@Valid @RequestBody FranchiseDto franchiseDto) {
        log.info("Creating new franchise: {}", franchiseDto.getName());
        Franchise franchise = Franchise.builder()
                .name(franchiseDto.getName())
                .build();
        
        return franchiseUseCase.createFranchise(franchise)
                .map(this::toDto);
    }
    
    @PatchMapping("/{id}/name")
    public Mono<FranchiseDto> updateFranchiseName(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNameRequest request) {
        log.info("Updating franchise {} name to: {}", id, request.getName());
        return franchiseUseCase.updateFranchiseName(id, request.getName())
                .map(this::toDto);
    }
    
    @GetMapping("/{id}")
    public Mono<FranchiseDto> getFranchise(@PathVariable Long id) {
        log.info("Getting franchise: {}", id);
        return franchiseUseCase.getFranchiseById(id)
                .map(this::toDto);
    }
    
    @GetMapping
    public Flux<FranchiseDto> getAllFranchises() {
        log.info("Getting all franchises");
        return franchiseUseCase.getAllFranchises()
                .map(this::toDto);
    }
    
    private FranchiseDto toDto(Franchise franchise) {
        return FranchiseDto.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .createdAt(franchise.getCreatedAt())
                .updatedAt(franchise.getUpdatedAt())
                .build();
    }
}