package com.example.orda.controller;

import com.example.orda.dto.request.OrderRequest;
import com.example.orda.model.Order;
import com.example.orda.model.OrderItem;
import com.example.orda.model.User;
import com.example.orda.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Extract the authenticated user from the Security Context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Map DTO to Entity
        Order order = Order.builder()
                .userId(user.getId())
                .vendorId(orderRequest.getVendorId())
                .items(mapItems(orderRequest.getItems()))
                .totalAmount(orderRequest.getTotalAmount())
                .build();

        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    private List<OrderItem> mapItems(List<com.example.orda.dto.request.OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(item -> OrderItem.builder()
                        .menuItemId(item.getMenuItemId())
                        .itemName(item.getItemName())
                        .price(item.getPrice())
                        .selectedCustomizations(item.getSelectedCustomizations())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/history")
    public ResponseEntity<?> getMyOrderHistory() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(orderService.getOrderHistory(user.getId()));
    }
}