package com.servicebooking.controller;

import com.servicebooking.dto.request.BookingCreateRequest;
import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.BookingResponseDTO;
import com.servicebooking.dto.response.PageResponse;
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
@Tag(name = "Bookings", description = "Booking management APIs")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ================= CREATE BOOKING =================
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Create a new booking",
            description = "Customer creates a booking for a selected service"
    )
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestBody BookingCreateRequest request) {

        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    // ================= ASSIGN PROVIDER =================
    @PutMapping("/{id}/assign-provider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Assign provider to booking",
            description = "Admin assigns a provider to an existing booking"
    )
    public ResponseEntity<ApiResponse<BookingResponseDTO>> assignProvider(
            @PathVariable Long id,
            @RequestParam Long providerId) {

        return ResponseEntity.ok(
                bookingService.assignProvider(id, providerId)
        );
    }

    // ================= UPDATE STATUS =================
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @Operation(
            summary = "Update booking status",
            description = "Admin or Provider updates booking status (ACCEPTED, COMPLETED, etc.)"
    )
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {

        return ResponseEntity.ok(
                bookingService.updateStatus(id, status)
        );
    }

    // ================= CUSTOMER BOOKINGS =================
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Get customer bookings",
            description = "Customer views their booking history with pagination"
    )
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getCustomerBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getCustomerBookings(page, size)
        );
    }

    // ================= PROVIDER BOOKINGS =================
    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Get provider bookings",
            description = "Provider views bookings assigned to them"
    )
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getProviderBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getProviderBookings(page, size)
        );
    }

    // ================= FILTER BOOKINGS =================
    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Filter bookings",
            description = "Admin filters bookings using dynamic criteria"
    )
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> filterBookings(
            @RequestBody Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.filterBookings(filters, page, size)
        );
    }

    // ================= CANCEL BOOKING =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @Operation(
            summary = "Cancel booking",
            description = "Customer or Admin cancels a booking"
    )
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(id)
        );
    }
}