package com.example.orda.repository;

import com.example.orda.dto.VendorOrderSummary;
import com.example.orda.enums.OrderStatus;
import com.example.orda.model.Order;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByStatus(OrderStatus status);

    @Aggregation(pipeline = {
        "{ '$group': { '_id': '$vendorId', 'orderCount': { '$sum': 1 }, 'totalRevenue': { '$sum': '$totalAmount' } } }",
        "{ '$project': { 'vendorId': '$_id', 'orderCount': 1, 'totalRevenue': 1, '_id': 0 } }"
    })
    List<VendorOrderSummary> aggregateOrdersByVendor();
}
