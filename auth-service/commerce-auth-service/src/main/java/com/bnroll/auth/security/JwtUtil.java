package com.bnroll.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Value("${password.token.expiration}")
    private long passwordResetTokenExpiration;


    private SecretKey getKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(
            Long userId,
            String email,
            String phone,
            String role
    ) {

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("phone", phone)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessTokenExpiration
                        )
                )
                .signWith(getKey())
                .compact();
    }


    public String generateRefreshToken(Long userId) {

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshTokenExpiration
                        )
                )
                .signWith(getKey())
                .compact();
    }


    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    /**
     * Access token subject = userId now
     */
    public Long extractUserId(String token) {

        return Long.parseLong(
                extractClaims(token).getSubject()
        );
    }


    public String extractEmail(String token) {

        return extractClaims(token)
                .get("email", String.class);
    }


    public String extractPhone(String token) {

        return extractClaims(token)
                .get("phone", String.class);
    }


    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }


    public Date extractExpiration(String token) {

        return extractClaims(token)
                .getExpiration();
    }


    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }


    public boolean isTokenValid(
            String token,
            Long userId
    ) {

        return userId.equals(extractUserId(token))
                && !isTokenExpired(token);
    }


    public String generatePasswordResetToken(String email) {

        return Jwts.builder()
                .subject(email)
                .claim("type", "PASSWORD_RESET")
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + passwordResetTokenExpiration
                        )
                )
                .signWith(getKey())
                .compact();
    }

    public String generateServiceToken(String service) {

        String token = Jwts.builder()
                .subject(service)
                .claim("type", "SERVICE")
                .claim("service", service)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getKey())
                .compact();


        return token;
    }
}