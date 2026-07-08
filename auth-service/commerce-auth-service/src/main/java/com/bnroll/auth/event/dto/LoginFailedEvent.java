package com.bnroll.auth.event.dto;

import com.bnroll.commercedomain.entity.user.LoginType;

import java.time.LocalDateTime;

public record LoginFailedEvent(
        String identifier,
        LoginType loginType,
        String ipAddress,
        String userAgent,
        String reason,
        LocalDateTime attemptTime
) {}