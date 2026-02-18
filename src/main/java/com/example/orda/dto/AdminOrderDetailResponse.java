package com.example.orda.dto;

import com.example.orda.enums.OrderStatus;
import com.example.orda.model.OrderItem;
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
public class AdminOrderDetailResponse {
    private String orderId;
    private String employeeName;
    private String employeeEmail;
    private String vendorName;
    private List<OrderItem> items;
    private Double totalAmount;
    private LocalDateTime orderTime;
    private OrderStatus status;
}