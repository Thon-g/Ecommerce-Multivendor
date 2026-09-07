package com.abs.app.unit.application.admin.category.command;

import com.abs.app.application.admin.category.command.CreateCategoryCommand;
import com.abs.app.application.admin.category.command.CreateCategoryCommandHandler;
import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCategoryCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CreateCategoryCommandHandler handler;

    private CreateCategoryCommand command;
    private Category parentCategory;

    @BeforeEach
    void setUp() {
        command = new CreateCategoryCommand("Electronics", "ELEC", "PARENT_ID", 2);

        parentCategory = new Category();
        parentCategory.setId("PARENT_ID");
        parentCategory.setName("All Departments");
        parentCategory.setCategoryId("ALL");
        parentCategory.setLevel(1);
    }

    @Test
    @DisplayName("Tạo Category thất bại: Ném ngoại lệ khi CategoryId đã tồn tại")
    void shouldThrowBusinessException_WhenCategoryIdExists() {
        when(categoryRepository.existsByCategoryId("ELEC")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CategoryConstant.CATEGORY_ID_ALREADY_EXISTS);
        
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Tạo Category thất bại: Ném ngoại lệ khi ParentCategory không tồn tại")
    void shouldThrowResourceNotFoundException_WhenParentCategoryNotFound() {
        when(categoryRepository.existsByCategoryId("ELEC")).thenReturn(false);
        when(categoryRepository.findById("PARENT_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Tạo Category thành công: Với ParentCategory")
    void shouldCreateCategorySuccessfully_WithParentCategory() {
        when(categoryRepository.existsByCategoryId("ELEC")).thenReturn(false);
        when(categoryRepository.findById("PARENT_ID")).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Electronics");
        assertThat(response.getCategoryId()).isEqualTo("ELEC");
        assertThat(response.getLevel()).isEqualTo(2);
        assertThat(response.getParentCategoryId()).isNotNull();
        assertThat(response.getParentCategoryId()).isEqualTo("PARENT_ID");

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Tạo Category thành công: Không có ParentCategory")
    void shouldCreateCategorySuccessfully_WithoutParentCategory() {
        command.setParentCategoryId(null); // No parent

        when(categoryRepository.existsByCategoryId("ELEC")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Electronics");
        assertThat(response.getCategoryId()).isEqualTo("ELEC");
        assertThat(response.getLevel()).isEqualTo(2);
        assertThat(response.getParentCategoryId()).isNull();

        verify(categoryRepository, never()).findById(anyString());
        verify(categoryRepository).save(any(Category.class));
    }
}
