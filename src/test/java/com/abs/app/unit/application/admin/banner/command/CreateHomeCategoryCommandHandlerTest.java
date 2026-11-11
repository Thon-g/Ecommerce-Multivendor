package com.abs.app.unit.application.admin.banner.command;

import com.abs.app.application.admin.banner.command.CreateHomeCategoryCommand;
import com.abs.app.application.admin.banner.command.CreateHomeCategoryCommandHandler;
import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateHomeCategoryCommandHandlerTest {

    @Mock
    private HomeCategoryRepository homeCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CreateHomeCategoryCommandHandler handler;

    private CreateHomeCategoryCommand command;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile("image", "banner.jpg", "image/jpeg", "image content".getBytes());
        command = new CreateHomeCategoryCommand(
                "Electronics Banner",
                mockFile,
                "electronics",
                HomeCategorySection.GRID
        );
    }

    @Test
    @DisplayName("Should create home category successfully when category exists")
    void handle_Success() {
        when(categoryRepository.existsByCategoryId("electronics")).thenReturn(true);
        when(fileStorageService.storeBanner(any(), any())).thenReturn("/images/uploads/banners/banner_electronics.jpg");
        when(homeCategoryRepository.save(any(HomeCategory.class))).thenAnswer(invocation -> {
            HomeCategory entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        HomeCategoryResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Electronics Banner");
        assertThat(response.getImage()).isEqualTo("/images/uploads/banners/banner_electronics.jpg");
        assertThat(response.getSection()).isEqualTo(HomeCategorySection.GRID);
        verify(fileStorageService).storeBanner(any(), eq(BannerConstant.BANNER_PREFIX + "electronics"));
        verify(homeCategoryRepository).save(any(HomeCategory.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category does not exist")
    void handle_CategoryNotFound() {
        when(categoryRepository.existsByCategoryId("electronics")).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);

        verify(homeCategoryRepository, never()).save(any());
        verify(fileStorageService, never()).storeBanner(any(), any());
    }
}
