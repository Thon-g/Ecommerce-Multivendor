package com.abs.app.unit.application.publicapi.home.query;

import com.abs.app.application.admin.banner.dto.HomeDataResponseDto;
import com.abs.app.application.publicapi.home.query.GetHomeDataQuery;
import com.abs.app.application.publicapi.home.query.GetHomeDataQueryHandler;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetHomeDataQueryHandlerTest {

    @Mock
    private HomeCategoryRepository homeCategoryRepository;

    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private GetHomeDataQueryHandler handler;

    @Test
    @DisplayName("Should aggregate home data successfully")
    void handle_Success() {
        HomeCategory gridCat = new HomeCategory(1L, "Grid 1", "img1.png", "cat1", HomeCategorySection.GRID);
        HomeCategory shopCat = new HomeCategory(2L, "Shop 1", "img2.png", "cat2", HomeCategorySection.SHOP_BY_CATEGORIES);
        HomeCategory electricCat = new HomeCategory(3L, "Elec 1", "img3.png", "cat3", HomeCategorySection.ELECTRIC_CATEGORIES);
        HomeCategory dealCat = new HomeCategory(4L, "Deal 1", "img4.png", "cat4", HomeCategorySection.DEALS);
        Deal deal = new Deal(10L, 15, dealCat);

        when(homeCategoryRepository.findBySection(HomeCategorySection.GRID)).thenReturn(List.of(gridCat));
        when(homeCategoryRepository.findBySection(HomeCategorySection.SHOP_BY_CATEGORIES)).thenReturn(List.of(shopCat));
        when(homeCategoryRepository.findBySection(HomeCategorySection.ELECTRIC_CATEGORIES)).thenReturn(List.of(electricCat));
        when(homeCategoryRepository.findBySection(HomeCategorySection.DEALS)).thenReturn(List.of(dealCat));
        when(dealRepository.findAll()).thenReturn(List.of(deal));

        HomeDataResponseDto response = handler.handle(new GetHomeDataQuery());

        assertThat(response).isNotNull();
        assertThat(response.getGrid()).hasSize(1);
        assertThat(response.getShopByCategories()).hasSize(1);
        assertThat(response.getElectricCategories()).hasSize(1);
        assertThat(response.getDealCategories()).hasSize(1);
        assertThat(response.getDeals()).hasSize(1);
        assertThat(response.getDeals().get(0).getDiscount()).isEqualTo(15);
    }
}

