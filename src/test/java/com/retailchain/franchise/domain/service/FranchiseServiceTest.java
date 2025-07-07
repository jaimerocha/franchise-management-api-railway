package com.retailchain.franchise.domain.service;

import com.retailchain.franchise.application.service.FranchiseService;
import com.retailchain.franchise.domain.exception.ResourceNotFoundException;
import com.retailchain.franchise.domain.model.Franchise;
import com.retailchain.franchise.domain.port.output.CacheRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {
    
    @Mock
    private FranchiseRepository franchiseRepository;
    
    @Mock
    private CacheRepository cacheRepository;
    
    @InjectMocks
    private FranchiseService franchiseService;
    
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
        Franchise newFranchise = Franchise.builder()
                .name("New Franchise")
                .build();
        
        Franchise savedFranchise = Franchise.builder()
                .id(2L)
                .name("New Franchise")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));
        when(cacheRepository.set(anyString(), any(), any(Duration.class))).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(franchiseService.createFranchise(newFranchise))
                .expectNext(savedFranchise)
                .verifyComplete();
        
        verify(franchiseRepository).save(any(Franchise.class));
        verify(cacheRepository).set(eq("franchise:2"), eq(savedFranchise), any(Duration.class));
    }
    
    @Test
    void updateFranchiseName_Success() {
        // Given
        String newName = "Updated Franchise";
        Franchise updatedFranchise = Franchise.builder()
                .id(1L)
                .name(newName)
                .createdAt(testFranchise.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(testFranchise));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));
        when(cacheRepository.delete(anyString())).thenReturn(Mono.empty());
        when(cacheRepository.set(anyString(), any(), any(Duration.class))).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(franchiseService.updateFranchiseName(1L, newName))
                .expectNext(updatedFranchise)
                .verifyComplete();
        
        verify(franchiseRepository).findById(1L);
        verify(franchiseRepository).update(any(Franchise.class));
        verify(cacheRepository).delete("franchise:1");
    }
    
    @Test
    void updateFranchiseName_NotFound() {
        // Given
        when(franchiseRepository.findById(999L)).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(franchiseService.updateFranchiseName(999L, "New Name"))
                .expectError(ResourceNotFoundException.class)
                .verify();
        
        verify(franchiseRepository).findById(999L);
        verify(franchiseRepository, never()).update(any());
    }
    
    @Test
    void getFranchiseById_CacheHit() {
        // Given
        when(cacheRepository.get("franchise:1", Franchise.class))
            .thenReturn(Mono.just(testFranchise));
        
        // Mock el repositorio con lenient por si acaso se llama
        lenient().when(franchiseRepository.findById(1L))
            .thenReturn(Mono.just(testFranchise));
        
        // When & Then
        StepVerifier.create(franchiseService.getFranchiseById(1L))
                .expectNext(testFranchise)
                .verifyComplete();
        
        // Verificar que se llamó al cache
        verify(cacheRepository).get("franchise:1", Franchise.class);
    }
    
    @Test
    void getFranchiseById_CacheMiss() {
        // Given
        when(cacheRepository.get("franchise:1", Franchise.class)).thenReturn(Mono.empty());
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(testFranchise));
        when(cacheRepository.set(anyString(), any(), any(Duration.class))).thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(franchiseService.getFranchiseById(1L))
                .expectNext(testFranchise)
                .verifyComplete();
        
        verify(cacheRepository).get("franchise:1", Franchise.class);
        verify(franchiseRepository).findById(1L);
        verify(cacheRepository).set(eq("franchise:1"), eq(testFranchise), any(Duration.class));
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
        
        when(franchiseRepository.findAll()).thenReturn(Flux.just(testFranchise, franchise2));
        
        // When & Then
        StepVerifier.create(franchiseService.getAllFranchises())
                .expectNext(testFranchise)
                .expectNext(franchise2)
                .verifyComplete();
        
        verify(franchiseRepository).findAll();
    }
}