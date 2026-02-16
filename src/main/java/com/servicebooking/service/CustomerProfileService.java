package com.servicebooking.service;

import com.servicebooking.dto.response.CustomerProfileResponseDTO;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.entity.User;
import com.servicebooking.repository.CustomerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustomerProfileService {

    @Autowired
    private CustomerProfileRepository repository;

    @Autowired
    private UserService userService;

    // ✅ Get My Profile
    public CustomerProfile getMyProfile() {
        User user = userService.getCurrentUser();

        return repository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    // ✅ Update profile with email + optional image
    public CustomerProfileResponseDTO updateProfile(String email, MultipartFile imageFile) {

        try {
            User user = userService.getCurrentUser();

            CustomerProfile profile = repository.findByUser(user)
                    .orElse(new CustomerProfile());

            profile.setUser(user);

            // ✅ Update email only if provided
            if (email != null && !email.isBlank()) {
                profile.setEmail(email);
                user.setEmail(email); // optional if user email also needs update
            }

            // ✅ If image uploaded
            if (imageFile != null && !imageFile.isEmpty()) {
                profile.setProfileImageData(imageFile.getBytes());
                profile.setImageName(imageFile.getOriginalFilename());
                profile.setImageType(imageFile.getContentType());
            }

            CustomerProfile savedProfile = repository.save(profile);

            // ✅ Convert Entity → DTO
            return CustomerProfileResponseDTO.builder()
                    .id(savedProfile.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .mobileNumber(user.getMobileNumber())
                    .imageName(savedProfile.getImageName())
                    .imageUrl("/api/customer/profile/image")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Profile update failed: " + e.getMessage());
        }
    }

    // ✅ Download image
    public CustomerProfile getProfileWithImage() {
        return getMyProfile();
    }

    // ✅ Delete profile
    public void deleteProfile() {
        User user = userService.getCurrentUser();

        CustomerProfile profile = repository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        repository.delete(profile);
    }
}
