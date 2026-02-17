package com.example.orda.service;

import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import java.util.List;

public interface VendorService {
    Vendor createVendor(Vendor vendor);
    Vendor getVendor(String id);
    List<Vendor> getAllVendors();
    Vendor updateVendor(String id, Vendor vendor);
    void deleteVendor(String id);
    List<MenuItem> getVendorMenu(String vendorId);
}
