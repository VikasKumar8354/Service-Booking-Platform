package com.servicebooking.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    @Value("${otp.hardcoded.value}")
    private String hardcodedOtp;

    @Value("${otp.recovery.pin}")
    private String recoveryPin;

    public String generateOtp(String mobileNumber) {
        // In production, integrate with SMS gateway
        System.out.println("OTP for " + mobileNumber + ": " + hardcodedOtp);
        return hardcodedOtp;
    }

    public boolean verifyOtp(String mobileNumber, String otp) {
        return hardcodedOtp.equals(otp);
    }

    public boolean verifyRecoveryPin(String pin) {
        return recoveryPin.equals(pin);
    }

    public String getHardcodedOtp() {
        return hardcodedOtp;
    }

    public String getRecoveryPin() {
        return recoveryPin;
    }
}
