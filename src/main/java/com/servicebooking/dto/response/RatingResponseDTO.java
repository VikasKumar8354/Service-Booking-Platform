package com.servicebooking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponseDTO {

    private Long ratingId;
    private Long bookingId;
    private String service;
    private int stars;
    private String comment;
    private String ratedBy;
    private LocalDateTime createdAt;
}