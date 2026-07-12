package com.bnroll.auth.event.dto;

import com.bnroll.enums.VerificationPurpose;

public record VerificationOtpEvent(
        Long userId,
        String email,
        String phone,
        String otp,
        VerificationPurpose purpose
) {
}