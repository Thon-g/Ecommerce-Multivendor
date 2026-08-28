package com.abs.app.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";
    private static final String RESET_PASSWORD_TOKEN_TYPE = "reset_password";
    private static final String ROLE_CLAIM = "role";
    private static final String SELLER_ID_CLAIM = "sellerId";

    private final Key accessTokenKey;
    private final Key resetPasswordTokenKey;
    private final long expiration;
    private final long refreshTokenExpiration;
    private final long resetPasswordTokenExpiration;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.reset-secret}") String resetPasswordSecret,
            @Value("${security.jwt.expiration}") long expiration,
            @Value("${security.jwt.refresh-expiration}") long refreshTokenExpiration,
            @Value("${security.jwt.reset-expiration}") long resetPasswordTokenExpiration) {
        this.accessTokenKey = buildHmacKey(jwtSecret, "security.jwt.secret");
        this.resetPasswordTokenKey = buildHmacKey(resetPasswordSecret, "security.jwt.reset-secret");
        this.expiration = expiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.resetPasswordTokenExpiration = resetPasswordTokenExpiration;
    }

    private Key buildHmacKey(String rawSecret, String propertyName) {
        String secret = rawSecret == null ? "" : rawSecret.trim();
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(propertyName + " must be at least 32 bytes for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String userId, String role) {
        return buildAccessToken(userId, role, null);
    }

    public String generateSellerAccessToken(String userId, String sellerId) {
        return buildAccessToken(userId, "SELLER", sellerId);
    }

    @Deprecated
    public String generateToken(String userId, String role) {
        return generateAccessToken(userId, role);
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiration))
                .setId(UUID.randomUUID().toString())
                .signWith(accessTokenKey)
                .compact();
    }

    public String generateResetPasswordToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .claim(TOKEN_TYPE_CLAIM, RESET_PASSWORD_TOKEN_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + resetPasswordTokenExpiration))
                .signWith(resetPasswordTokenKey)
                .compact();
    }

    public String getUserIdFromAccessToken(String token) {
        return parseAccessTokenClaims(token).getSubject();
    }

    public String getUserId(String token) {
        return getUserIdFromAccessToken(token);
    }

    public String getUserIdFromRefreshToken(String token) {
        return parseRefreshTokenClaims(token).getSubject();
    }

    public String getUserIdFromResetToken(String token) {
        Claims claims = parseClaims(token, resetPasswordTokenKey);
        validateTokenType(claims, RESET_PASSWORD_TOKEN_TYPE);
        return claims.getSubject();
    }

    public String getRole(String token) {
        return parseAccessTokenClaims(token).get(ROLE_CLAIM, String.class);
    }

    public String getSellerId(String token) {
        return parseAccessTokenClaims(token).get(SELLER_ID_CLAIM, String.class);
    }

    public boolean validateToken(String token) {
        return validateAccessToken(token);
    }

    public boolean validateAccessToken(String token) {
        try {
            parseAccessTokenClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            parseRefreshTokenClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String buildAccessToken(String userId, String role, String sellerId) {
        Date now = new Date();
        var builder = Jwts.builder()
                .setSubject(userId)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .claim(ROLE_CLAIM, role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration));
        if (sellerId != null && !sellerId.isBlank()) {
            builder.claim(SELLER_ID_CLAIM, sellerId);
        }
        return builder.signWith(accessTokenKey).compact();
    }

    private Claims parseAccessTokenClaims(String token) {
        Claims claims = parseClaims(token, accessTokenKey);
        validateTokenType(claims, ACCESS_TOKEN_TYPE);
        return claims;
    }

    private Claims parseRefreshTokenClaims(String token) {
        Claims claims = parseClaims(token, accessTokenKey);
        validateTokenType(claims, REFRESH_TOKEN_TYPE);
        return claims;
    }

    private Claims parseClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void validateTokenType(Claims claims, String expectedType) {
        String actualType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        if (!expectedType.equals(actualType)) {
            throw new JwtException("Invalid token type: expected " + expectedType + " but got " + actualType);
        }
    }
}
