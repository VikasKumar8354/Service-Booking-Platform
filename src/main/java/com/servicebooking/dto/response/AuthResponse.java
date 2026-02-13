package com.servicebooking.dto.response;

import com.servicebooking.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
//    private String tokenType = "Bearer";
    private Long userId;
//    private String name;
//    private String mobileNumber;
    private UserRole role;

    public AuthResponse(String token, Long userId, String name, String mobileNumber, UserRole role) {
        this.token = token;
        this.userId = userId;
//        this.name = name;
//        this.mobileNumber = mobileNumber;
        this.role = role;
    }
}
