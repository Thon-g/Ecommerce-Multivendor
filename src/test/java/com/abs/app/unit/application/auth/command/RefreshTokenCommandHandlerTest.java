package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.RefreshTokenCommand;
import com.abs.app.application.auth.command.RefreshTokenCommandHandler;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.RefreshTokenService;
import com.abs.app.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenCommandHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenCommandHandler handler;

    private RefreshTokenCommand command;
    private User mockUser;
    private final String REFRESH_TOKEN = "valid-refresh-token";
    private final String USER_ID = "USER_ID";
    private final String FAMILY_ID = "FAMILY_ID";
    private final String OLD_TOKEN_ID = "OLD_TOKEN_ID";
    private final String NEW_TOKEN_ID = "NEW_TOKEN_ID";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "refreshTokenExpirationMs", 604800000L); // 7 days in ms
        command = new RefreshTokenCommand(REFRESH_TOKEN);

        Role role = new Role();
        role.setRoleName(RoleUser.CUSTOMER);

        mockUser = new User();
        mockUser.setUserId(USER_ID);
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setRoles(Set.of(role));
    }

    @Test
    @DisplayName("Làm mới token thất bại: Ném ngoại lệ khi token không hợp lệ (không lấy được userId)")
    void shouldThrowUnauthorizedException_WhenExtractUserIdFails() {
        when(jwtTokenProvider.getUserIdFromRefreshToken(REFRESH_TOKEN)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_TOKEN);
    }

    @Test
    @DisplayName("Làm mới token thất bại: Ném ngoại lệ khi validate token thất bại")
    void shouldThrowUnauthorizedException_WhenTokenInvalid() {
        when(jwtTokenProvider.getUserIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtTokenProvider.validateRefreshToken(REFRESH_TOKEN)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_TOKEN);
    }

    @Test
    @DisplayName("Làm mới token thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(jwtTokenProvider.getUserIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtTokenProvider.getFamilyIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(FAMILY_ID);
        when(jwtTokenProvider.getTokenIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(OLD_TOKEN_ID);
        when(jwtTokenProvider.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
    }

    @Test
    @DisplayName("Làm mới token thất bại: Ném ngoại lệ khi User bị khóa/vô hiệu hóa")
    void shouldThrowUnauthorizedException_WhenUserNotActive() {
        mockUser.setStatus(UserStatus.INACTIVE);

        when(jwtTokenProvider.getUserIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtTokenProvider.getFamilyIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(FAMILY_ID);
        when(jwtTokenProvider.getTokenIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(OLD_TOKEN_ID);
        when(jwtTokenProvider.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.PROHIBIT_ACCOUNT_MESSAGE);
    }

    @Test
    @DisplayName("Làm mới token thất bại: Ném ngoại lệ khi RefreshToken không khớp trong Redis (Reuse Detection)")
    void shouldThrowUnauthorizedException_WhenTokenNotInRedis() {
        when(jwtTokenProvider.getUserIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtTokenProvider.getFamilyIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(FAMILY_ID);
        when(jwtTokenProvider.getTokenIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(OLD_TOKEN_ID);
        when(jwtTokenProvider.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(jwtTokenProvider.generateAccessToken(USER_ID, "CUSTOMER")).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(USER_ID, FAMILY_ID)).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getTokenIdFromRefreshToken("new-refresh-token")).thenReturn(NEW_TOKEN_ID);
        
        when(refreshTokenService.rotate(eq(USER_ID), eq(FAMILY_ID), eq(OLD_TOKEN_ID), eq(NEW_TOKEN_ID), anyLong())).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_TOKEN);
    }

    @Test
    @DisplayName("Làm mới token thành công")
    void shouldRefreshTokenSuccessfully() {
        when(jwtTokenProvider.getUserIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(USER_ID);
        when(jwtTokenProvider.getFamilyIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(FAMILY_ID);
        when(jwtTokenProvider.getTokenIdFromRefreshToken(REFRESH_TOKEN)).thenReturn(OLD_TOKEN_ID);
        when(jwtTokenProvider.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        
        when(jwtTokenProvider.generateAccessToken(USER_ID, "CUSTOMER")).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(USER_ID, FAMILY_ID)).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getTokenIdFromRefreshToken("new-refresh-token")).thenReturn(NEW_TOKEN_ID);
        
        when(refreshTokenService.rotate(eq(USER_ID), eq(FAMILY_ID), eq(OLD_TOKEN_ID), eq(NEW_TOKEN_ID), anyLong())).thenReturn(true);

        AuthResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(refreshTokenService).rotate(eq(USER_ID), eq(FAMILY_ID), eq(OLD_TOKEN_ID), eq(NEW_TOKEN_ID), anyLong());
    }
}
