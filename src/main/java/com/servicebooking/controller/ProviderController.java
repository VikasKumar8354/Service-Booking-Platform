package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.enums.ProviderStatus;
import com.servicebooking.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@Tag(name = "Providers", description = "Provider management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update provider profile")
    public ResponseEntity<ApiResponse<ProviderProfile>> updateProfile(@RequestBody Map<String, String> updates) {
        return ResponseEntity.ok(providerService.updateProfile(updates));
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update provider status (ONLINE/OFFLINE)")
    public ResponseEntity<ApiResponse<ProviderProfile>> updateStatus(@RequestParam ProviderStatus status) {
        return ResponseEntity.ok(providerService.updateStatus(status));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve provider (Admin only)")
    public ResponseEntity<ApiResponse<ProviderProfile>> approveProvider(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.approveProvider(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject provider (Admin only)")
    public ResponseEntity<ApiResponse<ProviderProfile>> rejectProvider(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.rejectProvider(id));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending provider applications")
    public ResponseEntity<ApiResponse<PageResponse<ProviderProfile>>> getPendingProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(providerService.getPendingProviders(page, size));
    }
}
