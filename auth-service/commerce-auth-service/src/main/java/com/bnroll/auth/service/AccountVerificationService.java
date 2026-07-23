package com.bnroll.auth.service;

import com.bnroll.auth.dto.otp.VerifyAccountRequest;
import com.bnroll.auth.entity.user.User;
import com.bnroll.auth.entity.verification.VerificationOtp;
import com.bnroll.auth.event.config.KafkaProducer;
import com.bnroll.auth.repository.UserRepository;
import com.bnroll.auth.repository.VerificationOtpRepository;
import com.bnroll.auth.util.OtpGenerator;

import com.bnroll.commercedomain.enums.VerificationPurpose;
import com.bnroll.commercedomain.exception.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountVerificationService {
    private final KafkaProducer kafkaProducer;
    private final VerificationOtpRepository verificationOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void generateOtpAndPublish(
            User user,
            VerificationPurpose purpose
    ) {
        log.info(
                "Generating verification OTP. userId={}, purpose={}",
                user.getId(),
                purpose
        );
        if (user.isVerified()) {
            log.warn(
                    "OTP generation rejected. User already verified. userId={}",
                    user.getId()
            );
            throw new AuthException(
                    "user.already.verified",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<VerificationOtp> existingOtp =
                verificationOtpRepository.findByUserAndPurposeAndUsedFalse(
                        user,
                        purpose
                );

        if (existingOtp.isPresent()) {

            log.warn(
                    "OTP already active. userId={}, purpose={}",
                    user.getId(),
                    purpose
            );

            VerificationOtp otp = existingOtp.get();

            if (otp.getExpiresAt().isAfter(LocalDateTime.now())) {

                //System.out.println("Expired OTP : "+otp.getOtpHash());

                long seconds =
                        Duration.between(
                                Instant.now(),
                                otp.getExpiresAt()
                        ).toSeconds();

                throw new AuthException(
                        "otp.resend.wait",
                        HttpStatus.TOO_MANY_REQUESTS,
                        seconds
                );
            }

            verificationOtpRepository.delete(otp);
        }

        verificationOtpRepository.deleteByUserAndPurpose(
                user,
                purpose
        );

        String otp = OtpGenerator.generate(6);

        System.out.println("New OTP : " + otp);

        VerificationOtp entity = VerificationOtp.builder()
                .user(user)
                .purpose(purpose)
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(LocalDateTime.now().plusSeconds(60))
                .attemptCount(0)
                .used(false)
                .build();

        verificationOtpRepository.save(entity);

        kafkaProducer.sendAccountVerificationOtpEvent(
                user,
                otp,
                purpose
        );

        log.info(
                "Verification OTP event published. userId={}, purpose={}",
                user.getId(),
                purpose
        );
    }

    @Transactional
    public void verifyAccount(
            @Valid VerifyAccountRequest request
    ) {

        log.info(
                "Account verification started. userId={}",
                request.getUserId()
        );


        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new AuthException(
                                "user.not.found",
                                HttpStatus.BAD_REQUEST
                        ));


        if (user.isVerified()) {
            log.warn(
                    "Account verification skipped. User already verified. userId={}",
                    user.getId()
            );
            throw new AuthException(
                    "user.already.verified",
                    HttpStatus.BAD_REQUEST
            );
        }


        VerificationOtp verificationOtp =
                verificationOtpRepository
                        .findFirstByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(
                                user,
                                VerificationPurpose.ACCOUNT_VERIFICATION
                        )
                        .orElseThrow(() ->
                                new AuthException(
                                        "otp.not.found",
                                        HttpStatus.BAD_REQUEST
                                ));


        if (verificationOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn(
                    "Account verification failed. OTP expired. userId={}",
                    user.getId()
            );
            throw new AuthException(
                    "otp.expired",
                    HttpStatus.BAD_REQUEST
            );
        }


        if (!passwordEncoder.matches(
                request.getOtp(),
                verificationOtp.getOtpHash()
        )) {

            log.warn(
                    "Account verification failed. Invalid OTP. userId={}",
                    user.getId()
            );

            throw new AuthException(
                    "otp.mismatch",
                    HttpStatus.BAD_REQUEST
            );
        }


        verificationOtp.setUsed(true);
        verificationOtp.setUsedAt(LocalDateTime.now());

        verificationOtpRepository.save(verificationOtp);


        user.setVerified(true);
        user.setActive(true);

        userRepository.save(user);

        log.info(
                "Account verified successfully. userId={}",
                user.getId()
        );

        // kafkaProducer.publishAccountVerifiedEvent(user);
    }
}
