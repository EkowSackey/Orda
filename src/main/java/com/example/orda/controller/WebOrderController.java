package com.example.orda.controller;

import com.example.orda.dto.OrderHistoryDTO;
import com.example.orda.dto.request.OrderItemRequest;
import com.example.orda.model.Order;
import com.example.orda.model.OrderItem;
import com.example.orda.model.User;
import com.example.orda.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class WebOrderController {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @GetMapping("/cart")
    public String viewCart(Model model) {
        model.addAttribute("title", "Your Cart - Orda");
        return "cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String vendorId,
                           @RequestParam String itemsJson,
                           @RequestParam Double totalAmount,
                           Model model) {
        try {
            List<OrderItemRequest> itemRequests = objectMapper.readValue(itemsJson, new TypeReference<List<OrderItemRequest>>() {});
            
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<OrderItem> items = itemRequests.stream()
                .map(item -> OrderItem.builder()
                        .menuItemId(item.getMenuItemId())
                        .itemName(item.getItemName())
                        .price(item.getPrice())
                        .selectedCustomizations(item.getSelectedCustomizations())
                        .build())
                .collect(Collectors.toList());

            Order order = Order.builder()
                    .userId(user.getId())
                    .vendorId(vendorId)
                    .items(items)
                    .totalAmount(totalAmount)
                    .build();

            orderService.placeOrder(order);
            
            return "redirect:/orders/confirmation";
        } catch (Exception e) {
            model.addAttribute("error", "Error processing order: " + e.getMessage());
            return "cart";
        }
    }

    @GetMapping("/confirmation")
    public String confirmation(Model model) {
        model.addAttribute("title", "Order Confirmation - Orda");
        return "order_confirmation";
    }

    @GetMapping("/history")
    public String history(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<OrderHistoryDTO> history = orderService.getOrderHistory(user.getId());
        model.addAttribute("orders", history);
        model.addAttribute("title", "Order History - Orda");
        return "history";
    }

    @PostMapping("/{id}/repeat")
    public String repeatOrder(@PathVariable String id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        orderService.repeatOrder(id, user.getId());
        return "redirect:/orders/confirmation";
    }
}
