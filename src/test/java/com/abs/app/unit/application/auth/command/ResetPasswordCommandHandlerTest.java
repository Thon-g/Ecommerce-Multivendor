package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.ResetPasswordCommand;
import com.abs.app.application.auth.command.ResetPasswordCommandHandler;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.exception.UnauthorizedException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.OtpTokenService;
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
class ResetPasswordCommandHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OtpTokenService otpTokenService;

    @InjectMocks
    private ResetPasswordCommandHandler handler;

    private ResetPasswordCommand command;
    private User mockUser;

    @BeforeEach
    void setUp() {
        command = new ResetPasswordCommand("mock-reset-token", "NewPass@123");
        
        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setEmail("test@gmail.com");
    }

    @Test
    @DisplayName("Đặt lại mật khẩu thất bại: Ném ngoại lệ khi Token không hợp lệ hoặc bị lỗi parse")
    void shouldThrowUnauthorizedException_WhenTokenIsInvalid() {
        when(jwtTokenProvider.getUserIdFromResetToken(command.getToken())).thenThrow(new RuntimeException("Invalid token"));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(AuthConstant.INVALID_TOKEN);

        verifyNoInteractions(userRepository, otpTokenService, passwordEncoder);
    }

    @Test
    @DisplayName("Đặt lại mật khẩu thất bại: Ném ngoại lệ khi User không tồn tại theo ID từ Token")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(jwtTokenProvider.getUserIdFromResetToken(command.getToken())).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verifyNoInteractions(otpTokenService, passwordEncoder);
    }

    @Test
    @DisplayName("Đặt lại mật khẩu thất bại: Ném ngoại lệ khi Token không khớp với Redis (hoặc đã hết hạn)")
    void shouldThrowBusinessException_WhenTokenNotVerifiedInRedis() {
        when(jwtTokenProvider.getUserIdFromResetToken(command.getToken())).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(otpTokenService.verifyResetPasswordToken(mockUser.getEmail(), command.getToken())).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthConstant.INVALID_TOKEN);
                
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Đặt lại mật khẩu thành công: Encode mật khẩu mới, lưu User và xóa Token trong Redis")
    void shouldResetPasswordSuccessfully() {
        when(jwtTokenProvider.getUserIdFromResetToken(command.getToken())).thenReturn("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(otpTokenService.verifyResetPasswordToken(mockUser.getEmail(), command.getToken())).thenReturn(true);
        when(passwordEncoder.encode(command.getNewPassword())).thenReturn("encodedNewPassword");

        handler.handle(command);

        assertThat(mockUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(otpTokenService).invalidateResetPasswordToken("test@gmail.com");
        verify(userRepository).save(mockUser);
    }
}
