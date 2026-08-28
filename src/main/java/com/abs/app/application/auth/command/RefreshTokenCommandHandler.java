package com.abs.app.application.auth.command;

import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.RefreshTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenCommandHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    public AuthResponseDto handle(RefreshTokenCommand command) {
        String refreshToken = command.getRefreshToken();
        String userId;
        try {
            userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new UnauthorizedException(AuthConstant.INVALID_TOKEN);
        }

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException(AuthConstant.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new UnauthorizedException(AuthConstant.PROHIBIT_ACCOUNT_MESSAGE);
        }

        if (!refreshTokenService.isValid(user.getUserId(), refreshToken)) {
            throw new UnauthorizedException(AuthConstant.INVALID_TOKEN);
        }

        String roleStr = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getRoleName().toString())
                .orElse("CUSTOMER");

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                userId,
                roleStr);

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        refreshTokenService.save(userId, newRefreshToken, refreshTokenExpirationMinutes());
        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private long refreshTokenExpirationMinutes() {
        return Math.max(1, Duration.ofMillis(refreshTokenExpirationMs).toMinutes());
    }
}
