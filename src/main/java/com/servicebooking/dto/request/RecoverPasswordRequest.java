package com.servicebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoverPasswordRequest {
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    @NotBlank(message = "Recovery PIN is required")
    private String recoveryPin;

    @NotBlank(message = "New password is required")
    private String newPassword;
}
