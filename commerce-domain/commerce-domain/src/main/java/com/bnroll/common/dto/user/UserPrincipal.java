package com.bnroll.common.dto.user;

public record UserPrincipal(
        Long id,
        String email,
        String phone,
        String role
) {
}