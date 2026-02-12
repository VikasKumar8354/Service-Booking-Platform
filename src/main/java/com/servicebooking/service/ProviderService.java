package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.entity.User;
import com.servicebooking.enums.ProviderStatus;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.ProviderProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProviderService {

    @Autowired
    private ProviderProfileRepository providerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public ApiResponse<ProviderProfile> updateProfile(Map<String, String> updates) {
        User currentUser = userService.getCurrentUser();
        ProviderProfile provider = providerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        if (updates.containsKey("documents")) provider.setDocuments(updates.get("documents"));
        if (updates.containsKey("selectedServices")) provider.setSelectedServices(updates.get("selectedServices"));

        provider = providerRepository.save(provider);
        return ApiResponse.success("Profile updated successfully", provider);
    }

    @Transactional
    public ApiResponse<ProviderProfile> updateStatus(ProviderStatus status) {
        User currentUser = userService.getCurrentUser();
        ProviderProfile provider = providerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        provider.setStatus(status);
        provider = providerRepository.save(provider);

        return ApiResponse.success("Status updated to " + status, provider);
    }

    @Transactional
    public ApiResponse<ProviderProfile> approveProvider(Long providerId) {
        ProviderProfile provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        provider.setStatus(ProviderStatus.APPROVED);
        provider = providerRepository.save(provider);

        notificationService.createNotification(provider.getUser().getId(), 
            "Application Approved", "Your provider application has been approved");

        return ApiResponse.success("Provider approved successfully", provider);
    }

    @Transactional
    public ApiResponse<ProviderProfile> rejectProvider(Long providerId) {
        ProviderProfile provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        provider.setStatus(ProviderStatus.REJECTED);
        provider = providerRepository.save(provider);

        notificationService.createNotification(provider.getUser().getId(), 
            "Application Rejected", "Your provider application has been rejected");

        return ApiResponse.success("Provider rejected", provider);
    }

    public ApiResponse<PageResponse<ProviderProfile>> getPendingProviders(int page, int size) {
        Page<ProviderProfile> providerPage = providerRepository.findByStatus(
            ProviderStatus.PENDING_APPROVAL, PageRequest.of(page, size));

        PageResponse<ProviderProfile> response = new PageResponse<>(
                providerPage.getContent(),
                providerPage.getNumber(),
                providerPage.getSize(),
                providerPage.getTotalElements(),
                providerPage.getTotalPages(),
                providerPage.isLast()
        );

        return ApiResponse.success("Pending providers fetched", response);
    }
}
