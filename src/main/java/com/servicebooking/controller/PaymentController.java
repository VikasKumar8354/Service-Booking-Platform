package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Payment;
import com.servicebooking.enums.PaymentStatus;
import com.servicebooking.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ================= RECORD PAYMENT =================
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @Operation(
            summary = "Record payment",
            description = "Create a payment for a booking"
    )
    public ResponseEntity<ApiResponse<Payment>> recordPayment(
            @RequestBody Map<String, Object> request) {

        return ResponseEntity.ok(
                paymentService.recordPayment(request));
    }

    // ================= MARK COMPLETE =================
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @Operation(
            summary = "Mark payment complete",
            description = "Update payment status to COMPLETED"
    )
    public ResponseEntity<ApiResponse<Payment>> markComplete(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.markComplete(id));
    }

    // ================= PAYMENT HISTORY =================
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(
            summary = "Payment history",
            description = "Fetch paginated payment history"
    )
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                paymentService.getPaymentHistory(page, size));
    }

    // ================= GET BY BOOKING =================
    @GetMapping("/by-booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(
            summary = "Payment by booking",
            description = "Fetch payment details for a booking"
    )
    public ResponseEntity<ApiResponse<Payment>> getPaymentByBooking(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBooking(bookingId));
    }

    // ================= BY STATUS =================
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Payments by status",
            description = "Admin fetches payments by status"
    )
    public ResponseEntity<ApiResponse<?>> getPaymentsByStatus(
            @RequestParam PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByStatus(status, page, size));
    }

    // ================= REVENUE REPORT =================
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Revenue report",
            description = "Calculate revenue between dates"
    )
    public ResponseEntity<ApiResponse<BigDecimal>> getRevenueBetweenDates(
            @RequestParam String start,
            @RequestParam String end) {

        return ResponseEntity.ok(
                paymentService.getRevenueBetweenDates(
                        LocalDateTime.parse(start),
                        LocalDateTime.parse(end)));
    }
}