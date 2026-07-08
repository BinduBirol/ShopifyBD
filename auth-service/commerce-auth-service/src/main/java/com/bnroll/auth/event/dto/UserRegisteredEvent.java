package com.bnroll.auth.event.dto;

import com.bnroll.commercedomain.entity.user.LoginType;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String phone,
        String firstName

) {
}

