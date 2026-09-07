package com.abs.app.unit.application.admin.category.command;

import com.abs.app.application.admin.category.command.DeleteCategoryCommand;
import com.abs.app.application.admin.category.command.DeleteCategoryCommandHandler;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DeleteCategoryCommandHandler handler;

    private DeleteCategoryCommand command;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        command = new DeleteCategoryCommand("CATEGORY_ID");

        mockCategory = new Category();
        mockCategory.setId("CATEGORY_ID");
    }

    @Test
    @DisplayName("Xóa Category thất bại: Ném ngoại lệ khi Category không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Xóa Category thất bại: Ném ngoại lệ khi Category có danh mục con")
    void shouldThrowBusinessException_WhenCategoryHasChildren() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.existsByParentCategoryId("CATEGORY_ID")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CategoryConstant.CATEGORY_HAS_CHILDREN);

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Xóa Category thành công")
    void shouldDeleteCategorySuccessfully() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.existsByParentCategoryId("CATEGORY_ID")).thenReturn(false);

        handler.handle(command);

        verify(categoryRepository).delete(mockCategory);
    }
}
