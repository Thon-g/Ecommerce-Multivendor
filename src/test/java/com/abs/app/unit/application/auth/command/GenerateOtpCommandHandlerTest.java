package com.abs.app.unit.application.auth.command;

import com.abs.app.application.auth.command.GenerateOtpCommand;
import com.abs.app.application.auth.command.GenerateOtpCommandHandler;
import com.abs.app.application.auth.dto.GenerateOtpResponseDto;
import com.abs.app.common.constant.AuthConstant;
import com.abs.app.common.exception.DuplicateResourceException;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.EmailService;
import com.abs.app.domain.service.OtpTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateOtpCommandHandlerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private OtpTokenService otpTokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GenerateOtpCommandHandler handler;

    private GenerateOtpCommand command;

    @BeforeEach
    void setUp() {
        command = new GenerateOtpCommand("test@gmail.com");
    }

    @Test
    @DisplayName("Tạo OTP thất bại: Ném ngoại lệ khi Email đã tồn tại (đã đăng ký)")
    void shouldThrowDuplicateResourceException_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(AuthConstant.EMAIL_EXIST);

        verifyNoInteractions(emailService, otpTokenService);
    }

    @Test
    @DisplayName("Tạo OTP thành công: Sinh mã OTP, gửi Email và lưu vào Redis")
    void shouldGenerateOtpSuccessfully() {
        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.empty());

        GenerateOtpResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(AuthConstant.SEND_OTP_SUCCESS);

        verify(emailService).sendVerifyOtp(eq("test@gmail.com"), anyString());
        verify(otpTokenService).saveOtp(eq("test@gmail.com"), anyString(), eq(1L));
    }
}
