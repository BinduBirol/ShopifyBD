package com.bnroll.auth.service;

import com.bnroll.auth.entity.auth.RefreshToken;
import com.bnroll.auth.entity.user.User;
import com.bnroll.auth.repository.RefreshTokenRepository;
import com.bnroll.auth.security.JwtUtil;
import com.bnroll.commercedomain.enums.user.RoleName;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String createAccessToken(User user, RoleName role) {

        return jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                role.name()
        );
    }

    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken entity = RefreshToken.builder()
                .tokenHash(DigestUtils.sha256Hex(refreshToken))
                .sessionId(UUID.randomUUID().toString())
                .user(user)
                .createdAt(now)
                .expiresAt(now.plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();

        refreshTokenRepository.save(entity);

        return refreshToken;
    }

    @Transactional
    public String rotateRefreshToken(RefreshToken storedToken) {

        String newRefreshToken =
                jwtUtil.generateRefreshToken(
                        storedToken.getUser().getId()
                );

        storedToken.setTokenHash(
                DigestUtils.sha256Hex(newRefreshToken)
        );

        storedToken.setLastUsedAt(Instant.now());

        storedToken.setExpiresAt(
                Instant.now().plusMillis(refreshTokenExpiration)
        );

        storedToken.setRevoked(false);

        refreshTokenRepository.save(storedToken);

        return newRefreshToken;
    }

    @Transactional
    public void revokeAllSessions(User user) {

        log.info("Revoking all sessions. userId={}", user.getId());

        refreshTokenRepository.revokeAllByUserId(
                user.getId(),
                Instant.now()
        );

        log.info("All sessions revoked. userId={}", user.getId());
    }

    public String generateServiceToken(String serviceName) {
        return jwtUtil.generateServiceToken(serviceName);
    }
}