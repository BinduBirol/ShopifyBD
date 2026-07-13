package com.bnroll.auth.controller;

import com.bnroll.auth.dto.*;
import com.bnroll.auth.dto.forgetpassword.ForgotPasswordRequest;
import com.bnroll.auth.dto.forgetpassword.ResetPasswordRequest;
import com.bnroll.auth.exception.AuthException;
import com.bnroll.auth.security.ratelimit.RateLimit;
import com.bnroll.auth.service.AuthService;
import com.bnroll.commercedomain.entity.user.User;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.common.i18n.MessageService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    private final MessageService messageService;

    @PostMapping("/v1/login")
    @RateLimit(limit = 5, durationSeconds = 60)
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, Locale locale,
                                            HttpServletRequest httpRequest) {

        LoginResponse response = authService.login(request);

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .data(response)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpRequest.getRequestURI())
                .build();
    }

    @PostMapping("/v1/refresh")
    public LoginResponse refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/v1/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                new MeResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRoles()
                )
        );
    }


    @PostMapping("/v1/register")
    @RateLimit(limit = 5, durationSeconds = 60)
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request, Locale locale,
                                        HttpServletRequest httpRequest) {

        User user = authService.register(request);

        return ApiResponse.<String>builder()
                .success(true)
                .data(messageService.get("user.register.success", locale))
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpRequest.getRequestURI())
                .correlationId(String.valueOf(user.getId()))
                .build();
    }

    @PostMapping("/v1/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
    }

    @PostMapping("/v1/logout/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutAll(@RequestBody @Valid LogoutRequest request) {
        authService.logoutAll(request);
    }

    @PostMapping("/v1/forgot-password")
    public ApiResponse<String> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequest request,
            Locale locale, HttpServletRequest httpRequest) {

        return authService.forgotPassword(request, locale, httpRequest);
    }

    @PostMapping("/v1/reset-password")
    public ApiResponse<String> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request, HttpServletRequest httpRequest, Locale locale) {


        User user = authService.resetPassword(request);

        String message = messageService.get("password.reset.success", locale);

        return ApiResponse.<String>builder()
                .success(true)
                .data(message)
                .timestamp(LocalDateTime.now())
                .version("v1")
                .correlationId(String.valueOf(user.getId()))
                .path(httpRequest.getRequestURI())
                .build();
    }
}