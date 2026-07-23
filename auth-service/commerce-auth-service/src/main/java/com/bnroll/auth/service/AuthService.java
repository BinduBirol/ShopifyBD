package com.bnroll.auth.service;


import com.bnroll.auth.client.PropertyClient;
import com.bnroll.auth.dto.*;
import com.bnroll.auth.dto.forgetpassword.ForgotPasswordRequest;
import com.bnroll.auth.dto.forgetpassword.ResetPasswordRequest;
import com.bnroll.auth.entity.auth.RefreshToken;
import com.bnroll.auth.entity.password.PasswordResetToken;
import com.bnroll.auth.entity.user.User;
import com.bnroll.auth.event.config.KafkaProducer;
import com.bnroll.auth.repository.RefreshTokenRepository;
import com.bnroll.auth.repository.UserRepository;
import com.bnroll.auth.util.AuthUtil;

import com.bnroll.commercedomain.enums.VerificationPurpose;
import com.bnroll.commercedomain.enums.user.LoginType;
import com.bnroll.commercedomain.enums.user.RoleName;
import com.bnroll.commercedomain.event.*;
import com.bnroll.commercedomain.exception.AuthException;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.dto.property.FacilityDto;
import com.bnroll.logging.MdcUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Slf4j
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

    private final AccountVerificationService accountVerificationService;

    private final PropertyClient propertyClient;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        log.info("Login request received. identifier={}, loginType={}",
                request.getIdentifier(),
                request.getLoginType());

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

            log.warn(
                    "Login failed. userId={}, reason={}",
                    user.getId(),
                    ex.getMessage()
            );

            logFailureAndThrow(
                    user,
                    request,
                    loginType,
                    role,
                    ex.getMessage(),
                    ex.getStatus()
            );
        }

        MdcUtil.setUserId(user.getId());

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

        log.info(
                "User authenticated successfully. userId={}, role={}",
                user.getId(),
                role
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

        log.info(
                "Registration request received. email={}, role={}",
                request.getEmail(),
                request.getRole()
        );

        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn("Registration failed. Email already exists. email={}",
                    request.getEmail());

            throw new AuthException("email.already.exists", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByPhone(request.getPhone())) {

            log.warn("Registration failed. Phone already exists. phone={}",
                    request.getPhone());

            throw new AuthException("phone.already.exists", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(request.getRole()));

        User savedUser = userRepository.save(user);

        MdcUtil.setUserId(savedUser.getId());

        log.info("User created successfully. userId={}", savedUser.getId());

        try {

            FacilityDto facilityDto = FacilityDto.builder()
                    .name(request.getFacilityTitle())
                    .type(request.getFacilityType())
                    .addressLine1(request.getAddressLine1())
                    .description(request.getDescription())
                    .userRole(request.getRole())
                    .creatorId(savedUser.getId())
                    .build();

            propertyClient.createFacility(facilityDto);

            log.info("Facility created successfully. userId={}", savedUser.getId());

        } catch (Exception ex) {

            log.error(
                    "Facility creation failed. Rolling back registration. userId={}",
                    savedUser.getId(),
                    ex
            );

            userRepository.delete(savedUser);

            throw new AuthException(
                    "facility.creation.failed",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        accountVerificationService.generateOtpAndPublish(
                user,
                VerificationPurpose.ACCOUNT_VERIFICATION
        );

        kafkaProducer.sendUserRegisteredEvent(
                new UserRegisteredEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getPhone(),
                        savedUser.getFirstName()
                )
        );

        log.info(
                "User registration completed successfully. userId={}, email={}",
                savedUser.getId(),
                savedUser.getEmail()
        );

        return savedUser;
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {

        log.info("Refresh token request received.");

        RefreshToken storedToken =
                authUtil.validateRefreshToken(request.getRefreshToken());

        User user = storedToken.getUser();

        MdcUtil.setUserId(user.getId());

        RoleName role = authUtil.parseRole(request.getRole());
        authUtil.validateRole(user, role);

        String accessToken = jwtService.createAccessToken(user, role);
        String refreshToken = jwtService.rotateRefreshToken(storedToken);

        long issuedAt = System.currentTimeMillis();

        log.info(
                "Access token refreshed successfully. userId={}, role={}",
                user.getId(),
                role
        );

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

        log.warn(
                "Authentication failed. identifier={}, reason={}",
                request.getIdentifier(),
                reason
        );

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

        log.info("Logout request received.");

        RefreshToken storedToken =
                authUtil.validateRefreshToken(request.getRefreshToken());

        User user = storedToken.getUser();

        MdcUtil.setUserId(user.getId());

        storedToken.setRevoked(true);
        storedToken.setRevokedAt(Instant.now());

        refreshTokenRepository.save(storedToken);

        log.info("User logged out successfully. userId={}", user.getId());
    }

    @Transactional
    public void logoutAll(LogoutRequest request) {

        log.info("Logout all sessions request received.");

        RefreshToken storedToken =
                authUtil.validateRefreshToken(request.getRefreshToken());

        User user = storedToken.getUser();

        MdcUtil.setUserId(user.getId());

        jwtService.revokeAllSessions(user);

        log.info("All sessions revoked successfully. userId={}", user.getId());
    }

    @Transactional
    public ApiResponse<String> forgotPassword(
            ForgotPasswordRequest request,
            Locale locale, HttpServletRequest httpRequest) {

        log.info(
                "Password reset requested. identifier={}, loginType={}",
                request.getIdentifier(),
                request.getLoginType()
        );

        LoginType loginType =
                authUtil.parseLoginType(request.getLoginType());

        User user = authUtil.findUser(
                request.getIdentifier(),
                loginType
        );

        // Security: don't reveal whether user exists
        if (user != null) {
            String token = passwordResetService.createPasswordResetToken(user, locale);

            System.err.println("PASSWORD RESET TOKEN: " + token);

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

            log.info(
                    "Password reset event published. userId={}",
                    user.getId()
            );

        } else {

            log.warn(
                    "Password reset failed. User not found. identifier={}",
                    request.getIdentifier()
            );
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
    public User resetPassword(ResetPasswordRequest request) {

        log.info("Password reset started.");

        PasswordResetToken resetToken =
                authUtil.validatePasswordResetToken(request.getToken());

        User user = resetToken.getUser();

        MdcUtil.setUserId(user.getId());

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

        log.info(
                "Password reset completed successfully. userId={}",
                user.getId()
        );

        return user;
    }
}