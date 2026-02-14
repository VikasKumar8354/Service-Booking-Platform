package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.User;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ FIXED HERE
    public User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    public ApiResponse<User> getProfile() {
        User user = getCurrentUser();
        user.setPassword(null);
        return ApiResponse.success("Profile fetched successfully", user);
    }

    @Transactional
    public ApiResponse<User> updateProfile(Map<String, String> updates) {

        User user = getCurrentUser();

        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }

        user = userRepository.save(user);
        user.setPassword(null);

        return ApiResponse.success("Profile updated successfully", user);
    }

    @Transactional
    public ApiResponse<String> deleteAccount() {

        User user = getCurrentUser();
        user.setStatus("DELETED");
        userRepository.save(user);

        return ApiResponse.success("Account deleted successfully");
    }

    public ApiResponse<PageResponse<User>> getAllUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        userPage.getContent().forEach(u -> u.setPassword(null));

        PageResponse<User> response = new PageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );

        return ApiResponse.success("Users fetched successfully", response);
    }
}