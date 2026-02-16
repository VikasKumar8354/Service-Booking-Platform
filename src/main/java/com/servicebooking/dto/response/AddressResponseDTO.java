package com.servicebooking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponseDTO {

    private Long id;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private Boolean isDefault;
}