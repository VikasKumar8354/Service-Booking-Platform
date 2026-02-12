package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Address;
import com.servicebooking.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/addresses")
@Tag(name = "Customer Addresses", description = "Address management for customers")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('CUSTOMER')")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    @Operation(summary = "Add new address")
    public ResponseEntity<ApiResponse<Address>> addAddress(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(addressService.addAddress(request));
    }

    @GetMapping
    @Operation(summary = "Get all addresses")
    public ResponseEntity<ApiResponse<List<Address>>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(addressService.updateAddress(id, updates));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.deleteAddress(id));
    }
}
