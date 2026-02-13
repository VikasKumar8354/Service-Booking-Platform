package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.dto.response.ProviderProfileResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProviderService {

    @Autowired
    private ProviderProfileRepository providerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;


    // ✅ BLOB upload version
    @Transactional
    public ApiResponse<ProviderProfileResponseDTO> updateProfile(
            MultipartFile documentFile,
            String selectedServices) {

        User currentUser = userService.getCurrentUser();

        ProviderProfile provider = providerRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        try {
            if (documentFile != null && !documentFile.isEmpty()) {

                String type = documentFile.getContentType();

                if (type == null || !(type.equalsIgnoreCase("application/pdf")
                        || type.equalsIgnoreCase("image/jpeg")
                        || type.equalsIgnoreCase("image/png"))) {
                    throw new RuntimeException("Only PDF/JPG/PNG allowed");
                }

                provider.setDocumentData(documentFile.getBytes());
                provider.setDocumentType(type);
                provider.setDocumentName(documentFile.getOriginalFilename());
            }

        } catch (Exception exception) {
            throw new RuntimeException("Upload failed");
        }

        if (selectedServices != null) {
            provider.setSelectedServices(selectedServices);
        }

        providerRepository.save(provider);

        return ApiResponse.success(
                "Profile updated successfully",
                toDTO(provider)
        );
    }



    // ✅ download helper
    public ProviderProfile getProviderDocument() {
        User user = userService.getCurrentUser();
        return providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
    }


    // ===== YOUR EXISTING METHODS (unchanged) =====

    @Transactional
    public ApiResponse<ProviderProfileResponseDTO> updateStatus(ProviderStatus status) {

        User user = userService.getCurrentUser();

        ProviderProfile provider = providerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        provider.setStatus(status);

        providerRepository.save(provider);

        return ApiResponse.success(
                "Status updated",
                toDTO(provider)
        );
    }


    @Transactional
    public ApiResponse<ProviderProfileResponseDTO> approveProvider(Long id) {

        ProviderProfile profile = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        profile.setStatus(ProviderStatus.APPROVED);
        providerRepository.save(profile);

        notificationService.createNotification(
                profile.getUser().getId(),
                "Approved",
                "Your provider application is approved"
        );

        return ApiResponse.success("Approved", toDTO(profile));
    }


    @Transactional
    public ApiResponse<ProviderProfileResponseDTO> rejectProvider(Long id) {

        ProviderProfile profile = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        profile.setStatus(ProviderStatus.REJECTED);
        providerRepository.save(profile);

        notificationService.createNotification(
                profile.getUser().getId(),
                "Rejected",
                "Your provider application rejected"
        );

        return ApiResponse.success("Rejected", toDTO(profile));
    }


    public ApiResponse<PageResponse<ProviderProfileResponseDTO>> getPendingProviders(int page, int size) {

        Page<ProviderProfile> profiles = providerRepository.findByStatus(
                ProviderStatus.PENDING_APPROVAL,
                PageRequest.of(page, size));

        PageResponse<ProviderProfileResponseDTO> response =
                new PageResponse<>(
                        profiles.getContent().stream().map(this::toDTO).toList(),
                        profiles.getNumber(),
                        profiles.getSize(),
                        profiles.getTotalElements(),
                        profiles.getTotalPages(),
                        profiles.isLast()
                );

        return ApiResponse.success("Fetched", response);
    }

    private ProviderProfileResponseDTO toDTO(ProviderProfile profile) {
        return new ProviderProfileResponseDTO(
                profile.getId(),
                profile.getSelectedServices(),
                profile.getStatus(),
                profile.getTotalEarnings(),
                profile.getCompletedJobs(),
                profile.getRating(),
                profile.getDocumentData() != null
        );
    }
}
