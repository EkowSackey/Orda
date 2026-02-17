package com.example.orda.service;

import com.example.orda.model.Order;
import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, List<Order>> getConsolidatedVendorOrders();
}
