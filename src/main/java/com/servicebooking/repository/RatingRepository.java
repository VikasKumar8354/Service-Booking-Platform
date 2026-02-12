package com.servicebooking.repository;

import com.servicebooking.entity.Rating;
import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByBooking(Booking booking);
    Page<Rating> findByProvider(ProviderProfile provider, Pageable pageable);
    Page<Rating> findByProviderId(Long providerId, Pageable pageable);
    
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.provider.id = :providerId")
    Double getAverageRatingForProvider(@Param("providerId") Long providerId);
}
