package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.User;
import com.servicebooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    // ================= GET PROFILE =================
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN','PROVIDER')")
    @Operation(
            summary = "Get current user profile",
            description = "Returns the profile details of the currently logged-in user"
    )
    public ResponseEntity<ApiResponse<User>> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER','ADMIN','PROVIDER')")
    @Operation(
            summary = "Update current user profile",
            description = "Allows logged-in users to update their profile fields like name, phone, etc."
    )
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @RequestBody Map<String, String> updates) {
        return ResponseEntity.ok(userService.updateProfile(updates));
    }

    // ================= DELETE ACCOUNT =================
    @DeleteMapping("/account")
    @PreAuthorize("hasAnyRole('USER','ADMIN','PROVIDER')")
    @Operation(
            summary = "Delete user account",
            description = "Deletes the account of the currently logged-in user"
    )
    public ResponseEntity<ApiResponse<String>> deleteAccount() {
        return ResponseEntity.ok(userService.deleteAccount());
    }

    // ================= GET ALL USERS (ADMIN) =================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all users (Admin only)",
            description = "Fetch paginated list of all users. Accessible only by ADMIN"
    )
    public ResponseEntity<ApiResponse<PageResponse<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }
}