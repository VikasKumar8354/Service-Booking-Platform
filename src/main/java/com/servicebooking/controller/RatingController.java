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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Ratings & Reviews", description = "Rating and review management")
@SecurityRequirement(name = "bearerAuth")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    @Operation(summary = "Submit a rating")
    public ResponseEntity<ApiResponse<Rating>> submitRating(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(ratingService.submitRating(request));
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get provider ratings")
    public ResponseEntity<ApiResponse<PageResponse<Rating>>> getProviderRatings(
            @PathVariable Long providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ratingService.getProviderRatings(providerId, page, size));
    }
}
