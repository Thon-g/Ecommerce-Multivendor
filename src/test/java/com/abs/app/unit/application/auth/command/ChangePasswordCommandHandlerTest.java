package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.ChangePasswordCommand;
import com.abs.app.application.auth.command.ChangePasswordCommandHandler;
import com.abs.app.application.auth.dto.AuthResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.domain.entity.Role;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.RoleUser;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ChangePasswordCommandHandler handler;

    private ChangePasswordCommand command;
    private User mockUser;

    @BeforeEach
    void setUp() {
        command = new ChangePasswordCommand("user123", "OldPass@123", "NewPass@123", "NewPass@123");

        Role role = new Role();
        role.setRoleName(RoleUser.CUSTOMER);

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setPassword("encodedOldPassword");
        mockUser.getRoles().add(role);
    }

    @Test
    @DisplayName("Đổi mật khẩu thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
                
        verifyNoInteractions(passwordEncoder, jwtTokenProvider);
    }

    @Test
    @DisplayName("Đổi mật khẩu thất bại: Ném ngoại lệ khi Mật khẩu hiện tại không khớp")
    void shouldThrowUnauthorizedException_WhenCurrentPasswordIsWrong() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(command.getCurrentPassword(), mockUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_CURRENT_PASSWORD);
                
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("Đổi mật khẩu thất bại: Ném ngoại lệ khi Mật khẩu mới và Nhập lại không khớp")
    void shouldThrowUnauthorizedException_WhenNewPasswordsDoNotMatch() {
        command = new ChangePasswordCommand("user123", "OldPass@123", "NewPass@123", "DifferentPass@123");

        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(command.getCurrentPassword(), mockUser.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_RE_PASSWORD);
                
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("Đổi mật khẩu thành công: Lưu mật khẩu mới và trả về Access Token")
    void shouldChangePasswordSuccessfully_AndReturnAccessToken() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(command.getCurrentPassword(), mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(command.getNewPassword())).thenReturn("encodedNewPassword");
        when(jwtTokenProvider.generateAccessToken(mockUser.getUserId(), "CUSTOMER")).thenReturn("mock-access-token");

        AuthResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
        
        assertThat(mockUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(userRepository).save(mockUser);
    }
}
