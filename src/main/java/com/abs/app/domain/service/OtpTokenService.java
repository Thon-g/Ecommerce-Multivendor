package com.abs.app.domain.service;

public interface OtpTokenService {
    void saveOtp(String email, String otp, long expirationMinutes);

    boolean verifyOtp(String email, String otp);

    void invalidateOtp(String email);

    void saveResetPasswordToken(String email, String token, long expirationMinutes);

    boolean verifyResetPasswordToken(String email, String token);

    void invalidateResetPasswordToken(String email);

    void markEmailVerified(String email, long expirationMinutes);

    boolean isEmailVerified(String email);

    void invalidateEmailVerified(String email);
}
