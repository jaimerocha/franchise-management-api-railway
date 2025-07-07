package com.retailchain.franchise.infrastructure.adapter.input.rest;

import com.retailchain.franchise.application.dto.FranchiseDto;
import com.retailchain.franchise.application.dto.UpdateNameRequest;
import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.port.input.FranchiseUseCase;
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

@WebFluxTest(FranchiseController.class)
class FranchiseControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private FranchiseUseCase franchiseUseCase;
    
    private Franchise testFranchise;
    
    @BeforeEach
    void setUp() {
        testFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void createFranchise_Success() {
        // Given
        FranchiseDto requestDto = FranchiseDto.builder()
                .name("New Franchise")
                .build();
        
        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));
        
        // When & Then
        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Franchise");
    }
    
    @Test
    void createFranchise_ValidationError() {
        // Given
        FranchiseDto requestDto = FranchiseDto.builder()
                .name("")  // Invalid: empty name
                .build();
        
        // When & Then
        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isBadRequest();
    }
    
    @Test
    void updateFranchiseName_Success() {
        // Given
        UpdateNameRequest request = new UpdateNameRequest("Updated Franchise");
        Franchise updatedFranchise = Franchise.builder()
                .id(1L)
                .name("Updated Franchise")
                .createdAt(testFranchise.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(franchiseUseCase.updateFranchiseName(eq(1L), eq("Updated Franchise")))
                .thenReturn(Mono.just(updatedFranchise));
        
        // When & Then
        webTestClient.patch()
                .uri("/api/v1/franchises/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Franchise");
    }
    
    @Test
    void getFranchise_Success() {
        // Given
        when(franchiseUseCase.getFranchiseById(1L))
                .thenReturn(Mono.just(testFranchise));
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Test Franchise");
    }
    
    @Test
    void getAllFranchises_Success() {
        // Given
        Franchise franchise2 = Franchise.builder()
                .id(2L)
                .name("Franchise 2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(franchiseUseCase.getAllFranchises())
                .thenReturn(Flux.just(testFranchise, franchise2));
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Test Franchise")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].name").isEqualTo("Franchise 2");
    }
    
    @Test
    void getAllFranchises_EmptyList() {
        // Given
        when(franchiseUseCase.getAllFranchises())
                .thenReturn(Flux.empty());
        
        // When & Then
        webTestClient.get()
                .uri("/api/v1/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }
}