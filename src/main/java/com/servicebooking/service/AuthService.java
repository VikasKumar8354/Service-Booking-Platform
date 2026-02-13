package com.servicebooking.service;

import com.servicebooking.dto.request.*;
import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.AuthResponse;
import com.servicebooking.entity.*;
import com.servicebooking.enums.ProviderStatus;
import com.servicebooking.exception.BadRequestException;
import com.servicebooking.exception.UnauthorizedException;
import com.servicebooking.repository.*;
import com.servicebooking.security.JwtTokenProvider;
import com.servicebooking.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;


    // ================= REGISTER =================
    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already registered");

        User user = new User();
        user.setName(request.getName());
        user.setMobileNumber(request.getMobileNumber()); // Add mobile number
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        user = userRepository.save(user);

        // create profile
        switch (request.getRole()) {
            case CUSTOMER -> {
                CustomerProfile cp = new CustomerProfile();
                cp.setUser(user);
                customerProfileRepository.save(cp);
            }
            case PROVIDER -> {
                ProviderProfile pp = new ProviderProfile();
                pp.setUser(user);
                pp.setStatus(ProviderStatus.PENDING_APPROVAL);
                providerProfileRepository.save(pp);
            }
        }

        return ApiResponse.success("Registered successfully. You can login now.");
    }


    // ================= LOGIN =================
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String token = tokenProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ApiResponse.success("Login successful", response);
    }


    // ================= SEND OTP (FORGOT PASSWORD) =================
    @Transactional
    public ApiResponse<String> sendForgotOtp(ForgotPasswordRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email not found"));

        // Generate OTP and set expiry
        String otp = generateOtp();
        user.setResetOtp(otp);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        // Send OTP via email
        emailService.sendEmail(request.getEmail(), "Password Reset OTP", "Your OTP is: " + otp);

        return ApiResponse.success("OTP sent to email");
    }


    // ================= RESET PASSWORD =================
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email not found"));

        // Validate OTP
        if (user.getResetOtp() == null || !user.getResetOtp().equals(request.getOtp()))
            throw new BadRequestException("Invalid OTP");

        if (user.getResetOtpExpiry() == null || user.getResetOtpExpiry().isBefore(LocalDateTime.now()))
            throw new BadRequestException("OTP expired");

        // Reset password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);

        userRepository.save(user);

        return ApiResponse.success("Password reset successful");
    }


    // ================= LOGOUT =================
    public ApiResponse<String> logout() {
        return ApiResponse.success("Logout successful");
    }


    // ================= OTP GENERATOR =================
    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}