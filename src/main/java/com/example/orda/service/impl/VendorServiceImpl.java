package com.example.orda.service.impl;

import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import com.example.orda.repository.VendorRepository;
import com.example.orda.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor getVendor(String id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found with id: " + id));
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor updateVendor(String id, Vendor vendor) {
        if (!vendorRepository.existsById(id)) {
            throw new EntityNotFoundException("Vendor not found with id: " + id);
        }
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @Override
    public void deleteVendor(String id) {
        if (!vendorRepository.existsById(id)) {
            throw new EntityNotFoundException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "menus", key = "#vendorId")
    public List<MenuItem> getVendorMenu(String vendorId) {
        log.info("Fetching menu for vendor: {}", vendorId);
        Vendor vendor = getVendor(vendorId);
        return vendor.getMenu();
    }
}
