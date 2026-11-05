package com.abs.app.unit.application.admin.banner.command;

import com.abs.app.application.admin.banner.command.UpdateHomeCategoryCommand;
import com.abs.app.application.admin.banner.command.UpdateHomeCategoryCommandHandler;
import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateHomeCategoryCommandHandlerTest {

    @Mock
    private HomeCategoryRepository homeCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UpdateHomeCategoryCommandHandler handler;

    @Test
    @DisplayName("Should update home category successfully with new image")
    void handle_Success_WithNewImage() {
        HomeCategory existing = new HomeCategory(1L, "Old Name", "old.png", "cat1", HomeCategorySection.GRID);
        MockMultipartFile newFile = new MockMultipartFile("image", "new.jpg", "image/jpeg", "new content".getBytes());

        when(homeCategoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByCategoryId("cat2")).thenReturn(true);
        when(fileStorageService.storeBanner(any(), eq(BannerConstant.BANNER_PREFIX + "1"))).thenReturn("/images/uploads/banners/new_banner.jpg");
        when(homeCategoryRepository.save(any(HomeCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateHomeCategoryCommand command = new UpdateHomeCategoryCommand(1L, "New Name", newFile, "cat2", HomeCategorySection.DEALS);
        HomeCategoryResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getCategoryId()).isEqualTo("cat2");
        assertThat(response.getSection()).isEqualTo(HomeCategorySection.DEALS);
        assertThat(response.getImage()).isEqualTo("/images/uploads/banners/new_banner.jpg");
        verify(fileStorageService).storeBanner(newFile, BannerConstant.BANNER_PREFIX + "1");
    }

    @Test
    @DisplayName("Should update home category successfully keeping existing image when image is null")
    void handle_Success_KeepExistingImage() {
        HomeCategory existing = new HomeCategory(1L, "Old Name", "old.png", "cat1", HomeCategorySection.GRID);

        when(homeCategoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByCategoryId("cat2")).thenReturn(true);
        when(homeCategoryRepository.save(any(HomeCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateHomeCategoryCommand command = new UpdateHomeCategoryCommand(1L, "New Name", null, "cat2", HomeCategorySection.DEALS);
        HomeCategoryResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getImage()).isEqualTo("old.png");
        verify(fileStorageService, never()).storeBanner(any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when home category does not exist")
    void handle_NotFound() {
        when(homeCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateHomeCategoryCommand command = new UpdateHomeCategoryCommand(99L, "New Name", null, "cat2", HomeCategorySection.DEALS);
        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(BannerConstant.HOME_CATEGORY_NOT_FOUND);
    }
}
