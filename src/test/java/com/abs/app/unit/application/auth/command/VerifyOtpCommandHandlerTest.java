package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.VerifyOtpCommand;
import com.abs.app.application.auth.command.VerifyOtpCommandHandler;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.service.OtpTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerifyOtpCommandHandlerTest {

    @Mock
    private OtpTokenService otpTokenService;

    @InjectMocks
    private VerifyOtpCommandHandler handler;

    private VerifyOtpCommand command;

    @BeforeEach
    void setUp() {
        command = new VerifyOtpCommand("test@gmail.com", "123456");
    }

    @Test
    @DisplayName("Xác thực OTP thất bại: Ném ngoại lệ khi OTP sai hoặc hết hạn")
    void shouldThrowBusinessException_WhenOtpIsInvalid() {
        when(otpTokenService.verifyOtp(command.getEmail(), command.getOtp())).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(AuthConstant.INVALID_OTP);
                
        verify(otpTokenService, never()).invalidateOtp(anyString());
        verify(otpTokenService, never()).markEmailVerified(anyString(), anyInt());
    }

    @Test
    @DisplayName("Xác thực OTP thành công: Hủy mã OTP cũ và Đánh dấu Email đã verify (TTL 10 phút)")
    void shouldVerifyOtpSuccessfully() {
        when(otpTokenService.verifyOtp(command.getEmail(), command.getOtp())).thenReturn(true);

        handler.handle(command);

        verify(otpTokenService).invalidateOtp("test@gmail.com");
        verify(otpTokenService).markEmailVerified(eq("test@gmail.com"), eq(10L));
    }
}
