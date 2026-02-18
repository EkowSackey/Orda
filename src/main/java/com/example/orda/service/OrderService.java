package com.example.orda.service;

import com.example.orda.dto.AdminOrderDetailResponse;
import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.dto.VendorOrderSummary;
import com.example.orda.model.Order;
import java.util.List;

public interface OrderService {
    Order placeOrder(Order order);
    List<OrderHistoryDTO> getOrderHistory(String userId);
    Order repeatOrder(String oldOrderId, String userId);
    List<VendorOrderSummary> getVendorOrderSummaries();
    List<AdminOrderDetailResponse> getAllOrdersForAdmin();
}
