package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.LoginUserCommand;
import com.abs.app.application.auth.command.LoginUserCommandHandler;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private LoginUserCommandHandler handler;

    private User mockUser;
    private LoginUserCommand command;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "refreshTokenExpirationMs", 86400000L); // 1 day

        Role role = new Role();
        role.setRoleName(RoleUser.CUSTOMER);

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setRoles(Set.of(role));

        command = new LoginUserCommand("test@gmail.com", "Password@123", false);
    }

    @Test
    @DisplayName("Đăng nhập thất bại: Ném ngoại lệ khi Email không tồn tại trong hệ thống")
    void shouldThrowUnauthorizedException_WhenEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_USERNAME_OR_PASSWORD);

        verify(userRepository).findByEmail(command.getEmail());
        verifyNoInteractions(passwordEncoder, jwtTokenProvider, refreshTokenService);
    }

    @Test
    @DisplayName("Đăng nhập thất bại: Ném ngoại lệ khi tài khoản User đang bị vô hiệu hóa (INACTIVE/BANNED)")
    void shouldThrowUnauthorizedException_WhenUserStatusIsNotActive() {
        // Arrange
        mockUser.setStatus(UserStatus.INACTIVE);
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(mockUser));

        // Act & Assert
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.PROHIBIT_ACCOUNT_MESSAGE);
    }

    @Test
    @DisplayName("Đăng nhập thất bại: Ném ngoại lệ khi Mật khẩu không khớp")
    void shouldThrowUnauthorizedException_WhenPasswordDoesNotMatch() {
        // Arrange
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(command.getPassword(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_USERNAME_OR_PASSWORD);
    }

    @Test
    @DisplayName("Đăng nhập thành công: Trả về Access Token khi Không tích Remember Me")
    void shouldReturnAccessToken_WhenLoginSuccessfulWithoutRememberMe() {
        // Arrange
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(command.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(mockUser.getUserId(), "CUSTOMER")).thenReturn("mock-access-token");

        // Act
        AuthResponseDto response = handler.handle(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
        assertThat(response.getRefreshToken()).isNull();

        verify(refreshTokenService, never()).save(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Đăng nhập thành công: Trả về Access Token và Refresh Token khi Có tích Remember Me")
    void shouldReturnAccessAndRefreshToken_WhenLoginSuccessfulWithRememberMe() {
        // Arrange
        command = new LoginUserCommand("test@gmail.com", "Password@123", true);
        
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(command.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(mockUser.getUserId(), "CUSTOMER")).thenReturn("mock-access-token");
        when(jwtTokenProvider.generateRefreshToken(eq(mockUser.getUserId()), anyString())).thenReturn("mock-refresh-token");
        when(jwtTokenProvider.getTokenIdFromRefreshToken("mock-refresh-token")).thenReturn("mock-token-id");

        // Act
        AuthResponseDto response = handler.handle(command);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token");

        verify(refreshTokenService).save(eq("user123"), anyString(), eq("mock-token-id"), eq(1440L)); // 86400000ms = 1440 mins
    }
}
