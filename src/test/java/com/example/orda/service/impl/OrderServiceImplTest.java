package com.example.orda.service.impl;

import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.enums.OrderStatus;
import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.Order;
import com.example.orda.model.OrderItem;
import com.example.orda.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_shouldSaveAndReturnOrder() {
        // Given
        Order order = new Order();
        order.setUserId("user1");
        order.setVendorId("vendor1");
        order.setTotalAmount(100.0);

        Order savedOrder = new Order();
        savedOrder.setId("order123");
        savedOrder.setUserId("user1");
        savedOrder.setVendorId("vendor1");
        savedOrder.setTotalAmount(100.0);
        savedOrder.setOrderTime(LocalDateTime.now());
        savedOrder.setStatus(OrderStatus.PENDING);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = orderService.placeOrder(order);

        // Then
        assertNotNull(result);
        assertEquals("order123", result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertNotNull(result.getOrderTime());
        assertEquals(100.0, result.getTotalAmount());
    }

    @Test
    void getOrderHistory_shouldReturnOrderHistoryForUser() {
        // Given
        String userId = "user1";
        Order order1 = new Order();
        order1.setId("order1");
        order1.setVendorId("vendor1");
        order1.setTotalAmount(50.0);
        order1.setOrderTime(LocalDateTime.now().minusDays(1));
        order1.setStatus(OrderStatus.COMPLETED);

        Order order2 = new Order();
        order2.setId("order2");
        order2.setVendorId("vendor2");
        order2.setTotalAmount(75.0);
        order2.setOrderTime(LocalDateTime.now().minusHours(5));
        order2.setStatus(OrderStatus.PENDING);

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        // When
        List<OrderHistoryDTO> result = orderService.getOrderHistory(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("order1", result.get(0).getId());
        assertEquals("vendor1", result.get(0).getVendorId());
        assertEquals(50.0, result.get(0).getTotalAmount());
        assertEquals(OrderStatus.COMPLETED, result.get(0).getStatus());
    }

    @Test
    void getOrderHistory_shouldReturnEmptyList_whenNoOrderHistory() {
        // Given
        String userId = "user1";
        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<OrderHistoryDTO> result = orderService.getOrderHistory(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void repeatOrder_shouldCreateNewOrderFromExisting() {
        // Given
        String oldOrderId = "oldOrder123";
        OrderItem item1 = OrderItem.builder()
                .menuItemId("menu1")
                .itemName("Burger")
                .price(10.0)
                .build();
        OrderItem item2 = OrderItem.builder()
                .menuItemId("menu2")
                .itemName("Fries")
                .price(5.0)
                .build();

        Order oldOrder = Order.builder()
                .id(oldOrderId)
                .userId("user1")
                .vendorId("vendor1")
                .items(Arrays.asList(item1, item2))
                .totalAmount(15.0)
                .orderTime(LocalDateTime.now().minusDays(2))
                .status(OrderStatus.COMPLETED)
                .build();

        Order newOrder = Order.builder()
                .id("newOrder456")
                .userId("user1")
                .vendorId("vendor1")
                .items(Arrays.asList(item1, item2))
                .totalAmount(15.0)
                .orderTime(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(oldOrderId)).thenReturn(Optional.of(oldOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(newOrder);

        // When
        Order result = orderService.repeatOrder(oldOrderId);

        // Then
        assertNotNull(result);
        assertEquals("newOrder456", result.getId());
        assertEquals("user1", result.getUserId());
        assertEquals("vendor1", result.getVendorId());
        assertEquals(2, result.getItems().size());
        assertEquals(15.0, result.getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertNotEquals(oldOrder.getOrderTime(), result.getOrderTime());
    }

    @Test
    void repeatOrder_shouldThrowEntityNotFoundException_whenOldOrderNotFound() {
        // Given
        String oldOrderId = "nonExistentOrder";
        when(orderRepository.findById(oldOrderId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                orderService.repeatOrder(oldOrderId)
        );
        assertEquals("Order not found with id: " + oldOrderId, exception.getMessage());
    }
}
