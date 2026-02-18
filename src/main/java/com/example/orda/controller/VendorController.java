package com.example.orda.controller;

import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import com.example.orda.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable String id) {
        return vendorService.getVendorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{vendorId}/menu")
    public ResponseEntity<MenuItem> addMenuItem(@PathVariable String vendorId, @RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(vendorService.addMenuItem(vendorId, menuItem));
    }

    @PutMapping("/{vendorId}/menu/{menuItemId}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable String vendorId, 
                                                 @PathVariable String menuItemId, 
                                                 @RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(vendorService.updateMenuItem(vendorId, menuItemId, menuItem));
    }

    @DeleteMapping("/{vendorId}/menu/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String vendorId, @PathVariable String menuItemId) {
        vendorService.deleteMenuItem(vendorId, menuItemId);
        return ResponseEntity.noContent().build();
    }
}