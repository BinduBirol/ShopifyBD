package com.bnroll.auth.dto;

import com.bnroll.commercedomain.entity.user.LoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "{identifier.required}")
    private String identifier;

    @NotBlank(message = "{password.required}")
    private String password;

    @NotBlank(message = "{role.required}")
    private String role;

    @NotNull(message = "{login.type.required}")
    private String loginType;
}