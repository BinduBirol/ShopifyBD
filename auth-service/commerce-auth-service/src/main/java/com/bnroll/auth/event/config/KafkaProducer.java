package com.bnroll.auth.event.config;

import com.bnroll.auth.dto.otp.ResendVerificationOtpRequest;
import com.bnroll.auth.event.dto.*;
import com.bnroll.commercedomain.entity.user.User;
import com.bnroll.enums.VerificationPurpose;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String PASSWORD_RESET_REQUEST_TOPIC = "password-reset-requested";
    private static final String PASSWORD_RESET_SUCCESS_TOPIC = "password-success-requested";
    private static final String USER_REGISTERED_TOPIC = "user-registered";
    private static final String LOGIN_FAILED_TOPIC = "login-failed";
    private static final String LOGIN_SUCCESS_TOPIC = "login-success";
    private static final String ACCOUNT_VERIFICATION_OTP = "account-verification-otp";


    public void sendUserRegisteredEvent(UserRegisteredEvent event) {

        kafkaTemplate.send(
                USER_REGISTERED_TOPIC,
                event.userId().toString(),
                event
        );
    }


    public void sendLoginFailedEvent(LoginFailedEvent event) {

        kafkaTemplate.send(
                LOGIN_FAILED_TOPIC,
                event.identifier(),
                event
        );
    }


    public void sendLoginSuccessEvent(LoginSuccessEvent event) {

        kafkaTemplate.send(
                LOGIN_SUCCESS_TOPIC,
                event.userId().toString(),
                event
        );
    }

    public void sendPasswordResetRequestedEvent(
            PasswordResetRequestedEvent event) {

        kafkaTemplate.send(
                PASSWORD_RESET_REQUEST_TOPIC,
                event.email(),
                event
        );
    }

    public void sendPasswordResetSuccessEvent(
            PasswordResetSuccessEvent event) {

        kafkaTemplate.send(
                PASSWORD_RESET_SUCCESS_TOPIC,
                event.email(),
                event
        );
    }


    public void sendAccountVerificationOtpEvent(
            User user,
            String otp,
            VerificationPurpose purpose
    ) {

        kafkaTemplate.send(
                ACCOUNT_VERIFICATION_OTP,
                String.valueOf(user.getId()),
                new VerificationOtpEvent(
                        user.getId(),
                        user.getEmail(),
                        user.getPhone(),
                        otp,
                        purpose
                )
        );
    }
}