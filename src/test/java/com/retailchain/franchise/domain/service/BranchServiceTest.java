package com.retailchain.franchise.domain.service;

import com.retailchain.franchise.application.service.BranchService;
import com.retailchain.franchise.domain.exception.ResourceNotFoundException;
import com.retailchain.franchise.domain.model.Branch;
import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.port.output.BranchRepository;
import com.retailchain.franchise.domain.port.output.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {
    
    @Mock
    private BranchRepository branchRepository;
    
    @Mock
    private FranchiseRepository franchiseRepository;
    
    @InjectMocks
    private BranchService branchService;
    
    private Branch testBranch;
    private Franchise testFranchise;
    
    @BeforeEach
    void setUp() {
        testFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void addBranchToFranchise_Success() {
        // Given
        Branch newBranch = Branch.builder()
                .name("New Branch")
                .build();
        
        Branch savedBranch = Branch.builder()
                .id(2L)
                .name("New Branch")
                .franchiseId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(testFranchise));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(savedBranch));
        
        // When & Then
        StepVerifier.create(branchService.addBranchToFranchise(1L, newBranch))
                .expectNext(savedBranch)
                .verifyComplete();
        
        verify(franchiseRepository).findById(1L);
        verify(branchRepository).save(any(Branch.class));
    }
    
    @Test
    void addBranchToFranchise_FranchiseNotFound() {
        // Given
        Branch newBranch = Branch.builder()
                .name("New Branch")
                .build();
        
        when(franchiseRepository.findById(999L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(branchService.addBranchToFranchise(999L, newBranch))
                .expectError(ResourceNotFoundException.class)
                .verify();
        
        verify(franchiseRepository).findById(999L);
        verify(branchRepository, never()).save(any());
    }
    
    @Test
    void updateBranchName_Success() {
        // Given
        String newName = "Updated Branch";
        Branch updatedBranch = Branch.builder()
                .id(1L)
                .name(newName)
                .franchiseId(1L)
                .createdAt(testBranch.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(branchRepository.findById(1L)).thenReturn(Mono.just(testBranch));
        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(updatedBranch));
        
        // When & Then
        StepVerifier.create(branchService.updateBranchName(1L, newName))
                .expectNext(updatedBranch)
                .verifyComplete();
        
        verify(branchRepository).findById(1L);
        verify(branchRepository).update(any(Branch.class));
    }
    
    @Test
    void updateBranchName_NotFound() {
        // Given
        when(branchRepository.findById(999L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(branchService.updateBranchName(999L, "New Name"))
                .expectError(ResourceNotFoundException.class)
                .verify();
        
        verify(branchRepository).findById(999L);
        verify(branchRepository, never()).update(any());
    }
    
    @Test
    void getBranchesByFranchiseId_Success() {
        // Given
        Branch branch2 = Branch.builder()
                .id(2L)
                .name("Branch 2")
                .franchiseId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(branchRepository.findByFranchiseId(1L))
                .thenReturn(Flux.just(testBranch, branch2));
        
        // When & Then
        StepVerifier.create(branchService.getBranchesByFranchiseId(1L))
                .expectNext(testBranch)
                .expectNext(branch2)
                .verifyComplete();
        
        verify(branchRepository).findByFranchiseId(1L);
    }
    
    @Test
    void getBranchesByFranchiseId_EmptyList() {
        // Given
        when(branchRepository.findByFranchiseId(999L))
                .thenReturn(Flux.empty());
        
        // When & Then
        StepVerifier.create(branchService.getBranchesByFranchiseId(999L))
                .verifyComplete();
        
        verify(branchRepository).findByFranchiseId(999L);
    }
}