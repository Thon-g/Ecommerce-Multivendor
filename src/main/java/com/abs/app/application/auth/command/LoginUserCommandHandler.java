package com.abs.app.application.auth.command;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.RefreshTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUserCommandHandler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    public AuthResponseDto handle(LoginUserCommand command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new UnauthorizedException(AuthConstant.INVALID_USERNAME_OR_PASSWORD));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new UnauthorizedException(AuthConstant.PROHIBIT_ACCOUNT_MESSAGE);
        }

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(AuthConstant.INVALID_USERNAME_OR_PASSWORD);
        }
        String roleStr = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getRoleName().toString())
                .orElse("CUSTOMER");
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), roleStr);
        if (command.isRememberMe()) {
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
            refreshTokenService.save(user.getUserId(), refreshToken, refreshTokenExpirationMinutes());

            return AuthResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }

    private long refreshTokenExpirationMinutes() {
        return Math.max(1, Duration.ofMillis(refreshTokenExpirationMs).toMinutes());
    }
}
