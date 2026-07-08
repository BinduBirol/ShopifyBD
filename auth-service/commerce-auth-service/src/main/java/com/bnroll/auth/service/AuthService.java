package com.bnroll.auth.service;


import com.bnroll.auth.dto.LoginRequest;
import com.bnroll.auth.dto.LoginResponse;
import com.bnroll.auth.dto.RegisterRequest;
import com.bnroll.auth.exception.AuthException;
import com.bnroll.auth.repository.UserRepository;
import com.bnroll.auth.security.JwtUtil;
import com.bnroll.commercedomain.entity.user.LoginType;
import com.bnroll.commercedomain.entity.user.RoleName;
import com.bnroll.commercedomain.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest httpServletRequest;

    public LoginResponse login(LoginRequest request, Locale locale) {

        LoginType loginType;
        try {
            loginType = LoginType.valueOf(request.getLoginType().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AuthException("loginType.invalid", HttpStatus.BAD_REQUEST);
        }

        RoleName requestedRole;
        try {
            requestedRole = RoleName.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new AuthException("invalid.role", HttpStatus.BAD_REQUEST);
        }

        User user = null;
        String notVerifiedMessage = "";

        switch (loginType) {

            case EMAIL -> {
                user = userRepository.findByEmail(request.getIdentifier())
                        .orElseThrow(() -> new AuthException("user.not.found", HttpStatus.NOT_FOUND));

                notVerifiedMessage = "mail.not.verified";
            }

            case MOBILE -> {
                user = userRepository.findByPhone(request.getIdentifier())
                        .orElseThrow(() -> new AuthException("user.not.found", HttpStatus.NOT_FOUND));

                notVerifiedMessage = "phone.not.verified";
            }

            case GOOGLE -> {
                throw new UnsupportedOperationException("Google login is not implemented yet.");
            }
        }

        if (!user.getRoles().contains(requestedRole)) {
            logFailureAndThrow(
                    user,
                    request,
                    loginType,
                    requestedRole,
                    "role.not.assigned",
                    HttpStatus.FORBIDDEN
            );
        }

        if (!user.isVerified()) {
            logFailureAndThrow(
                    user,
                    request,
                    loginType,
                    requestedRole,
                    notVerifiedMessage,
                    HttpStatus.FORBIDDEN
            );
        }

        if (!user.isActive()) {
            logFailureAndThrow(
                    user,
                    request,
                    loginType,
                    requestedRole,
                    "user.inactive",
                    HttpStatus.FORBIDDEN
            );
        }

        if (loginType != LoginType.GOOGLE &&
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            logFailureAndThrow(
                    user,
                    request,
                    loginType,
                    requestedRole,
                    "invalid.password",
                    HttpStatus.UNAUTHORIZED
            );
        }

        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + 3_600_000L;

        String token = jwtUtil.generateToken(
                user.getEmail(),
                requestedRole.name()
        );

        loginAttemptService.log(
                user,
                request.getIdentifier(),
                loginType,
                requestedRole,
                true,
                null,
                httpServletRequest
        );

        return LoginResponse.of(
                token,
                requestedRole.name(),
                issuedAt,
                expiresAt
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

        throw new AuthException(reason, status);
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("email.already.exists", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AuthException("phone.already.exists", HttpStatus.CONFLICT);
        }

        RoleName role;
        try {
            role = RoleName.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AuthException("invalid.role", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(role));

        return userRepository.save(user);
    }


}