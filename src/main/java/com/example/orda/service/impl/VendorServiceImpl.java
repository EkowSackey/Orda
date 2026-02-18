package com.example.orda.service.impl;

import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import com.example.orda.repository.VendorRepository;
import com.example.orda.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    public Vendor createVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor getVendor(String id) {
        return vendorRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Vendor not found with id: " + id));
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public Optional<Vendor> getVendorById(String id) {
        return vendorRepository.findById(id);
    }

    @Override
    public Vendor updateVendor(String id, Vendor vendorDetails) {
       if (!vendorRepository.existsById(id)){
           throw new EntityNotFoundException("Vendor not found with id: " + id);
       } else {
           vendorDetails.setId(id);
           return vendorRepository.save(vendorDetails);
       }
    }

    @Override
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
}