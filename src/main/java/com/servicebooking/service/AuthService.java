package com.servicebooking.service;

import com.servicebooking.dto.request.*;
import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.AuthResponse;
import com.servicebooking.entity.User;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.enums.ProviderStatus;
import com.servicebooking.exception.BadRequestException;
import com.servicebooking.exception.UnauthorizedException;
import com.servicebooking.repository.UserRepository;
import com.servicebooking.repository.CustomerProfileRepository;
import com.servicebooking.repository.ProviderProfileRepository;
import com.servicebooking.security.JwtTokenProvider;
import com.servicebooking.util.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private OtpService otpService;

    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus("ACTIVE");

        user = userRepository.save(user);

        // Create profile based on role
        switch (request.getRole()) {
            case CUSTOMER:
                CustomerProfile customerProfile = new CustomerProfile();
                customerProfile.setUser(user);
                customerProfileRepository.save(customerProfile);
                break;
            case PROVIDER:
                ProviderProfile providerProfile = new ProviderProfile();
                providerProfile.setUser(user);
                providerProfile.setStatus(ProviderStatus.PENDING_APPROVAL);
                providerProfileRepository.save(providerProfile);
                break;
        }

        String token = tokenProvider.generateTokenFromMobile(user.getMobileNumber());
        AuthResponse authResponse = new AuthResponse(token, user.getId(), user.getName(), 
                                                     user.getMobileNumber(), user.getRole());

        return ApiResponse.success("Registration successful", authResponse);
    }

    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getMobileNumber(),
                            request.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);

            User user = userRepository.findByMobileNumber(request.getMobileNumber())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            AuthResponse authResponse = new AuthResponse(token, user.getId(), user.getName(), 
                                                        user.getMobileNumber(), user.getRole());

            return ApiResponse.success("Login successful", authResponse);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid mobile number or password");
        }
    }

    public ApiResponse<String> sendOtp(OtpRequest request) {
        if (!userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number not registered");
        }

        String otp = otpService.generateOtp(request.getMobileNumber());
        return ApiResponse.success("OTP sent successfully. Use: " + otp, otp);
    }

    public ApiResponse<String> verifyOtp(VerifyOtpRequest request) {
        if (!userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number not registered");
        }

        if (!otpService.verifyOtp(request.getMobileNumber(), request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        return ApiResponse.success("OTP verified successfully");
    }

    @Transactional
    public ApiResponse<String> recoverPassword(RecoverPasswordRequest request) {
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new BadRequestException("Mobile number not found"));

        if (!otpService.verifyRecoveryPin(request.getRecoveryPin())) {
            throw new BadRequestException("Invalid recovery PIN");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.success("Password recovered successfully");
    }

    public ApiResponse<String> logout() {
        return ApiResponse.success("Logout successful");
    }
}
