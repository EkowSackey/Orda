package com.example.orda.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderItemRequest {
    @NotBlank
    private String menuItemId;

    @NotBlank
    private String itemName;

    @NotNull
    private Double price;

    private List<String> selectedCustomizations;
}