package com.bnroll.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Login response")
public class LoginResponse {

    @Schema(
            description = "JWT access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;

    @Schema(
            description = "Token type",
            example = "Bearer"
    )
    private String tokenType;

    @Schema(
            description = "Authenticated user role",
            example = "CUSTOMER"
    )
    private String role;

    @Schema(
            description = "Token issued time (Unix epoch milliseconds)",
            example = "1751934600000"
    )
    private long issuedAt;

    @Schema(
            description = "Token expiration time (Unix epoch milliseconds)",
            example = "1751938200000"
    )
    private long expiresAt;

    @Schema(
            description = "Token validity duration in seconds",
            example = "3600"
    )
    private long expiresIn;

    public static LoginResponse of(
            String token,
            String role,
            long issuedAt,
            long expiresAt) {

        return new LoginResponse(
                token,
                "Bearer",
                role,
                issuedAt,
                expiresAt,
                (expiresAt - issuedAt) / 1000
        );
    }
}