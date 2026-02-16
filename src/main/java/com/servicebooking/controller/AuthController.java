package com.servicebooking.controller;

import com.servicebooking.dto.request.*;
import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.AuthResponse;
import com.servicebooking.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Email-based Authentication APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ================= REGISTER =================
    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Create a new account using email and password"
    )
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticate user and return JWT token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // ================= FORGOT PASSWORD =================
    @PostMapping("/forgot-password")
    @Operation(
            summary = "Send OTP for password reset",
            description = "Sends OTP to registered email for password reset"
    )
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(authService.sendForgotOtp(request));
    }

    // ================= RESET PASSWORD =================
    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Reset password using email + OTP verification"
    )
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.resetPassword(request));
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Logout user",
            description = "Invalidate JWT token / clear session"
    )
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}