package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Payment;
import com.servicebooking.service.PaymentService;
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
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ================= RECORD PAYMENT =================
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(
            summary = "Record a payment",
            description = "Create a new payment for a booking. Accessible by USER or ADMIN."
    )
    public ResponseEntity<ApiResponse<Payment>> recordPayment(
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(paymentService.recordPayment(request));
    }

    // ================= MARK PAYMENT COMPLETE =================
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @Operation(
            summary = "Mark payment as complete",
            description = "Update payment status to COMPLETED. Accessible by ADMIN or PROVIDER."
    )
    public ResponseEntity<ApiResponse<Payment>> markComplete(
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.markComplete(id));
    }

    // ================= PAYMENT HISTORY =================
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get payment history",
            description = "Fetch paginated payment history for the logged-in user."
    )
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                paymentService.getPaymentHistory(page, size)
        );
    }
}