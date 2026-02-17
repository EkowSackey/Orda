package com.example.orda.service.impl;

import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.enums.OrderStatus;
import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.Order;
import com.example.orda.model.OrderItem;
import com.example.orda.repository.OrderRepository;
import com.example.orda.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

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
    public Order repeatOrder(String oldOrderId) {
        Order oldOrder = orderRepository.findById(oldOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oldOrderId));

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
}
