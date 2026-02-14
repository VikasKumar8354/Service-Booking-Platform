package com.servicebooking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {

    private Long id;
    private String name;
    private String email;
    private String mobile;
}