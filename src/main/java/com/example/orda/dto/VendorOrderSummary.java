package com.example.orda.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorOrderSummary {
    private String vendorId;
    private String vendorName; // Added vendorName
    private Long totalOrders; // Renamed from orderCount to match template
    private Double totalRevenue;
}