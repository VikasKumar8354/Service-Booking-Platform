package com.servicebooking.util;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    public void storeOtp(String email, String otp) {
        otpStore.put(email,
                new OtpData(otp, LocalDateTime.now().plusMinutes(10)));
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpStore.get(email);

        if (data == null) return false;

        if (data.expiry().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }

        boolean valid = data.otp().equals(otp);

        if (valid) otpStore.remove(email);

        return valid;
    }

    private record OtpData(String otp, LocalDateTime expiry) {}
}