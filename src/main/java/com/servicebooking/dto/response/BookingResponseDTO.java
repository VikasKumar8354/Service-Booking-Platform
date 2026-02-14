package com.servicebooking.dto.response;

import com.servicebooking.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDTO {

    private Long bookingId;

    private CustomerDTO customer;
    private ProviderDTO provider;
    private ServiceDTO service;

    private LocalDateTime bookingDateTime;
    private String location;

    private BookingStatus status;
    private BigDecimal amount;

    private LocalDateTime createdAt;
}