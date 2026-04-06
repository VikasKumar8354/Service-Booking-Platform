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

import java.time.LocalDateTime;
import java.util.List;
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
    @Operation(summary = "Create booking",
            description = "Customer creates a booking")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestBody BookingCreateRequest request) {

        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    // ================= ASSIGN PROVIDER =================
    @PutMapping("/{id}/assign-provider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign provider",
            description = "Admin assigns provider to booking")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> assignProvider(
            @PathVariable Long id,
            @RequestParam Long providerId) {

        return ResponseEntity.ok(
                bookingService.assignProvider(id, providerId));
    }

    // ================= UPDATE STATUS =================
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @Operation(summary = "Update booking status",
            description = "Admin/Provider updates status")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {

        return ResponseEntity.ok(
                bookingService.updateStatus(id, status));
    }

    // ================= CUSTOMER BOOKINGS =================
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Customer bookings",
            description = "Customer views own bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getCustomerBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getCustomerBookings(page, size));
    }

    // ================= PROVIDER BOOKINGS =================
    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Provider bookings",
            description = "Provider views assigned bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getProviderBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getProviderBookings(page, size));
    }

    // ================= FILTER BOOKINGS =================
    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filter bookings",
            description = "Admin filters bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> filterBookings(
            @RequestBody Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.filterBookings(filters, page, size));
    }

    // ================= CANCEL BOOKING =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @Operation(summary = "Cancel booking",
            description = "Cancel a booking")
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(id));
    }

    // ================= BY CUSTOMER ID =================
    @GetMapping("/by-customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bookings by customer ID",
            description = "Admin gets bookings of a customer")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookingsByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getBookingsByCustomerId(customerId, page, size));
    }

    // ================= BY PROVIDER ID =================
    @GetMapping("/by-provider/{providerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bookings by provider ID",
            description = "Admin gets provider bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookingsByProviderId(
            @PathVariable Long providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getBookingsByProviderId(providerId, page, size));
    }

    // ================= BY STATUS =================
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bookings by status",
            description = "Admin filters by status")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getBookingsByStatus(
            @RequestParam BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                bookingService.getBookingsByStatus(status, page, size));
    }

    // ================= COUNT BY STATUS =================
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count bookings",
            description = "Dashboard booking count")
    public ResponseEntity<ApiResponse<Long>> countBookingsByStatus(
            @RequestParam BookingStatus status) {

        return ResponseEntity.ok(
                bookingService.countBookingsByStatus(status));
    }

    // ================= BETWEEN DATES =================
    @GetMapping("/between-dates")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bookings between dates",
            description = "Admin report by date range")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getBookingsBetweenDates(
            @RequestParam String start,
            @RequestParam String end) {

        return ResponseEntity.ok(
                bookingService.getBookingsBetweenDates(
                        LocalDateTime.parse(start),
                        LocalDateTime.parse(end)));
    }
}