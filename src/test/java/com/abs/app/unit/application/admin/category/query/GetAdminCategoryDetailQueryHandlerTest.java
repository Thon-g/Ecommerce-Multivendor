package com.abs.app.unit.application.admin.category.query;

import com.abs.app.application.admin.category.query.GetAdminCategoryDetailQuery;
import com.abs.app.application.admin.category.query.GetAdminCategoryDetailQueryHandler;
import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminCategoryDetailQueryHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetAdminCategoryDetailQueryHandler handler;

    @Test
    @DisplayName("Should return category detail with children successfully")
    void handle_Success() {
        Category parent = new Category();
        parent.setId("cat_root");
        parent.setName("Electronics");
        parent.setCategoryId("electronics");
        parent.setLevel(1);
        parent.setCommissionRate(8.0);

        Category child = new Category();
        child.setId("cat_child");
        child.setName("Laptops");
        child.setCategoryId("laptops");
        child.setParentCategory(parent);
        child.setLevel(2);

        when(categoryRepository.findById("cat_root")).thenReturn(Optional.of(parent));
        when(categoryRepository.findByParentCategoryId(eq("cat_root"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(child)));

        CategoryResponseDto response = handler.handle(new GetAdminCategoryDetailQuery("cat_root"));

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Electronics");
        assertThat(response.getChildren()).hasSize(1);
        assertThat(response.getChildren().get(0).getName()).isEqualTo("Laptops");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found")
    void handle_NotFound() {
        when(categoryRepository.findById("missing_id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetAdminCategoryDetailQuery("missing_id")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);
    }
}

