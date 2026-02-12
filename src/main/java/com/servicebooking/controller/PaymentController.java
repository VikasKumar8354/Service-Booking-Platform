package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Payment;
import com.servicebooking.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    @Operation(summary = "Record a payment")
    public ResponseEntity<ApiResponse<Payment>> recordPayment(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(paymentService.recordPayment(request));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark payment as complete")
    public ResponseEntity<ApiResponse<Payment>> markComplete(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.markComplete(id));
    }

    @GetMapping("/history")
    @Operation(summary = "Get payment history")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(page, size));
    }
}
