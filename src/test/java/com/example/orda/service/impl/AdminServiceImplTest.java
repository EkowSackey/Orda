package com.example.orda.service.impl;

import com.example.orda.enums.OrderStatus;
import com.example.orda.model.Order;
import com.example.orda.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AdminServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getConsolidatedVendorOrders_shouldReturnGroupedOrders() {
        // Given
        Order order1 = new Order();
        order1.setVendorId("vendor1");
        order1.setStatus(OrderStatus.PENDING);

        Order order2 = new Order();
        order2.setVendorId("vendor2");
        order2.setStatus(OrderStatus.PENDING);

        Order order3 = new Order();
        order3.setVendorId("vendor1");
        order3.setStatus(OrderStatus.PENDING);

        List<Order> pendingOrders = Arrays.asList(order1, order2, order3);

        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        // When
        Map<String, List<Order>> result = adminService.getConsolidatedVendorOrders();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.containsKey("vendor1"));
        assertTrue(result.containsKey("vendor2"));
        assertEquals(2, result.get("vendor1").size());
        assertEquals(1, result.get("vendor2").size());
        assertTrue(result.get("vendor1").contains(order1));
        assertTrue(result.get("vendor1").contains(order3));
        assertTrue(result.get("vendor2").contains(order2));
    }

    @Test
    void getConsolidatedVendorOrders_shouldReturnEmptyMap_whenNoPendingOrders() {
        // Given
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(Collections.emptyList());

        // When
        Map<String, List<Order>> result = adminService.getConsolidatedVendorOrders();

        // Then
        assertTrue(result.isEmpty());
    }
}
