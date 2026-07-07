package com.bnroll.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request")
public class LoginRequest {

    @Schema(
            description = "User identifier. Email address or mobile number depending on the login type.",
            example = "bindu@gmail.com"
    )
    @NotBlank(message = "{identifier.required}")
    private String identifier;

    @Schema(
            description = "User password. Not required for Google login.",
            example = "P@ssw0rd123"
    )
    @NotBlank(message = "{password.required}")
    private String password;

    @Schema(
            description = "Role to log in as.",
            allowableValues = {"ADMIN", "SELLER", "CUSTOMER"},
            example = "CUSTOMER"
    )
    @NotBlank(message = "{role.required}")
    private String role;

    @Schema(
            description = "Authentication method.",
            allowableValues = {"EMAIL", "MOBILE", "GOOGLE"},
            example = "EMAIL"
    )
    @NotBlank(message = "{login.type.required}")
    private String loginType;
}