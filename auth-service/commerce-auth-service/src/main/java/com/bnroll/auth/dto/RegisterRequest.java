package com.bnroll.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(
            description = "User email address",
            example = "bindu@gmail.com"
    )
    @NotBlank(message = "{email.required}")
    private String email;

    @Schema(
            description = "User password",
            example = "P@ssw0rd123"
    )
    @NotBlank(message = "{password.required}")
    private String password;

    @Schema(
            description = "User's first name",
            example = "Birol"
    )
    @NotBlank(message = "{firstName.required}")
    private String firstName;

    @Schema(
            description = "User's last name",
            example = "Bindu"
    )
    @NotBlank(message = "{lastName.required}")
    private String lastName;

    @Schema(
            description = "Mobile phone number",
            example = "01830444758"
    )
    @NotBlank(message = "{phone.required}")
    private String phone;

    @Schema(
            description = "Role assigned to the user",
            allowableValues = {"ADMIN", "SELLER", "CUSTOMER"},
            example = "CUSTOMER"
    )
    @NotBlank(message = "{role.required}")
    private String role;
}