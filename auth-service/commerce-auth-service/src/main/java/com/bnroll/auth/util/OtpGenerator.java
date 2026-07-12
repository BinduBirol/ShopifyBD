package com.bnroll.auth.util;


import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private OtpGenerator() {
    }


    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {

        if (length < 4 || length > 10) {
            throw new IllegalArgumentException(
                    "OTP length must be between 4 and 10 digits."
            );
        }

        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - min;

        int otp = min + RANDOM.nextInt(max);

        return String.valueOf(otp);
    }
}