package com.example.orda.service.impl;

import com.example.orda.dto.AdminOrderDetailResponse;
import com.example.orda.enums.OrderStatus;
import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.Order;
import com.example.orda.model.OrderItem;
import com.example.orda.model.User;
import com.example.orda.model.Vendor;
import com.example.orda.repository.OrderRepository;
import com.example.orda.repository.UserRepository;
import com.example.orda.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order sampleOrder;
    private String userId = "user-123";
    private String orderId = "order-456";

    @BeforeEach
    void setUp() {
        sampleOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .vendorId("vendor-789")
                .items(List.of(OrderItem.builder().menuItemId("item-1").itemName("Item 1").build()))
                .totalAmount(15.0)
                .status(OrderStatus.COMPLETED)
                .orderTime(LocalDateTime.now().minusDays(1))
                .build();
    }

    @Test
    void repeatOrder_Success() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order repeatedOrder = orderService.repeatOrder(orderId, userId);

        // Assert
        assertNotNull(repeatedOrder);
        assertEquals(userId, repeatedOrder.getUserId());
        assertEquals(OrderStatus.PENDING, repeatedOrder.getStatus());
        assertEquals(sampleOrder.getItems().size(), repeatedOrder.getItems().size());
        assertNotEquals(sampleOrder.getId(), repeatedOrder.getId());
        assertTrue(repeatedOrder.getOrderTime().isAfter(sampleOrder.getOrderTime()));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void repeatOrder_UnauthorizedUser_ThrowsException() {
        // Arrange
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            orderService.repeatOrder(orderId, "wrong-user-id")
        );
        assertEquals("You are not authorized to repeat this order.", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void repeatOrder_OrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> 
            orderService.repeatOrder("invalid-id", userId)
        );
    }

    @Test
    void getAllOrdersForAdmin_ReturnsEnrichedData() {
        // Arrange
        User mockUser = User.builder().id(userId).username("jdoe").email("jdoe@example.com").build();
        Vendor mockVendor = Vendor.builder().id("vendor-789").name("Burger Joint").build();

        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(vendorRepository.findById("vendor-789")).thenReturn(Optional.of(mockVendor));

        // Act
        List<AdminOrderDetailResponse> results = orderService.getAllOrdersForAdmin();

        // Assert
        assertFalse(results.isEmpty());
        AdminOrderDetailResponse detail = results.get(0);
        assertEquals("jdoe", detail.getEmployeeName());
        assertEquals("jdoe@example.com", detail.getEmployeeEmail());
        assertEquals("Burger Joint", detail.getVendorName());
        assertEquals(orderId, detail.getOrderId());
    }

    @Test
    void getAllOrdersForAdmin_HandlesMissingUserOrVendor() {
        // Arrange
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(vendorRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act
        List<AdminOrderDetailResponse> results = orderService.getAllOrdersForAdmin();

        // Assert
        assertEquals("Unknown", results.get(0).getEmployeeName());
        assertEquals("Unknown Vendor", results.get(0).getVendorName());
    }
}