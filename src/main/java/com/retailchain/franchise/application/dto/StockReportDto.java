package com.retailchain.franchise.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReportDto {
    private Long productId;
    private String productName;
    private Integer stock;
    private Long branchId;
    private String branchName;
    private Long franchiseId;
    private String franchiseName;
}