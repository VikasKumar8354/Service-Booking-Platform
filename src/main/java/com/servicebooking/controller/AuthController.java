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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP to mobile number")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/recover-password")
    @Operation(summary = "Recover password using recovery PIN")
    public ResponseEntity<ApiResponse<String>> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
        return ResponseEntity.ok(authService.recoverPassword(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}
