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
import com.servicebooking.util.OtpService;
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

    @Autowired
    private OtpService otpService;

    // ================= REGISTER =================
    @Transactional
    public ApiResponse<String> register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email))
            throw new BadRequestException("Email already registered");

        User user = new User();
        user.setName(request.getName());
        user.setMobileNumber(request.getMobileNumber());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        user = userRepository.save(user);

        // create profile
        switch (request.getRole()) {

            case CUSTOMER -> {
                CustomerProfile profile = new CustomerProfile();
                profile.setUser(user);
                customerProfileRepository.save(profile);
            }

            case PROVIDER -> {
                ProviderProfile profile = new ProviderProfile();
                profile.setUser(user);
                profile.setStatus(ProviderStatus.PENDING_APPROVAL);
                providerProfileRepository.save(profile);
            }
        }

        return ApiResponse.success("Registered successfully. You can login now.");
    }

    // ================= LOGIN =================
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid credentials"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        String token = tokenProvider.generateToken(
                email,
                user.getRole().name()
        );

        return ApiResponse.success("Login successful",
                new AuthResponse(
                        user.getId(),
                        user.getRole(),
                        "Bearer " + token
                ));
    }

    // ================= SEND OTP =================
    @Transactional
    public ApiResponse<String> sendForgotOtp(ForgotPasswordRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Email not found"));

        String otp = generateOtp();

        otpService.storeOtp(email, otp);

        emailService.sendEmail(
                email,
                "Password Reset OTP",
                "Your OTP is: " + otp
        );

        return ApiResponse.success("OTP sent to email");
    }

    // ================= RESET PASSWORD =================
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Email not found"));

        boolean valid = otpService.validateOtp(email, request.getOtp());

        if (!valid)
            throw new BadRequestException("Invalid or expired OTP");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

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