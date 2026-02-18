package com.example.orda.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    @NotBlank
    private String vendorId;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    @NotNull
    private Double totalAmount;
}