package com.abs.app.unit.application.publicapi.category.query;

import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.application.publicapi.category.query.GetCategoryByIdQuery;
import com.abs.app.application.publicapi.category.query.GetCategoryByIdQueryHandler;
import com.abs.app.common.constant.CategoryConstant;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCategoryByIdQueryHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetCategoryByIdQueryHandler handler;

    private GetCategoryByIdQuery query;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        query = new GetCategoryByIdQuery("CATEGORY_ID");

        mockCategory = new Category();
        mockCategory.setId("CATEGORY_ID");
        mockCategory.setName("Electronic");
    }

    @Test
    @DisplayName("Lấy chi tiết Category thất bại: Ném ngoại lệ khi không tìm thấy")
    void shouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("Lấy chi tiết Category thành công")
    void shouldReturnCategoryDetailsSuccessfully() {
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));

        CategoryResponseDto response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("CATEGORY_ID");
        assertThat(response.getName()).isEqualTo("Electronic");

        verify(categoryRepository).findById("CATEGORY_ID");
    }
}
