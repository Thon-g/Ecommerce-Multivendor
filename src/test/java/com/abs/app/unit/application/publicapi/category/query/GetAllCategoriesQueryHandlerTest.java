package com.abs.app.unit.application.publicapi.category.query;

import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.application.publicapi.category.query.GetAllCategoriesQuery;
import com.abs.app.application.publicapi.category.query.GetAllCategoriesQueryHandler;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllCategoriesQueryHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private GetAllCategoriesQueryHandler handler;

    private GetAllCategoriesQuery query;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        query = new GetAllCategoriesQuery("keyword", 0, 10);

        mockCategory = new Category();
        mockCategory.setId("CATEGORY_ID");
        mockCategory.setName("Electronic");
        mockCategory.setLevel(1);
    }

    @Test
    @DisplayName("Lấy danh sách Category thành công, trả về PageResponse")
    void shouldReturnPageResponse_WhenGetCategories() {
        Page<Category> categoryPage = new PageImpl<>(List.of(mockCategory));
        
        when(categoryRepository.search(eq("keyword"), any(Pageable.class))).thenReturn(categoryPage);

        PageResponse<CategoryResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getName()).isEqualTo("Electronic");
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getLimit()).isEqualTo(10);

        verify(categoryRepository).search(eq("keyword"), any(Pageable.class));
    }
    
    @Test
    @DisplayName("Lấy danh sách Category thành công (Empty)")
    void shouldReturnEmptyPageResponse_WhenNoCategoriesFound() {
        Page<Category> categoryPage = new PageImpl<>(Collections.emptyList());
        
        when(categoryRepository.search(eq("keyword"), any(Pageable.class))).thenReturn(categoryPage);

        PageResponse<CategoryResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0);

        verify(categoryRepository).search(eq("keyword"), any(Pageable.class));
    }
}
