package com.servicebooking.controller;

import com.servicebooking.dto.request.AddressRequestDTO;
import com.servicebooking.dto.response.AddressResponseDTO;
import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
@Tag(name = "Customer Addresses", description = "Address management for customers")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('CUSTOMER')")
public class AddressController {

    private final AddressService addressService;

    // ✅ ADD ADDRESS
    @PostMapping
    @Operation(summary = "Add new address for logged-in customer")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> addAddress(
            @Valid @RequestBody AddressRequestDTO request) {

        return ResponseEntity.ok(addressService.addAddress(request));
    }

    // ✅ GET ALL ADDRESSES
    @GetMapping
    @Operation(summary = "Get all addresses of logged-in customer")
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> getAllAddresses() {

        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    // ✅ UPDATE ADDRESS
    @PutMapping("/{id}")
    @Operation(summary = "Update customer address by ID")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO request) {

        return ResponseEntity.ok(addressService.updateAddress(id, request));
    }

    // ✅ DELETE ADDRESS
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer address by ID")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @PathVariable Long id) {

        return ResponseEntity.ok(addressService.deleteAddress(id));
    }
}