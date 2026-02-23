package com.example.orda.controller;

import com.example.orda.model.Vendor;
import com.example.orda.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/vendors")
@RequiredArgsConstructor
public class WebVendorController {

    private final VendorService vendorService;

    @GetMapping
    public String getAllVendors(Model model) {
        List<Vendor> vendors = vendorService.getAllVendors();
        model.addAttribute("vendors", vendors);
        return "vendors/list";
    }

    @GetMapping("/{id}")
    public String getVendorDetails(@PathVariable String id, Model model) {
        Optional<Vendor> vendor = vendorService.getVendorById(id);
        if (vendor.isPresent()) {
            model.addAttribute("vendor", vendor.get());
            return "vendors/detail";
        } else {
            return "redirect:/vendors";
        }
    }
}