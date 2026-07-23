package com.bnroll.auth.service;

import com.bnroll.auth.entity.password.PasswordResetToken;
import com.bnroll.auth.entity.user.User;
import com.bnroll.auth.repository.PasswordResetTokenRepository;
import com.bnroll.auth.security.JwtUtil;
import com.bnroll.auth.util.OtpGenerator;

import com.bnroll.commercedomain.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.password-reset-token.expiration}")
    private long passwordResetTokenExpiration;

    private final MessageSource messageSource;

    @Transactional
    public String createPasswordResetToken(User user, Locale locale) throws AuthException {

        log.info(
                "Creating password reset token. userId={}",
                user.getId()
        );
        Optional<PasswordResetToken> existingToken =
                passwordResetTokenRepository.findByUserAndUsedFalse(user);

        if (existingToken.isPresent()) {

            log.warn(
                    "Password reset already requested. userId={}",
                    user.getId()
            );

            PasswordResetToken token = existingToken.get();

            if (token.getExpiresAt().isAfter(Instant.now())) {

                Duration remaining = Duration.between(
                        Instant.now(),
                        token.getExpiresAt()
                );

                throw new AuthException(
                        "auth.password.reset.already.requested",
                        HttpStatus.CONFLICT,

                        formatRemainingTime(remaining, locale),
                        token.getUser().getId()
                );
            }

            passwordResetTokenRepository.invalidateAllByUserId(
                    user.getId(),
                    Instant.now()
            );
        }


        String token = OtpGenerator.generateToken();

        PasswordResetToken entity = PasswordResetToken.builder().tokenHash(DigestUtils.sha256Hex(token)).user(user).createdAt(Instant.now()).expiresAt(Instant.now().plusMillis(passwordResetTokenExpiration)).used(false).build();

        passwordResetTokenRepository.save(entity);

        log.info(
                "Password reset token created successfully. userId={}",
                user.getId()
        );

        return token;
    }

    @Transactional(readOnly = true)
    public PasswordResetToken validateToken(String token) {
        log.info("Validating password reset token.");
        PasswordResetToken storedToken = passwordResetTokenRepository.findByTokenHash(DigestUtils.sha256Hex(token)).orElseThrow(() -> new AuthException("password.reset.token.invalid", HttpStatus.BAD_REQUEST));

        if (storedToken.isUsed()) {
            log.warn("Password reset token already used.");
            throw new AuthException("password.reset.token.used", HttpStatus.BAD_REQUEST);
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Password reset token expired.");
            throw new AuthException("password.reset.token.expired", HttpStatus.BAD_REQUEST);
        }

        if (!jwtUtil.isTokenValid(token, storedToken.getUser().getId())) {
            log.warn("Invalid password reset token.");
            throw new AuthException("password.reset.token.invalid", HttpStatus.BAD_REQUEST);
        }

        return storedToken;
    }

    @Transactional
    public void markAsUsed(PasswordResetToken token) {

        log.info(
                "Password reset token marked as used. userId={}",
                token.getUser().getId()
        );

        token.setUsed(true);
        token.setUsedAt(Instant.now());
    }

    private String formatRemainingTime(Duration duration, Locale locale) {

        try {
            long minutes = duration.toMinutes();
            long seconds = duration.minusMinutes(minutes).getSeconds();

            if (minutes > 0) {
                return messageSource.getMessage("time.remaining.minutes.seconds", new Object[]{minutes, seconds}, locale);
            }

            return messageSource.getMessage("time.remaining.seconds", new Object[]{seconds}, locale);
        } catch (Exception ex) {
            log.error("Failed to format remaining time.", ex);
            ex.printStackTrace();
            throw new AuthException("internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}