package com.abs.app.application.auth.command;

import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.OtpTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResetPasswordCommandHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpTokenService otpTokenService;

    public void handle(ResetPasswordCommand command) {
        String userId;
        try {
            userId = jwtTokenProvider.getUserIdFromResetToken(command.getToken());
        } catch (Exception e) {
            throw new UnauthorizedException(AuthConstant.INVALID_TOKEN);
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException(UserConstant.USER_NOT_EXIST);
        }

        User user = userOptional.get();
        boolean isValid = otpTokenService.verifyResetPasswordToken(user.getEmail(), command.getToken());
        if (!isValid) {
            throw new BusinessException(AuthConstant.INVALID_TOKEN);
        }
        otpTokenService.invalidateResetPasswordToken(user.getEmail());
        String encodedPassword = passwordEncoder.encode(command.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
