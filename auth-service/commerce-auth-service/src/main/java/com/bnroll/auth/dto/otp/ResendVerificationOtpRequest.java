package com.bnroll.auth.dto.otp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Resend account verification OTP request")
public class ResendVerificationOtpRequest {

    @NotNull(message = "{validation.userId.required}")
    @Schema(
            description = "User ID received after successful registration",
            example = "123"
    )
    private Long userId;

}