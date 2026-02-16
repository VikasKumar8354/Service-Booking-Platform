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

import java.util.List;
import java.util.Map;
@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    // ================= SUBMIT RATING =================
    @Transactional
    public ApiResponse<Rating> submitRating(Map<String, Object> request) {

        Long bookingId = Long.valueOf(request.get("bookingId").toString());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getProvider() == null) {
            throw new ResourceNotFoundException("No provider assigned");
        }

        Rating rating = new Rating();
        rating.setBooking(booking);
        rating.setProvider(booking.getProvider());
        rating.setStars(Integer.valueOf(request.get("stars").toString()));
        rating.setComment(request.getOrDefault("comment", "").toString());

        ratingRepository.save(rating);

        updateProviderRating(booking.getProvider().getId());

        return ApiResponse.success("Rating submitted", rating);
    }

    // ================= UPDATE AVG =================
    private void updateProviderRating(Long providerId) {

        Double avg = ratingRepository.getAverageRatingForProvider(providerId);

        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        provider.setRating(avg != null ? avg : 0.0);

        providerProfileRepository.save(provider);
    }

    // ================= GET ALL RATINGS =================
    public ApiResponse<PageResponse<Rating>> getProviderRatings(
            Long providerId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Rating> ratingPage =
                ratingRepository.findByProviderId(providerId, pageable);

        return buildPageResponse(ratingPage, "Ratings fetched");
    }

    // ================= AVERAGE =================
    public ApiResponse<Double> getAverageRating(Long providerId) {

        Double avg =
                ratingRepository.getAverageRatingForProvider(providerId);

        return ApiResponse.success(
                "Average rating",
                avg != null ? avg : 0.0
        );
    }

    // ================= LOW RATINGS =================
    public ApiResponse<PageResponse<Rating>> getLowRatings(
            Long providerId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Rating> ratingPage =
                ratingRepository.findByProviderIdAndStarsIn(
                        providerId,
                        List.of(1,2,3),
                        pageable
                );

        return buildPageResponse(ratingPage, "Low ratings fetched");
    }

    // ================= TOP RATINGS =================
    public ApiResponse<PageResponse<Rating>> getTopRatings(
            Long providerId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Rating> ratingPage =
                ratingRepository.findByProviderIdAndStarsGreaterThanEqual(
                        providerId,
                        4,
                        pageable
                );

        return buildPageResponse(ratingPage, "Top ratings fetched");
    }

    // ================= COMMON PAGE BUILDER =================
    private ApiResponse<PageResponse<Rating>> buildPageResponse(
            Page<Rating> ratingPage,
            String message) {

        PageResponse<Rating> response = new PageResponse<>(
                ratingPage.getContent(),
                ratingPage.getNumber(),
                ratingPage.getSize(),
                ratingPage.getTotalElements(),
                ratingPage.getTotalPages(),
                ratingPage.isLast()
        );

        return ApiResponse.success(message, response);
    }
}