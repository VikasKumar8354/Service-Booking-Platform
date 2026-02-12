package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.Booking;
import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.entity.Rating;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.BookingRepository;
import com.servicebooking.repository.ProviderProfileRepository;
import com.servicebooking.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Transactional
    public ApiResponse<Rating> submitRating(Map<String, Object> request) {
        Long bookingId = Long.valueOf(request.get("bookingId").toString());
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getProvider() == null) {
            throw new ResourceNotFoundException("No provider assigned to this booking");
        }

        Rating rating = new Rating();
        rating.setBooking(booking);
        rating.setProvider(booking.getProvider());
        rating.setStars(Integer.valueOf(request.get("stars").toString()));
        rating.setComment(request.getOrDefault("comment", "").toString());

        rating = ratingRepository.save(rating);

        // Update provider's average rating
        updateProviderRating(booking.getProvider().getId());

        return ApiResponse.success("Rating submitted successfully", rating);
    }

    private void updateProviderRating(Long providerId) {
        Double avgRating = ratingRepository.getAverageRatingForProvider(providerId);
        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        provider.setRating(avgRating != null ? avgRating : 0.0);
        providerProfileRepository.save(provider);
    }

    public ApiResponse<PageResponse<Rating>> getProviderRatings(Long providerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rating> ratingPage = ratingRepository.findByProviderId(providerId, pageable);

        PageResponse<Rating> response = new PageResponse<>(
                ratingPage.getContent(),
                ratingPage.getNumber(),
                ratingPage.getSize(),
                ratingPage.getTotalElements(),
                ratingPage.getTotalPages(),
                ratingPage.isLast()
        );

        return ApiResponse.success("Ratings fetched successfully", response);
    }
}
