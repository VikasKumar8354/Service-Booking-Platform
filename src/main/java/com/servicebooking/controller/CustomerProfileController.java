package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.CustomerProfileResponseDTO;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer")
@Tag(name = "Customer Profile", description = "Customer profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService service;

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Get customer profile",
            description = "Fetch the profile details of the logged-in customer"
    )
    public ResponseEntity<ApiResponse<CustomerProfile>> getProfile() {
        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched", service.getMyProfile())
        );
    }

    // ================= UPDATE PROFILE =================
    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Update customer profile",
            description = "Update customer email and/or profile image"
    )
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>> updateProfile(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile updated",
                        service.updateProfile(email, imageFile)
                )
        );
    }

    // ================= DOWNLOAD IMAGE =================
    @GetMapping("/profile/image")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Download profile image",
            description = "Download the customer's profile image"
    )
    public ResponseEntity<byte[]> downloadImage() {

        CustomerProfile profile = service.getProfileWithImage();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profile.getImageType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + profile.getImageName() + "\"")
                .body(profile.getProfileImageData());
    }

    // ================= DELETE PROFILE =================
    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Delete customer profile",
            description = "Delete the logged-in customer's profile permanently"
    )
    public ResponseEntity<ApiResponse<String>> deleteProfile() {
        service.deleteProfile();
        return ResponseEntity.ok(
                ApiResponse.success("Profile deleted", null)
        );
    }
}