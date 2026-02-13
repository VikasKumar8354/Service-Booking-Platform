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


import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@SecurityRequirement(name = "bearerAuth")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> updateProfile(
            @RequestParam MultipartFile documentFile,
            @RequestParam(required = false) String selectedServices) {

        return ResponseEntity.ok(
                providerService.updateProfile(documentFile, selectedServices)
        );
    }

    @GetMapping("/document")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<byte[]> downloadDocument() {

        ProviderProfile p = providerService.getProviderDocument();

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + p.getDocumentName() + "\"")
                .header("Content-Type", p.getDocumentType())
                .body(p.getDocumentData());
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> updateStatus(
            @RequestParam ProviderStatus status) {

        return ResponseEntity.ok(providerService.updateStatus(status));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> approveProvider(
            @PathVariable Long id) {

        return ResponseEntity.ok(providerService.approveProvider(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProviderProfileResponseDTO>> rejectProvider(
            @PathVariable Long id) {

        return ResponseEntity.ok(providerService.rejectProvider(id));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ProviderProfileResponseDTO>>> getPendingProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                providerService.getPendingProviders(page, size)
        );
    }
}
