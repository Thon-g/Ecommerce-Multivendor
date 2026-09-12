package com.abs.app.application.auth.command;

import com.abs.app.domain.service.RefreshTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutCommandHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    public void handle(LogoutCommand command) {
        String userId = command.getUserId();
        String refreshToken = command.getRefreshToken();

        if (userId != null && refreshToken != null) {
            try {
                String familyId = jwtTokenProvider.getFamilyIdFromRefreshToken(refreshToken);
                if (familyId != null) {
                    refreshTokenService.invalidateFamily(userId, familyId);
                }
            } catch (Exception e) {
                // Ignore invalid or expired token on logout
            }
        }
    }
}
