package com.abs.app.unit.application.admin.banner.query;

import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.application.admin.banner.query.GetHomeCategoriesQuery;
import com.abs.app.application.admin.banner.query.GetHomeCategoriesQueryHandler;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.HomeCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetHomeCategoriesQueryHandlerTest {

    @Mock
    private HomeCategoryRepository homeCategoryRepository;

    @InjectMocks
    private GetHomeCategoriesQueryHandler handler;

    @Test
    @DisplayName("Should return home categories filtered by section")
    void handle_WithSection() {
        HomeCategory cat = new HomeCategory(1L, "Grid 1", "img.png", "cat1", HomeCategorySection.GRID);
        when(homeCategoryRepository.findBySection(HomeCategorySection.GRID)).thenReturn(List.of(cat));

        List<HomeCategoryResponseDto> result = handler.handle(new GetHomeCategoriesQuery(HomeCategorySection.GRID));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Grid 1");
        verify(homeCategoryRepository).findBySection(HomeCategorySection.GRID);
    }

    @Test
    @DisplayName("Should return all home categories when section is null")
    void handle_All() {
        HomeCategory cat = new HomeCategory(1L, "Cat 1", "img.png", "cat1", HomeCategorySection.GRID);
        when(homeCategoryRepository.findAll()).thenReturn(List.of(cat));

        List<HomeCategoryResponseDto> result = handler.handle(new GetHomeCategoriesQuery(null));

        assertThat(result).hasSize(1);
        verify(homeCategoryRepository).findAll();
    }
}

