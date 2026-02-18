package com.example.orda.service;

import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import java.util.List;
import java.util.Optional;

public interface VendorService {
    Vendor createVendor(Vendor vendor);
    Vendor getVendor(String id);
    List<Vendor> getAllVendors();
    Optional<Vendor> getVendorById(String id);
    Vendor updateVendor(String id, Vendor vendorDetails);
    void deleteVendor(String id);
    List<MenuItem> getVendorMenu(String vendorId);
}