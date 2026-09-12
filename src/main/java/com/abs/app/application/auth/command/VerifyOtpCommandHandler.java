package com.abs.app.application.auth.command;

import org.springframework.stereotype.Component;

import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.service.OtpTokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerifyOtpCommandHandler {
    private static final int EMAIL_VERIFIED_TTL_MINUTES = 10;

    private final OtpTokenService otpTokenService;

    public void handle(VerifyOtpCommand command) {
        boolean isValid = otpTokenService.verifyOtp(command.getEmail(), command.getOtp());
        if (!isValid) {
            throw new BusinessException(AuthConstant.INVALID_OTP);
        }
        otpTokenService.invalidateOtp(command.getEmail());
        otpTokenService.markEmailVerified(command.getEmail(), EMAIL_VERIFIED_TTL_MINUTES);
    }
}
