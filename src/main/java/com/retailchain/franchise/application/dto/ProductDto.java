package com.retailchain.franchise.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProductDto {
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    // SIN @NotNull - el branchId viene de la URL
    private Long branchId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}