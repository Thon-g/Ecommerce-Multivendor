package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.ForgotPasswordCommand;
import com.abs.app.application.auth.command.ForgotPasswordCommandHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.EmailService;
import com.abs.app.domain.service.OtpTokenService;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordCommandHandlerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpTokenService otpTokenService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ForgotPasswordCommandHandler handler;

    private ForgotPasswordCommand command;
    private User mockUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "resetTokenExpirationMs", 900000L); // 15 minutes = 900,000 ms

        command = new ForgotPasswordCommand("test@gmail.com");

        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setEmail("test@gmail.com");
    }

    @Test
    @DisplayName("Quên mật khẩu thất bại: Ném ngoại lệ khi Email không tồn tại trong hệ thống")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verifyNoInteractions(jwtTokenProvider, emailService, otpTokenService);
    }

    @Test
    @DisplayName("Quên mật khẩu thành công: Tạo Reset Token, Gửi Email và Lưu Token")
    void shouldHandleForgotPasswordSuccessfully() {
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtTokenProvider.generateResetPasswordToken(mockUser.getUserId())).thenReturn("mock-reset-token");

        handler.handle(command);

        verify(jwtTokenProvider).generateResetPasswordToken("user123");
        verify(emailService).sendResetPasswordEmail("test@gmail.com", "mock-reset-token");
        verify(otpTokenService).saveResetPasswordToken(eq("test@gmail.com"), eq("mock-reset-token"), eq(15L));
    }
}
