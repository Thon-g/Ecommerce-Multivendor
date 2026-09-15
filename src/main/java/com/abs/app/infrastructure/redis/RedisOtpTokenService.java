package com.abs.app.infrastructure.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.abs.app.domain.service.OtpTokenService;

import java.util.concurrent.TimeUnit;

@Service
public class RedisOtpTokenService implements OtpTokenService {
    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String RESET_PASSWORD_KEY_PREFIX = "reset-password:";
    private static final String EMAIL_VERIFIED_KEY_PREFIX = "email-verified:";
    private static final String VERIFIED_VALUE = "true";

    private final StringRedisTemplate redisTemplate;

    public RedisOtpTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveOtp(String email, String otp, long expirationMinutes) {
        saveToken(OTP_KEY_PREFIX, email, otp, expirationMinutes);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        return verifyToken(OTP_KEY_PREFIX, email, otp);
    }

    @Override
    public void invalidateOtp(String email) {
        invalidateToken(OTP_KEY_PREFIX, email);
    }

    @Override
    public void saveResetPasswordToken(String email, String token, long expirationMinutes) {
        saveToken(RESET_PASSWORD_KEY_PREFIX, email, token, expirationMinutes);
    }

    @Override
    public boolean verifyResetPasswordToken(String email, String token) {
        return verifyToken(RESET_PASSWORD_KEY_PREFIX, email, token);
    }

    @Override
    public void invalidateResetPasswordToken(String email) {
        invalidateToken(RESET_PASSWORD_KEY_PREFIX, email);
    }

    @Override
    public void markEmailVerified(String email, long expirationMinutes) {
        saveToken(EMAIL_VERIFIED_KEY_PREFIX, email, VERIFIED_VALUE, expirationMinutes);
    }

    @Override
    public boolean isEmailVerified(String email) {
        return VERIFIED_VALUE.equals(redisTemplate.opsForValue().get(buildKey(EMAIL_VERIFIED_KEY_PREFIX, email)));
    }

    @Override
    public void invalidateEmailVerified(String email) {
        invalidateToken(EMAIL_VERIFIED_KEY_PREFIX, email);
    }

    private void saveToken(String prefix, String email, String token, long expirationMinutes) {
        redisTemplate.opsForValue().set(buildKey(prefix, email), token, expirationMinutes, TimeUnit.MINUTES);
    }

    private boolean verifyToken(String prefix, String email, String token) {
        if (token == null) {
            return false;
        }
        String savedToken = redisTemplate.opsForValue().get(buildKey(prefix, email));
        return token.equals(savedToken);
    }

    private void invalidateToken(String prefix, String email) {
        redisTemplate.delete(buildKey(prefix, email));
    }

    private String buildKey(String prefix, String email) {
        return prefix + email.trim().toLowerCase();
    }
}
