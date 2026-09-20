package com.abs.app.application.auth.command;

import com.abs.app.application.auth.dto.GenerateOtpResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.exception.DuplicateResourceException;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.EmailService;
import com.abs.app.domain.service.OtpTokenService;

import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateOtpCommandHandler {
    private static final int OTP_TTL_MINUTES = 1;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailService emailService;
    private final OtpTokenService otpTokenService;
    private final UserRepository userRepository;

    public GenerateOtpResponseDto handle(GenerateOtpCommand command) {
        var userOpt = userRepository.findByEmail(command.getEmail());
        if (userOpt.isPresent()) {
            throw new DuplicateResourceException(AuthConstant.EMAIL_EXIST);
        }
        String otp = generateOTP();
        String message = AuthConstant.SEND_OTP_SUCCESS;
        emailService.sendVerifyOtp(command.getEmail(), otp);
        otpTokenService.saveOtp(command.getEmail(), otp, OTP_TTL_MINUTES);
        return new GenerateOtpResponseDto(message);
    }

    private String generateOTP() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
