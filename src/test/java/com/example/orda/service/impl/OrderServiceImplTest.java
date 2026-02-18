package com.example.orda.service.impl;

import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.model.Order;
import com.example.orda.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrder_shouldSaveAndReturnOrder() {
        // Arrange
        Order order = Order.builder()
                .userId("user123")
                .vendorId("vendor456")
                .totalAmount(25.0)
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order savedOrder = orderService.placeOrder(order);

        // Assert
        assertNotNull(savedOrder);
        assertEquals("user123", savedOrder.getUserId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getOrderHistory_shouldReturnOrdersForUser() {
        // Arrange
        String userId = "user123";
        List<Order> mockOrders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findByUserId(userId)).thenReturn(mockOrders);

        // Act
        List<OrderHistoryDTO> history = orderService.getOrderHistory(userId);

        // Assert
        assertEquals(2, history.size());
        verify(orderRepository, times(1)).findByUserId(userId);
    }
}