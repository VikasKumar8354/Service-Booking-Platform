package com.servicebooking.dto.response;

import com.servicebooking.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long userId;
    private UserRole role;
    private String token;

}
