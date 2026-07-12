package com.bnroll.auth.repository;


import com.bnroll.commercedomain.entity.user.User;
import com.bnroll.commercedomain.entity.verification.VerificationOtp;
import com.bnroll.enums.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface VerificationOtpRepository
        extends JpaRepository<VerificationOtp, UUID> {

    Optional<VerificationOtp> findByUserAndPurposeAndUsedFalse(
            User user,
            VerificationPurpose purpose
    );

    boolean existsByUserAndPurposeAndUsedFalse(
            User user,
            VerificationPurpose purpose
    );

    void deleteByUserAndPurpose(
            User user,
            VerificationPurpose purpose
    );

    void deleteByExpiresAtBefore(
            Instant now
    );

    Optional<VerificationOtp> findFirstByUserAndPurposeAndUsedFalseOrderByCreatedAtDesc(User user, VerificationPurpose verificationPurpose);
}