package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.dto.response.ProviderProfileResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/provider")
@Tag(name = "Provider Management", description = "APIs for provider profile and approval workflow")
@SecurityRequirement(name = "bearerAuth")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    // ================= UPDATE PROFILE =================
    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Update provider profile",
            description = "Upload/update provider documents and select services. Only PROVIDER allowed."
    )
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> updateProfile(
            @RequestParam MultipartFile documentFile,
            @RequestParam(required = false) String selectedServices) {

        return ResponseEntity.ok(
                providerService.updateProfile(documentFile, selectedServices)
        );
    }

    // ================= DOWNLOAD DOCUMENT =================
    @GetMapping("/document")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Download provider document",
            description = "Allows provider to download their uploaded verification document"
    )
    public ResponseEntity<byte[]> downloadDocument() {

        ProviderProfile p = providerService.getProviderDocument();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + p.getDocumentName() + "\"")
                .header("Content-Type", p.getDocumentType())
                .body(p.getDocumentData());
    }

    // ================= UPDATE STATUS =================
    @PutMapping("/status")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(
            summary = "Update provider availability status",
            description = "Provider can update their current status (ACTIVE, INACTIVE, etc.)"
    )
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> updateStatus(
            @RequestParam ProviderStatus status) {

        return ResponseEntity.ok(providerService.updateStatus(status));
    }

    // ================= APPROVE PROVIDER =================
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Approve provider (ADMIN)",
            description = "Admin approves a provider after verification"
    )
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> approveProvider(
            @PathVariable Long id) {

        return ResponseEntity.ok(providerService.approveProvider(id));
    }

    // ================= REJECT PROVIDER =================
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Reject provider (ADMIN)",
            description = "Admin rejects a provider application"
    )
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> rejectProvider(
            @PathVariable Long id) {

        return ResponseEntity.ok(providerService.rejectProvider(id));
    }

    // ================= PENDING PROVIDERS =================
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get pending providers (ADMIN)",
            description = "Fetch paginated list of providers awaiting approval"
    )
    public ResponseEntity<ApiResponse<PageResponse<ProviderProfileResponseDTO>>> getPendingProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                providerService.getPendingProviders(page, size)
        );
    }
}