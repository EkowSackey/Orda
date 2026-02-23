package com.example.orda.controller;

import com.example.orda.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class WebAdminController {

    private final OrderService orderService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("title", "Admin Dashboard - Orda");
        return "admin/dashboard";
    }

    @GetMapping("/summary")
    public String summary(Model model) {
        model.addAttribute("summaries", orderService.getVendorOrderSummaries());
        model.addAttribute("title", "Vendor Summaries - Orda");
        return "admin/summary";
    }

    @GetMapping("/logistics")
    public String logistics(Model model) {
        model.addAttribute("orders", orderService.getAllOrdersForAdmin());
        model.addAttribute("title", "Logistics - Orda");
        return "admin/logistics";
    }
}
