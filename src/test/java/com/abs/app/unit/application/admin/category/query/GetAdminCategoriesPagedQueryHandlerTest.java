package com.abs.app.unit.application.admin.category.query;

import com.abs.app.application.admin.category.query.GetAdminCategoriesPagedQuery;
import com.abs.app.application.admin.category.query.GetAdminCategoriesPagedQueryHandler;
import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminCategoriesPagedQueryHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetAdminCategoriesPagedQueryHandler handler;

    @Test
    @DisplayName("Should return paged categories for admin successfully")
    void handle_Success() {
        Category cat = new Category();
        cat.setId("cat_1");
        cat.setName("Fashion");
        cat.setCategoryId("fashion");
        cat.setLevel(1);
        cat.setCommissionRate(10.0);

        Page<Category> paged = new PageImpl<>(List.of(cat));
        when(categoryRepository.search(eq("fashion"), eq(1), any(Pageable.class))).thenReturn(paged);

        GetAdminCategoriesPagedQuery query = new GetAdminCategoriesPagedQuery("fashion", 1, 1, 10);
        PageResponse<CategoryResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getName()).isEqualTo("Fashion");
        assertThat(response.getItems().get(0).getCommissionRate()).isEqualTo(10.0);
        assertThat(response.getTotal()).isEqualTo(1);
    }
}
