package com.abs.app.application.auth.command;

import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.abs.app.domain.service.OtpTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ForgotPasswordCommandHandler {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OtpTokenService otpTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${security.jwt.reset-expiration}")
    private long resetTokenExpirationMs;

    public void handle(ForgotPasswordCommand command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));
        String resetPasswordToken = jwtTokenProvider.generateResetPasswordToken(user.getUserId());
        emailService.sendResetPasswordEmail(user.getEmail(), resetPasswordToken);
        otpTokenService.saveResetPasswordToken(user.getEmail(), resetPasswordToken, resetTokenExpirationMinutes());
    }

    private long resetTokenExpirationMinutes() {
        return Math.max(1, Duration.ofMillis(resetTokenExpirationMs).toMinutes());
    }
}
