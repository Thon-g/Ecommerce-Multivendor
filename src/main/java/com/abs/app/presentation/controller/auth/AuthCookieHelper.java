package com.abs.app.presentation.controller.auth;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.abs.app.application.auth.dto.AuthResponseDto;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthCookieHelper {
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final Duration accessTokenMaxAge;
    private final Duration refreshTokenMaxAge;
    private final boolean secure;
    private final String sameSite;

    public AuthCookieHelper(
            @Value("${security.jwt.expiration}") long accessTokenExpirationMs,
            @Value("${security.jwt.refresh-expiration}") long refreshTokenExpirationMs,
            @Value("${security.cookie.secure:false}") boolean secure,
            @Value("${security.cookie.same-site:Lax}") String sameSite) {
        this.accessTokenMaxAge = Duration.ofMillis(accessTokenExpirationMs);
        this.refreshTokenMaxAge = Duration.ofMillis(refreshTokenExpirationMs);
        this.secure = secure;
        this.sameSite = sameSite;
    }

    public HttpHeaders createAuthCookieHeaders(AuthResponseDto authResponse) {
        HttpHeaders headers = new HttpHeaders();
        if (authResponse.getAccessToken() != null) {
            headers.add(HttpHeaders.SET_COOKIE, createAccessTokenCookie(authResponse.getAccessToken()).toString());
        }
        if (authResponse.getRefreshToken() != null) {
            headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(authResponse.getRefreshToken()).toString());
        }
        return headers;
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return createCookie(ACCESS_TOKEN_COOKIE_NAME, token, accessTokenMaxAge);
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, token, refreshTokenMaxAge);
    }

    public HttpHeaders clearAuthCookieHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, clearCookie(ACCESS_TOKEN_COOKIE_NAME).toString());
        headers.add(HttpHeaders.SET_COOKIE, clearCookie(REFRESH_TOKEN_COOKIE_NAME).toString());
        return headers;
    }

    public Optional<String> getAccessToken(HttpServletRequest request) {
        return getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    public Optional<String> getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    private ResponseCookie createCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }
}
