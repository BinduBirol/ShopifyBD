package com.bnroll.auth.controller;

import com.bnroll.auth.dto.otp.ResendVerificationOtpRequest;
import com.bnroll.auth.dto.otp.VerifyAccountRequest;
import com.bnroll.auth.exception.AuthException;
import com.bnroll.auth.repository.UserRepository;
import com.bnroll.auth.service.AccountVerificationService;
import com.bnroll.auth.util.AuthUtil;
import com.bnroll.commercedomain.entity.user.User;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.common.i18n.MessageService;
import com.bnroll.enums.VerificationPurpose;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Locale;

@RestController
@RequestMapping("/v1/user/verify")
@RequiredArgsConstructor
@Tag(name = "User Account Verification", description = "User Account Verification APIs")
public class AccountVerificationController {

    private final AccountVerificationService accountVerificationService;

    private final MessageService messageService;

    private final UserRepository userRepository;

    @PostMapping("/resend-otp")
    @Operation(summary = "Resend verification OTP")
    public ApiResponse<String> resendOtp(
            @Valid @RequestBody ResendVerificationOtpRequest request,
            HttpServletRequest httpRequest,
            Locale locale) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new AuthException("user.not.found", HttpStatus.BAD_REQUEST));



        accountVerificationService.generateOtpAndPublish(user, VerificationPurpose.ACCOUNT_VERIFICATION);

        return ApiResponse.<String>builder()
                .success(true)
                .data(messageService.get("otp.resent", locale))
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpRequest.getRequestURI())
                .correlationId(String.valueOf(user.getId()))
                .build();
    }


    @PostMapping("/account/otp")
    @Operation(summary = "Verify user account")
    public ApiResponse<String> verifyAccount(
            @Valid @RequestBody VerifyAccountRequest request,
            HttpServletRequest httpRequest,
            Locale locale
    ) {

        accountVerificationService.verifyAccount(request);

        return ApiResponse.<String>builder()
                .success(true)
                .data(messageService.get(
                        "otp.verificationSuccessful",
                        locale
                ))
                .timestamp(LocalDateTime.now())
                .version("v1")
                .path(httpRequest.getRequestURI())
                .correlationId(String.valueOf(request.getUserId()))
                .build();
    }

}