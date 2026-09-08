package com.abs.app.application.auth.command;

import com.abs.app.domain.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutAllCommandHandler {

    private final RefreshTokenService refreshTokenService;

    public void handle(LogoutAllCommand command) {
        String userId = command.getUserId();
        if (userId != null) {
            refreshTokenService.invalidateAll(userId);
        }
    }
}
