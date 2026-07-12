package com.bnroll.auth.service;


import com.bnroll.auth.dto.*;
import com.bnroll.auth.dto.forgetpassword.ForgotPasswordRequest;
import com.bnroll.auth.dto.forgetpassword.ResetPasswordRequest;
import com.bnroll.auth.event.config.KafkaProducer;
import com.bnroll.auth.event.dto.*;
import com.bnroll.auth.exception.AuthException;
import com.bnroll.auth.repository.RefreshTokenRepository;
import com.bnroll.auth.repository.UserRepository;
import com.bnroll.auth.security.JwtUtil;
import com.bnroll.auth.util.AuthUtil;
import com.bnroll.commercedomain.entity.auth.RefreshToken;
import com.bnroll.commercedomain.entity.password.PasswordResetToken;
import com.bnroll.commercedomain.entity.user.LoginType;
import com.bnroll.commercedomain.entity.user.RoleName;
import com.bnroll.commercedomain.entity.user.User;
import com.bnroll.common.dto.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private final JwtService jwtService;

    private final PasswordResetService passwordResetService;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest httpServletRequest;
    private final KafkaProducer kafkaProducer;
    private final RefreshTokenRepository refreshTokenRepository;

    private final MessageSource messageSource;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        LoginType loginType = authUtil.parseLoginType(request.getLoginType());
        User user = authUtil.findUser(request, loginType);
        RoleName role = authUtil.parseRole(request.getRole());

        try {
            authUtil.validateRole(user, role);
            authUtil.validateUserStatus(user, loginType);

            if (loginType != LoginType.GOOGLE) {
                authUtil.validatePassword(user, request.getPassword());
            }

        } catch (AuthException ex) {

            logFailureAndThrow(
                    user,
                    request,
                    loginType,
                    role,
                    ex.getMessage(),
                    ex.getStatus()
            );
        }

        String accessToken = jwtService.createAccessToken(user, role);
        String refreshToken = jwtService.createRefreshToken(user);

        long issuedAt = System.currentTimeMillis();

        loginAttemptService.log(
                user,
                request.getIdentifier(),
                loginType,
                role,
                true,
                null,
                httpServletRequest
        );

        kafkaProducer.sendLoginSuccessEvent(
                new LoginSuccessEvent(
                        user.getId(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getFirstName(),
                        loginType,
                        httpServletRequest.getRemoteAddr(),
                        httpServletRequest.getHeader("User-Agent"),
                        LocalDateTime.now()
                )
        );

        return LoginResponse.of(
                accessToken,
                refreshToken,
                role.name(),
                issuedAt,
                issuedAt + accessTokenExpiration
        );
    }

    @Transactional
    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("email.already.exists", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AuthException("phone.already.exists", HttpStatus.CONFLICT);
        }

        RoleName role = authUtil.parseRole(request.getRole());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);

        kafkaProducer.sendUserRegisteredEvent(
                new UserRegisteredEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getPhone(),
                        savedUser.getFirstName()
                )
        );

        return savedUser;
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {


        RefreshToken storedToken =
                authUtil.validateRefreshToken(request.getRefreshToken());


        User user = storedToken.getUser();

        RoleName role = authUtil.parseRole(request.getRole());
        authUtil.validateRole(user, role);

        String accessToken = jwtService.createAccessToken(user, role);
        String refreshToken = jwtService.rotateRefreshToken(storedToken);

        long issuedAt = System.currentTimeMillis();

        return LoginResponse.of(
                accessToken,
                refreshToken,
                role.name(),
                issuedAt,
                issuedAt + accessTokenExpiration
        );
    }

    private void logFailureAndThrow(
            User user,
            LoginRequest request,
            LoginType loginType,
            RoleName role,
            String reason,
            HttpStatus status) {

        loginAttemptService.log(
                user,
                request.getIdentifier(),
                loginType,
                role,
                false,
                reason,
                httpServletRequest
        );

        kafkaProducer.sendLoginFailedEvent(
                new LoginFailedEvent(
                        request.getIdentifier(),
                        loginType,
                        httpServletRequest.getRemoteAddr(),
                        httpServletRequest.getHeader("User-Agent"),
                        reason,
                        LocalDateTime.now()
                )
        );

        throw new AuthException(reason, status, String.valueOf(user.getId()));
    }

    @Transactional
    public void logout(LogoutRequest request) {

        RefreshToken storedToken =
                authUtil.validateRefreshToken(request.getRefreshToken());

        storedToken.setRevoked(true);
        storedToken.setRevokedAt(Instant.now());

        refreshTokenRepository.save(storedToken);
    }

    @Transactional
    public void logoutAll(LogoutRequest request) {

        RefreshToken storedToken =
                authUtil.validateRefreshToken(request.getRefreshToken());

        jwtService.revokeAllSessions(storedToken.getUser());

    }

    @Transactional
    public ApiResponse<String> forgotPassword(
            ForgotPasswordRequest request,
            Locale locale, HttpServletRequest httpRequest) {

        LoginType loginType =
                authUtil.parseLoginType(request.getLoginType());

        User user = authUtil.findUser(
                request.getIdentifier(),
                loginType
        );

        // Security: don't reveal whether user exists
        if (user != null) {
            String token = passwordResetService.createPasswordResetToken(user, locale);

            kafkaProducer.sendPasswordResetRequestedEvent(
                    new PasswordResetRequestedEvent(
                            user.getId(),
                            user.getEmail(),
                            user.getPhone(),
                            loginType,
                            token,
                            LocalDateTime.now()
                    )
            );

        } else {
            throw new AuthException("user.notfound", HttpStatus.NOT_FOUND);
        }

        String messageKey = loginType == LoginType.EMAIL
                ? "auth.password.reset.requested.email"
                : "auth.password.reset.requested.phone";

        String response = messageSource.getMessage(
                messageKey,
                null,
                locale
        );

        return ApiResponse.<String>builder()
                .success(true)
                .data(response)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpRequest.getRequestURI())
                .build();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken =
                authUtil.validatePasswordResetToken(request.getToken());

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        passwordResetService.markAsUsed(resetToken);
        jwtService.revokeAllSessions(user);

        kafkaProducer.sendPasswordResetSuccessEvent(
                new PasswordResetSuccessEvent(
                        user.getId(),
                        user.getEmail(),
                        user.getPhone(),
                        LocalDateTime.now()
                )
        );
    }
}