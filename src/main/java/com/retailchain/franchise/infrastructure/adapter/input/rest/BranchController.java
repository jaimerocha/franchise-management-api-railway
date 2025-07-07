package com.retailchain.franchise.infrastructure.adapter.input.rest;

import com.retailchain.franchise.application.dto.BranchDto;
import com.retailchain.franchise.application.dto.UpdateNameRequest;
import com.retailchain.franchise.domain.model.Branch;
import com.retailchain.franchise.domain.port.input.BranchUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BranchController {
    
    private final BranchUseCase branchUseCase;
    
    @PostMapping("/franchises/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BranchDto> addBranch(
            @PathVariable Long franchiseId,
            @Valid @RequestBody BranchDto branchDto) {
        log.info("Adding branch to franchise: {}", franchiseId);
        Branch branch = Branch.builder()
                .name(branchDto.getName())
                .build();
        
        return branchUseCase.addBranchToFranchise(franchiseId, branch)
                .map(this::toDto);
    }
    
    @PatchMapping("/branches/{id}/name")
    public Mono<BranchDto> updateBranchName(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNameRequest request) {
        log.info("Updating branch {} name to: {}", id, request.getName());
        return branchUseCase.updateBranchName(id, request.getName())
                .map(this::toDto);
    }
    
    @GetMapping("/franchises/{franchiseId}/branches")
    public Flux<BranchDto> getBranchesByFranchise(@PathVariable Long franchiseId) {
        log.info("Getting branches for franchise: {}", franchiseId);
        return branchUseCase.getBranchesByFranchiseId(franchiseId)
                .map(this::toDto);
    }
    
    private BranchDto toDto(Branch branch) {
        return BranchDto.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}