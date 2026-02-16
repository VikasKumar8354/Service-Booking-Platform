package com.servicebooking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerProfileResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String imageName;
    private String imageUrl;

}