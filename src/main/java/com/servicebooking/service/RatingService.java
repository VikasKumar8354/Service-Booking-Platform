package com.servicebooking.service;

import com.servicebooking.dto.response.*;
import com.servicebooking.entity.*;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    // ================= SUBMIT =================
    @Transactional
    public ApiResponse<RatingResponseDTO> submitRating(Map<String, Object> request) {

        Long bookingId = Long.valueOf(request.get("bookingId").toString());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getProvider() == null) {
            throw new ResourceNotFoundException("No provider assigned");
        }

        Rating rating = new Rating();
        rating.setBooking(booking);
        rating.setProvider(booking.getProvider());
        rating.setStars(Integer.parseInt(request.get("stars").toString()));
        rating.setComment(request.getOrDefault("comment", "").toString());

        Rating saved = ratingRepository.save(rating);

        updateProviderRating(booking.getProvider().getId());

        return ApiResponse.success("Rating submitted", mapToDTO(saved));
    }

    // ================= DTO MAPPER =================
    private RatingResponseDTO mapToDTO(Rating rating) {

        Booking booking = rating.getBooking();

        return RatingResponseDTO.builder()
                .ratingId(rating.getId())
                .bookingId(booking.getId())
                .service(
                        booking.getService() != null
                                ? booking.getService().getName()
                                : "N/A"
                )
                .stars(rating.getStars())
                .comment(rating.getComment())
                .ratedBy(
                        booking.getCustomer()
                                .getUser()
                                .getName()
                )
                .createdAt(rating.getCreatedAt())
                .build();
    }

    // ================= UPDATE PROVIDER AVG =================
    private void updateProviderRating(Long providerId) {

        Double avg = ratingRepository.getAverageRatingForProvider(providerId);

        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        provider.setRating(avg != null ? avg : 0.0);
        providerProfileRepository.save(provider);
    }

    // ================= GET RATINGS =================
    public ApiResponse<PageResponse<RatingResponseDTO>> getProviderRatings(
            Long providerId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Rating> ratingPage =
                ratingRepository.findByProviderId(providerId, pageable);

        return buildPageResponse(ratingPage, "Ratings fetched");
    }

    public ApiResponse<Double> getAverageRating(Long providerId) {

        Double avg =
                ratingRepository.getAverageRatingForProvider(providerId);

        return ApiResponse.success("Average rating", avg != null ? avg : 0.0);
    }

    public ApiResponse<PageResponse<RatingResponseDTO>> getLowRatings(
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

    public ApiResponse<PageResponse<RatingResponseDTO>> getTopRatings(
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

    // ================= PAGE BUILDER =================
    private ApiResponse<PageResponse<RatingResponseDTO>> buildPageResponse(
            Page<Rating> ratingPage,
            String message) {

        List<RatingResponseDTO> dtoList =
                ratingPage.getContent()
                        .stream()
                        .map(this::mapToDTO)
                        .toList();

        PageResponse<RatingResponseDTO> response =
                new PageResponse<>(
                        dtoList,
                        ratingPage.getNumber(),
                        ratingPage.getSize(),
                        ratingPage.getTotalElements(),
                        ratingPage.getTotalPages(),
                        ratingPage.isLast()
                );

        return ApiResponse.success(message, response);
    }
}