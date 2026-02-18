package com.example.orda.service.impl;

import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import com.example.orda.repository.VendorRepository;
import com.example.orda.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @CacheEvict(value = "vendors", allEntries = true)
    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor getVendor(String id) {
        return vendorRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Vendor not found with id: " + id));
    }

    @Override
    @Cacheable(value = "vendors")
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Optional<Vendor> getVendorById(String id) {
        return vendorRepository.findById(id);
    }

    @Override
    @CacheEvict(value = "vendors", allEntries = true)
    public Vendor updateVendor(String id, Vendor vendorDetails) {
       if (!vendorRepository.existsById(id)){
           throw new EntityNotFoundException("Vendor not found with id: " + id);
       } else {
           vendorDetails.setId(id);
           return vendorRepository.save(vendorDetails);
       }
    }

    @Override
    @CacheEvict(value = "vendors", allEntries = true)
    public void deleteVendor(String id) {
        if (!vendorRepository.existsById(id)){
            throw new EntityNotFoundException("Vendor not found with id: " + id);
            } else {
            vendorRepository.deleteById(id);
        }
    }

    @Override
    public List<MenuItem> getVendorMenu(String vendorId) {
        return getVendor(vendorId).getMenu();
    }

    @Override
    @CacheEvict(value = "vendors", allEntries = true)
    public MenuItem addMenuItem(String vendorId, MenuItem menuItem) {
        Vendor vendor = getVendor(vendorId);
        if (vendor.getMenu() == null) {
            vendor.setMenu(new ArrayList<>());
        }
        
        if (menuItem.getId() == null || menuItem.getId().isEmpty()) {
            menuItem.setId(UUID.randomUUID().toString());
        }
        
        vendor.getMenu().add(menuItem);
        vendorRepository.save(vendor);
        return menuItem;
    }

    @Override
    @CacheEvict(value = "vendors", allEntries = true)
    public MenuItem updateMenuItem(String vendorId, String menuItemId, MenuItem menuItemDetails) {
        Vendor vendor = getVendor(vendorId);
        List<MenuItem> menu = vendor.getMenu();
        
        MenuItem existingItem = menu.stream()
                .filter(item -> item.getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found with id: " + menuItemId));

        existingItem.setName(menuItemDetails.getName());
        existingItem.setBasePrice(menuItemDetails.getBasePrice());
        existingItem.setCustomizationOptions(menuItemDetails.getCustomizationOptions());

        vendorRepository.save(vendor);
        return existingItem;
    }

    @Override
    @CacheEvict(value = "vendors", allEntries = true)
    public void deleteMenuItem(String vendorId, String menuItemId) {
        Vendor vendor = getVendor(vendorId);
        boolean removed = vendor.getMenu().removeIf(item -> item.getId().equals(menuItemId));
        
        if (!removed) {
            throw new EntityNotFoundException("Menu item not found with id: " + menuItemId);
        }
        
        vendorRepository.save(vendor);
    }
}