package com.retailchain.franchise.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDto {
    private Long id;
    
    @NotBlank(message = "Branch name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    // SIN @NotNull - el franchiseId viene de la URL
    private Long franchiseId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}