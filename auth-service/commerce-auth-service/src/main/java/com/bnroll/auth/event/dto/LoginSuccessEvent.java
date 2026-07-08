package com.bnroll.auth.event.dto;

import com.bnroll.commercedomain.entity.user.LoginType;

import java.time.LocalDateTime;

public record LoginSuccessEvent(
        Long userId,
        String email,
        String phone,
        String firstName,
        LoginType loginType,
        String ipAddress,
        String userAgent,
        LocalDateTime loginTime
) {}