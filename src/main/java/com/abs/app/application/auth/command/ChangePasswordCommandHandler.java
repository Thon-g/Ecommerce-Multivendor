package com.abs.app.application.auth.command;

import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordCommandHandler {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponseDto handle(ChangePasswordCommand command) {
        User user = userRepo.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        if (!passwordEncoder.matches(command.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException(AuthConstant.INVALID_CURRENT_PASSWORD);
        }

        if (!command.getNewPassword().equals(command.getRePassword())) {
            throw new UnauthorizedException(AuthConstant.INVALID_RE_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(command.getNewPassword()));
        userRepo.save(user);
        String roleStr = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getRoleName().toString())
                .orElse("CUSTOMER");
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), roleStr);
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
