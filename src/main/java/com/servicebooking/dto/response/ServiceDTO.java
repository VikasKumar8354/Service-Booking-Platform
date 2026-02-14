package com.servicebooking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceDTO {

    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
}