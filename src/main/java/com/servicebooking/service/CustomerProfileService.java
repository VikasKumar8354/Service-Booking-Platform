package com.servicebooking.service;

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
    public CustomerProfile updateProfile(String email, MultipartFile imageFile) {
        try {
            User user = userService.getCurrentUser();

            CustomerProfile profile = repository.findByUser(user)
                    .orElse(new CustomerProfile());

            profile.setUser(user);
            profile.setEmail(email);

            // ✅ If image uploaded
            if (imageFile != null && !imageFile.isEmpty()) {
                profile.setProfileImageData(imageFile.getBytes());
                profile.setImageName(imageFile.getOriginalFilename());
                profile.setImageType(imageFile.getContentType());
            }

            return repository.save(profile);

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
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
