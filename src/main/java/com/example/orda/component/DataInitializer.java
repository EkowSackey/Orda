package com.example.orda.component;

import com.example.orda.model.MenuItem;
import com.example.orda.model.Vendor;
import com.example.orda.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final VendorRepository vendorRepository;

    @Override
    public void run(String... args) {
        if (vendorRepository.count() == 0) {
            Vendor v1 = Vendor.builder()
                    .name("The Burger Joint")
                    .contactInfo("123 Grill Ave, Food City")
                    .menu(Arrays.asList(
                            MenuItem.builder()
                                    .name("Classic Cheeseburger")
                                    .basePrice(12.50)
                                    .customizationOptions(Arrays.asList("Extra Cheese", "No Onions", "Bacon"))
                                    .build(),
                            MenuItem.builder()
                                    .name("Truffle Fries")
                                    .basePrice(5.00)
                                    .build()
                    )).build();

            Vendor v2 = Vendor.builder()
                    .name("Pizza Palace")
                    .contactInfo("456 Dough St, Food City")
                    .menu(Arrays.asList(
                            MenuItem.builder()
                                    .name("Margherita Pizza")
                                    .basePrice(15.00)
                                    .customizationOptions(Arrays.asList("Thin Crust", "Extra Basil"))
                                    .build()
                    )).build();

            vendorRepository.saveAll(Arrays.asList(v1, v2));
        }
    }
}