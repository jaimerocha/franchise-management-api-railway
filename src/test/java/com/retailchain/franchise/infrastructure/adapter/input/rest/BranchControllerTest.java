package com.retailchain.franchise.infrastructure.adapter.input.rest;

import com.retailchain.franchise.application.dto.BranchDto;
import com.retailchain.franchise.application.dto.UpdateNameRequest;
import com.retailchain.franchise.domain.model.Branch;
import com.retailchain.franchise.domain.port.input.BranchUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(BranchController.class)
class BranchControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private BranchUseCase branchUseCase;
    
    private Branch testBranch;
    
    @BeforeEach
    void setUp() {
        testBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void addBranch_Success() {
        // Given
        BranchDto requestDto = BranchDto.builder()
                .name("New Branch")
                .build();
        
        when(branchUseCase.addBranchToFranchise(eq(1L), any(Branch.class)))
                .thenReturn(Mono.just(testBranch));
        
        // When & Then
        webTestClient.post()
                .uri("/api/v1/franchises/1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Branch")
                .jsonPath("$.franchiseId").isEqualTo(1);
    }
    
    @Test
    void addBranch_ValidationError() {
        // Given
        BranchDto requestDto = BranchDto.builder()
                .name("AB")  // Invalid: too short
                .build();
        
        // When & Then
        webTestClient.post()
                .uri("/api/v1/franchises/1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }
    
    @Test
    void updateBranchName_Success() {
        // Given
        UpdateNameRequest request = new UpdateNameRequest("Updated Branch");
        Branch updatedBranch = Branch.builder()
                .id(1L)
                .name("Updated Branch")
                .franchiseId(1L)
                .createdAt(testBranch.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(branchUseCase.updateBranchName(eq(1L), eq("Updated Branch")))
                .thenReturn(Mono.just(updatedBranch));
        
        // When & Then
        webTestClient.patch()
                .uri("/api/v1/branches/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Branch");
    }
    
    @Test
    void getBranchesByFranchise_Success() {
        // Given
        Branch branch2 = Branch.builder()
                .id(2L)
                .name("Branch 2")
                .franchiseId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(branchUseCase.getBranchesByFranchiseId(1L))
                .thenReturn(Flux.just(testBranch, branch2));
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises/1/branches")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Branch")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("Branch 2");
    }
    
    @Test
    void getBranchesByFranchise_EmptyList() {
        // Given
        when(branchUseCase.getBranchesByFranchiseId(999L))
                .thenReturn(Flux.empty());
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises/999/branches")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }
}