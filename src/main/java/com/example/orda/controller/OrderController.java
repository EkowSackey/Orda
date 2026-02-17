package com.example.orda.controller;

import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.model.Order;
import com.example.orda.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.placeOrder(order));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    @PostMapping("/repeat/{oldOrderId}")
    public ResponseEntity<Order> repeatOrder(@PathVariable String oldOrderId) {
        return ResponseEntity.ok(orderService.repeatOrder(oldOrderId));
    }
}
