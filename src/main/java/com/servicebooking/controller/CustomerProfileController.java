package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customer")
@Tag(name = "Customer Profile")
@SecurityRequirement(name = "bearerAuth")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService service;

    // ✅ Get Profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfile>> getProfile() {
        return ResponseEntity.ok(
                ApiResponse.success("Profile fetched", service.getMyProfile())
        );
    }

    // ✅ Update Profile (email + image BLOB)
    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerProfile>> updateProfile(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile imageFile) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Profile updated",
                        service.updateProfile(email, imageFile)
                )
        );
    }

    // ✅ Download Profile Image
    @GetMapping("/profile/image")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> downloadImage() {

        CustomerProfile profile = service.getProfileWithImage();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profile.getImageType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + profile.getImageName() + "\"")
                .body(profile.getProfileImageData());
    }

    // ✅ Delete Profile
    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> deleteProfile() {
        service.deleteProfile();
        return ResponseEntity.ok(ApiResponse.success("Profile deleted", null));
    }
}
