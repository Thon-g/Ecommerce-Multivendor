package com.abs.app.unit.application.admin.category.command;

import com.abs.app.application.admin.category.command.UpdateCategoryCommand;
import com.abs.app.application.admin.category.command.UpdateCategoryCommandHandler;
import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.service.CategoryTreeService;
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
class UpdateCategoryCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryTreeService categoryTreeService;

    @InjectMocks
    private UpdateCategoryCommandHandler handler;

    private UpdateCategoryCommand command;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        command = new UpdateCategoryCommand("CATEGORY_ID", "Updated Category");

        mockCategory = new Category();
        mockCategory.setId("CATEGORY_ID");
        mockCategory.setName("Old Category");
    }

    @Test
    @DisplayName("Cập nhật Category thất bại: Ném ngoại lệ khi Category không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Cập nhật Category thành công")
    void shouldUpdateCategorySuccessfully() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockCategory.getName()).isEqualTo("Updated Category"); // Object should be mutated
        assertThat(response.getName()).isEqualTo("Updated Category");

        verify(categoryRepository).save(mockCategory);
    }
}
