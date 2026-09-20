package com.abs.app.unit.application.user.info.command;

import com.abs.app.application.user.info.command.UpdateUserImageCommand;
import com.abs.app.application.user.info.command.UpdateUserImageCommandHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserImageCommandHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UpdateUserImageCommandHandler handler;

    private UpdateUserImageCommand command;
    private User mockUser;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile("picture", "avatar.jpg", "image/jpeg", "dummy image content".getBytes());
        command = new UpdateUserImageCommand("user123", mockFile);
        
        mockUser = new User();
        mockUser.setUserId("user123");
        mockUser.setPicture("old_avatar.jpg");
    }

    @Test
    @DisplayName("Cập nhật ảnh đại diện thất bại: Ném ngoại lệ khi User không tồn tại")
    void shouldThrowRuntimeException_WhenUserNotFound() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verifyNoInteractions(fileStorageService);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cập nhật ảnh đại diện thành công: Lưu trữ file và cập nhật URL ảnh trong Database")
    void shouldUpdateUserImageSuccessfully() {
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(mockUser));
        when(fileStorageService.storeAvatar(mockFile, "user123")).thenReturn("http://localhost/new_avatar.jpg");

        handler.handle(command);

        assertThat(mockUser.getPicture()).isEqualTo("http://localhost/new_avatar.jpg");
        verify(fileStorageService).storeAvatar(mockFile, "user123");
        verify(userRepository).save(mockUser);
    }
    
    @Test
    @DisplayName("Cập nhật ảnh đại diện thành công nhưng không có file: Bỏ qua upload, chỉ lưu User")
    void shouldSaveUserWithoutUpdatingImage_WhenPictureIsNull() {
        command = new UpdateUserImageCommand("user123", null);
        when(userRepository.findById(command.getUserId())).thenReturn(Optional.of(mockUser));

        handler.handle(command);

        assertThat(mockUser.getPicture()).isEqualTo("old_avatar.jpg"); // Image remains unchanged
        verifyNoInteractions(fileStorageService);
        verify(userRepository).save(mockUser);
    }
}
