package com.abs.app.unit.application.user.info.command;

import com.abs.app.application.user.info.command.UpdateUserProfileCommand;
import com.abs.app.application.user.info.command.UpdateUserProfileCommandHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserProfileCommandHandler handler;

    private UpdateUserProfileCommand command;
    private User mockUser;

    @BeforeEach
    void setUp() {
        command = new UpdateUserProfileCommand("user123", "John", "Doe", "0123456789", true);
        
        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setFirstName("Old");
        mockUser.setLastName("Name");
    }

    @Test
    @DisplayName("Cập nhật thông tin thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowRuntimeException_WhenUserNotFound() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cập nhật thông tin thành công: Thay đổi thông tin cá nhân và lưu vào Database")
    void shouldUpdateUserProfileSuccessfully() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(mockUser));

        handler.handle(command);

        assertThat(mockUser.getFirstName()).isEqualTo("John");
        assertThat(mockUser.getLastName()).isEqualTo("Doe");
        assertThat(mockUser.getPhoneNumber()).isEqualTo("0123456789");
        assertThat(mockUser.isGender()).isTrue();

        verify(userRepository).save(mockUser);
    }
}
