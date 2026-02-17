package com.example.orda.service.impl;

import com.example.orda.enums.OrderStatus;
import com.example.orda.model.Order;
import com.example.orda.repository.OrderRepository;
import com.example.orda.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final OrderRepository orderRepository;

    @Override
    public Map<String, List<Order>> getConsolidatedVendorOrders() {
        log.info("Admin Access: Consolidated View Triggered");
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        return pendingOrders.stream()
                .collect(Collectors.groupingBy(Order::getVendorId));
    }
}
