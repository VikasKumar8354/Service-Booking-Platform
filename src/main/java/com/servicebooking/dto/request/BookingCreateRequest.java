package com.servicebooking.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingCreateRequest {

    private Long serviceId;
    private LocalDateTime bookingDateTime;
    private String location;
}