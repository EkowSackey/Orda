package com.example.orda.service.impl;

import com.example.orda.exception.EntityNotFoundException;
import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import com.example.orda.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VendorServiceImplTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVendor_shouldSaveAndReturnVendor() {
        // Given
        Vendor vendor = new Vendor();
        vendor.setName("Test Vendor");
        vendor.setContactInfo("test@example.com");

        Vendor savedVendor = new Vendor();
        savedVendor.setId("vendor123");
        savedVendor.setName("Test Vendor");
        savedVendor.setContactInfo("test@example.com");

        when(vendorRepository.save(any(Vendor.class))).thenReturn(savedVendor);

        // When
        Vendor result = vendorService.createVendor(vendor);

        // Then
        assertNotNull(result);
        assertEquals("vendor123", result.getId());
        assertEquals("Test Vendor", result.getName());
        assertEquals("test@example.com", result.getContactInfo());
    }

    @Test
    void getVendor_shouldReturnVendor_whenFound() {
        // Given
        String vendorId = "vendor123";
        Vendor vendor = new Vendor();
        vendor.setId(vendorId);
        vendor.setName("Test Vendor");

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        // When
        Vendor result = vendorService.getVendor(vendorId);

        // Then
        assertNotNull(result);
        assertEquals(vendorId, result.getId());
        assertEquals("Test Vendor", result.getName());
    }

    @Test
    void getVendor_shouldThrowEntityNotFoundException_whenNotFound() {
        // Given
        String vendorId = "nonExistentVendor";
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vendorService.getVendor(vendorId)
        );
        assertEquals("Vendor not found with id: " + vendorId, exception.getMessage());
    }

    @Test
    void getAllVendors_shouldReturnListOfVendors() {
        // Given
        Vendor vendor1 = new Vendor();
        vendor1.setId("vendor1");
        Vendor vendor2 = new Vendor();
        vendor2.setId("vendor2");
        List<Vendor> vendors = Arrays.asList(vendor1, vendor2);

        when(vendorRepository.findAll()).thenReturn(vendors);

        // When
        List<Vendor> result = vendorService.getAllVendors();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("vendor1", result.get(0).getId());
    }

    @Test
    void getAllVendors_shouldReturnEmptyList_whenNoVendors() {
        // Given
        when(vendorRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Vendor> result = vendorService.getAllVendors();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateVendor_shouldUpdateAndReturnVendor_whenFound() {
        // Given
        String vendorId = "vendor123";
        Vendor existingVendor = new Vendor();
        existingVendor.setId(vendorId);
        existingVendor.setName("Old Name");
        existingVendor.setContactInfo("old@example.com");

        Vendor updatedVendorDetails = new Vendor();
        updatedVendorDetails.setName("New Name");
        updatedVendorDetails.setContactInfo("new@example.com");

        Vendor savedVendor = new Vendor();
        savedVendor.setId(vendorId);
        savedVendor.setName("New Name");
        savedVendor.setContactInfo("new@example.com");

        when(vendorRepository.existsById(vendorId)).thenReturn(true);
        when(vendorRepository.save(any(Vendor.class))).thenReturn(savedVendor);

        // When
        Vendor result = vendorService.updateVendor(vendorId, updatedVendorDetails);

        // Then
        assertNotNull(result);
        assertEquals(vendorId, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getContactInfo());
    }

    @Test
    void updateVendor_shouldThrowEntityNotFoundException_whenNotFound() {
        // Given
        String vendorId = "nonExistentVendor";
        Vendor updatedVendorDetails = new Vendor();
        when(vendorRepository.existsById(vendorId)).thenReturn(false);

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vendorService.updateVendor(vendorId, updatedVendorDetails)
        );
        assertEquals("Vendor not found with id: " + vendorId, exception.getMessage());
    }

    @Test
    void deleteVendor_shouldDeleteVendor_whenFound() {
        // Given
        String vendorId = "vendor123";
        when(vendorRepository.existsById(vendorId)).thenReturn(true);
        doNothing().when(vendorRepository).deleteById(vendorId);

        // When
        vendorService.deleteVendor(vendorId);

        // Then
        verify(vendorRepository, times(1)).deleteById(vendorId);
    }

    @Test
    void deleteVendor_shouldThrowEntityNotFoundException_whenNotFound() {
        // Given
        String vendorId = "nonExistentVendor";
        when(vendorRepository.existsById(vendorId)).thenReturn(false);

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vendorService.deleteVendor(vendorId)
        );
        assertEquals("Vendor not found with id: " + vendorId, exception.getMessage());
    }

    @Test
    void getVendorMenu_shouldReturnMenu_whenVendorFound() {
        // Given
        String vendorId = "vendor123";
        MenuItem item1 = new MenuItem();
        item1.setId("item1");
        item1.setName("Pizza");
        item1.setBasePrice(12.50);

        MenuItem item2 = new MenuItem();
        item2.setId("item2");
        item2.setName("Pasta");
        item2.setBasePrice(15.00);

        Vendor vendor = new Vendor();
        vendor.setId(vendorId);
        vendor.setName("Test Vendor");
        vendor.setMenu(Arrays.asList(item1, item2));

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        // When
        List<MenuItem> result = vendorService.getVendorMenu(vendorId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getName());
        assertEquals("Pasta", result.get(1).getName());
        assertEquals(12.50, result.get(0).getBasePrice());
    }

    @Test
    void getVendorMenu_shouldThrowEntityNotFoundException_whenVendorNotFound() {
        // Given
        String vendorId = "nonExistentVendor";
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        // When / Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                vendorService.getVendorMenu(vendorId)
        );
        assertEquals("Vendor not found with id: " + vendorId, exception.getMessage());
    }
}
