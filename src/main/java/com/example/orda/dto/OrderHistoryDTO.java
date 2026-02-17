package com.example.orda.dto;

import com.example.orda.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDTO {
    private String id;
    private String vendorId;
    private Double totalAmount;
    private LocalDateTime orderTime;
    private OrderStatus status;
}
