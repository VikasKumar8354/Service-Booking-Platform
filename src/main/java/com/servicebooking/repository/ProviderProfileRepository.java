package com.servicebooking.repository;

import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.entity.User;
import com.servicebooking.enums.ProviderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    Optional<ProviderProfile> findByUser(User user);
    Optional<ProviderProfile> findByUserId(Long userId);
    Page<ProviderProfile> findByStatus(ProviderStatus status, Pageable pageable);
}
