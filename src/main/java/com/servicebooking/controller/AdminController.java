package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin management and analytics APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')") // applies to all endpoints
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    @Operation(
            summary = "Get admin dashboard statistics",
            description = "Returns overall platform statistics like users, bookings, revenue, etc."
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        return ResponseEntity.ok(
                adminService.getDashboardStats()
        );
    }

    // ================= MONTHLY REPORT =================
    @GetMapping("/reports/monthly")
    @Operation(
            summary = "Get monthly analytics report",
            description = "Returns monthly aggregated reports for bookings, revenue, and growth"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyReport() {
        return ResponseEntity.ok(
                adminService.getMonthlyReport()
        );
    }
}