package com.servicebooking.dto.response;

import com.servicebooking.enums.ProviderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProviderProfileResponseDTO {

    private Long id;
    private String selectedServices;
    private ProviderStatus status;
    private BigDecimal totalEarnings;
    private Integer completedJobs;
    private Double rating;
    private Boolean documentAvailable;
}
