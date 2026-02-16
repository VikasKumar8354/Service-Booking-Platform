package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.Rating;
import com.servicebooking.service.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings & Reviews", description = "Rating and review management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // ================= SUBMIT RATING =================
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','CUSTOMER','ADMIN')")
    @Operation(
            summary = "Submit Rating",
            description = "Logged-in customer submits rating & review for a completed booking"
    )
    public ResponseEntity<ApiResponse<Rating>> submitRating(
            @RequestBody Map<String, Object> request) {

        return ResponseEntity.ok(
                ratingService.submitRating(request)
        );
    }

    // ================= ALL PROVIDER RATINGS =================
    @GetMapping("/provider/{providerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get Provider Ratings",
            description = "Fetch all ratings for a provider with pagination"
    )
    public ResponseEntity<ApiResponse<PageResponse<Rating>>> getProviderRatings(
            @PathVariable Long providerId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size) {

        return ResponseEntity.ok(
                ratingService.getProviderRatings(providerId, page, size)
        );
    }

    // ================= AVERAGE RATING =================
    @GetMapping("/provider/{providerId}/average")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Average Rating",
            description = "Fetch average rating of a provider"
    )
    public ResponseEntity<ApiResponse<Double>> getAverageRating(
            @PathVariable Long providerId) {

        return ResponseEntity.ok(
                ratingService.getAverageRating(providerId)
        );
    }

    // ================= LOW RATINGS =================
    @GetMapping("/provider/{providerId}/low")
    @PreAuthorize("hasAnyRole('ADMIN','PROVIDER')")
    @Operation(
            summary = "Get Low Ratings",
            description = "Fetch low ratings (1-3 stars) for a provider"
    )
    public ResponseEntity<ApiResponse<PageResponse<Rating>>> getLowRatings(
            @PathVariable Long providerId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size) {

        return ResponseEntity.ok(
                ratingService.getLowRatings(providerId, page, size)
        );
    }

    // ================= TOP RATINGS =================
    @GetMapping("/provider/{providerId}/top")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get Top Ratings",
            description = "Fetch top ratings (4-5 stars) for a provider"
    )
    public ResponseEntity<ApiResponse<PageResponse<Rating>>> getTopRatings(
            @PathVariable Long providerId,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size) {

        return ResponseEntity.ok(
                ratingService.getTopRatings(providerId, page, size)
        );
    }
}