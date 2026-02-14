package com.servicebooking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderDTO {

    private Long id;
    private String name;
    private Double rating;
}