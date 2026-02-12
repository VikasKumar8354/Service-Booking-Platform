package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.Booking;
import com.servicebooking.enums.BookingStatus;
import com.servicebooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Booking management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new booking")
    public ResponseEntity<ApiResponse<Booking>> createBooking(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @PutMapping("/{id}/assign-provider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign provider to booking")
    public ResponseEntity<ApiResponse<Booking>> assignProvider(
            @PathVariable Long id,
            @RequestParam Long providerId) {
        return ResponseEntity.ok(bookingService.assignProvider(id, providerId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update booking status")
    public ResponseEntity<ApiResponse<Booking>> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateStatus(id, status));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get customer bookings")
    public ResponseEntity<ApiResponse<PageResponse<Booking>>> getCustomerBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(page, size));
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Get provider bookings")
    public ResponseEntity<ApiResponse<PageResponse<Booking>>> getProviderBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.getProviderBookings(page, size));
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter bookings with dynamic criteria")
    public ResponseEntity<ApiResponse<PageResponse<Booking>>> filterBookings(
            @RequestBody Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.filterBookings(filters, page, size));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel booking")
    public ResponseEntity<ApiResponse<String>> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}
