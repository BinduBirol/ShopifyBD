package com.bnroll.auth.dto.otp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Account verification request")
public class VerifyAccountRequest {

    @NotNull(message = "{validation.userId.required}")
    @Schema(
            description = "User ID received after successful registration",
            example = "123"
    )
    private Long userId;

    @NotBlank(message = "{validation.otp.required}")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "{validation.otp.invalid}"
    )
    @Schema(
            description = "6-digit OTP sent to email or mobile",
            example = "123456"
    )
    private String otp;

}