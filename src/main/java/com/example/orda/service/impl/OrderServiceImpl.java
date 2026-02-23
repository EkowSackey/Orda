package com.example.orda.service.impl;

import com.example.orda.dto.AdminOrderDetailResponse;
import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.dto.VendorOrderSummary;
import com.example.orda.enums.OrderStatus;
import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.Order;
import com.example.orda.model.OrderItem;
import com.example.orda.model.User;
import com.example.orda.model.Vendor;
import com.example.orda.repository.OrderRepository;
import com.example.orda.repository.UserRepository;
import com.example.orda.repository.VendorRepository;
import com.example.orda.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public Order placeOrder(Order order) {
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        log.info("Order Placed: UserID={}, Amount={}", savedOrder.getUserId(), savedOrder.getTotalAmount());
        return savedOrder;
    }

    @Override
    public List<OrderHistoryDTO> getOrderHistory(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(order -> OrderHistoryDTO.builder()
                        .id(order.getId())
                        .vendorId(order.getVendorId())
                        .totalAmount(order.getTotalAmount())
                        .orderTime(order.getOrderTime())
                        .status(order.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Order repeatOrder(String oldOrderId, String userId) {
        Order oldOrder = orderRepository.findById(oldOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oldOrderId));

        if (!oldOrder.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to repeat this order.");
        }

        List<OrderItem> newItems = oldOrder.getItems().stream()
                .map(item -> OrderItem.builder()
                        .menuItemId(item.getMenuItemId())
                        .itemName(item.getItemName())
                        .selectedCustomizations(item.getSelectedCustomizations())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        Order newOrder = Order.builder()
                .userId(oldOrder.getUserId())
                .vendorId(oldOrder.getVendorId())
                .items(newItems)
                .totalAmount(oldOrder.getTotalAmount())
                .orderTime(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(newOrder);
        log.info("Order Repeated: UserID={}, Amount={}, OldOrderID={}", savedOrder.getUserId(), savedOrder.getTotalAmount(), oldOrderId);
        return savedOrder;
    }

    @Override
    public List<VendorOrderSummary> getVendorOrderSummaries() {
        List<VendorOrderSummary> summaries = orderRepository.aggregateOrdersByVendor();
        
        // Fetch vendor names
        Map<String, String> vendorNames = vendorRepository.findAll().stream()
                .collect(Collectors.toMap(Vendor::getId, Vendor::getName));

        summaries.forEach(summary -> 
            summary.setVendorName(vendorNames.getOrDefault(summary.getVendorId(), "Unknown Vendor"))
        );
        
        return summaries;
    }

    @Override
    public List<AdminOrderDetailResponse> getAllOrdersForAdmin() {
        // Only fetch PENDING orders for lunch coordination
        return orderRepository.findByStatus(OrderStatus.PENDING).stream()
                .map(order -> {
                    User user = userRepository.findById(order.getUserId()).orElse(null);
                    Vendor vendor = vendorRepository.findById(order.getVendorId()).orElse(null);

                    return AdminOrderDetailResponse.builder()
                            .orderId(order.getId())
                            .employeeName(user != null ? user.getUsername() : "Unknown")
                            .employeeEmail(user != null ? user.getEmail() : "N/A")
                            .vendorName(vendor != null ? vendor.getName() : "Unknown Vendor")
                            .items(order.getItems())
                            .totalAmount(order.getTotalAmount())
                            .orderTime(order.getOrderTime())
                            .status(order.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
