package com.retailchain.franchise.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Product> products;
}