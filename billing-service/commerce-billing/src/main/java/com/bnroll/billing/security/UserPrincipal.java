package com.bnroll.billing.security;

public record UserPrincipal(
        Long id,
        String email,
        String phone,
        String role
) {
}