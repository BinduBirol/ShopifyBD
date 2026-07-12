package com.bnroll.auth.util;

import com.bnroll.auth.dto.LoginRequest;
import com.bnroll.auth.exception.AuthException;
import com.bnroll.auth.repository.PasswordResetTokenRepository;
import com.bnroll.auth.repository.RefreshTokenRepository;
import com.bnroll.auth.repository.UserRepository;
import com.bnroll.auth.security.JwtUtil;
import com.bnroll.commercedomain.entity.auth.RefreshToken;
import com.bnroll.commercedomain.entity.password.PasswordResetToken;
import com.bnroll.commercedomain.entity.user.LoginType;
import com.bnroll.commercedomain.entity.user.RoleName;
import com.bnroll.commercedomain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public LoginType parseLoginType(String loginType) {

        try {
            return LoginType.valueOf(loginType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AuthException("loginType.invalid", HttpStatus.BAD_REQUEST);
        }
    }

    public RoleName parseRole(String role) {

        try {
            return RoleName.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AuthException("invalid.role", HttpStatus.BAD_REQUEST);
        }
    }

    public User findUser(LoginRequest request, LoginType loginType) {

        return switch (loginType) {

            case EMAIL -> userRepository.findByEmail(request.getIdentifier())
                    .orElseThrow(() ->
                            new AuthException(
                                    "user.not.found",
                                    HttpStatus.NOT_FOUND));

            case MOBILE -> userRepository.findByPhone(request.getIdentifier())
                    .orElseThrow(() ->
                            new AuthException(
                                    "user.not.found",
                                    HttpStatus.NOT_FOUND));

            case GOOGLE -> throw new UnsupportedOperationException(
                    "Google login is not implemented yet.");
        };
    }

    public User findUser(String userid, LoginType loginType) {

        return switch (loginType) {

            case EMAIL -> userRepository.findByEmail(userid)
                    .orElseThrow(() ->
                            new AuthException(
                                    "user.not.found",
                                    HttpStatus.NOT_FOUND));

            case MOBILE -> userRepository.findByPhone(userid)
                    .orElseThrow(() ->
                            new AuthException(
                                    "user.not.found",
                                    HttpStatus.NOT_FOUND));

            case GOOGLE -> throw new UnsupportedOperationException(
                    "Google login is not implemented yet.");
        };
    }

    public void validateRole(User user, RoleName role) {

        if (!user.getRoles().contains(role)) {
            throw new AuthException(
                    "role.not.assigned",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public void validateUserStatus(User user, LoginType loginType) {


        if (!user.isVerified()) {

            String message = switch (loginType) {
                case EMAIL -> "mail.not.verified";
                case MOBILE -> "phone.not.verified";
                default -> "user.not.verified";
            };


            throw new AuthException(
                    message,
                    HttpStatus.FORBIDDEN,
                    String.valueOf(user.getId())
            );
        }


        if (!user.isActive()) {
            throw new AuthException(
                    "user.inactive",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public void validatePassword(User user, String password) {

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException(
                    "invalid.password",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    public RefreshToken validateRefreshToken(String refreshToken) {

        String tokenHash = DigestUtils.sha256Hex(refreshToken);

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new AuthException(
                                "validation.failed",
                                HttpStatus.UNAUTHORIZED
                        ));

        if (storedToken.isRevoked()) {
            throw new AuthException(
                    "validation.failed",
                    HttpStatus.UNAUTHORIZED
            );
        }

        User user = storedToken.getUser();

        if (!jwtUtil.isTokenValid(refreshToken, user.getEmail())) {
            throw new AuthException(
                    "validation.failed",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return storedToken;
    }


    public PasswordResetToken validatePasswordResetToken(
            @NotBlank(message = "{validation.reset.token.required}") String token) {

        String tokenHash = DigestUtils.sha256Hex(token);

        PasswordResetToken storedToken = passwordResetTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new AuthException(
                        "password.reset.token.invalid",
                        HttpStatus.BAD_REQUEST
                ));

        if (storedToken.isUsed()) {
            throw new AuthException(
                    "password.reset.token.used",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException(
                    "password.reset.token.expired",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!jwtUtil.isTokenValid(
                token,
                storedToken.getUser().getEmail()
        )) {
            throw new AuthException(
                    "password.reset.token.invalid",
                    HttpStatus.BAD_REQUEST
            );
        }

        return storedToken;
    }
}