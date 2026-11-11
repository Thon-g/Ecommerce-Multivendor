package com.abs.app.application.publicapi.home.query;

import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.application.admin.banner.dto.HomeDataResponseDto;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.entity.enums.HomeCategorySection;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.mapper.HomeBannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetHomeDataQueryHandler {

    private final HomeCategoryRepository homeCategoryRepository;
    private final DealRepository dealRepository;

    @Transactional(readOnly = true)
    public HomeDataResponseDto handle(GetHomeDataQuery query) {
        List<HomeCategory> gridList = homeCategoryRepository.findBySection(HomeCategorySection.GRID);
        List<HomeCategory> shopByList = homeCategoryRepository.findBySection(HomeCategorySection.SHOP_BY_CATEGORIES);
        List<HomeCategory> electricList = homeCategoryRepository.findBySection(HomeCategorySection.ELECTRIC_CATEGORIES);
        List<HomeCategory> dealCategoriesList = homeCategoryRepository.findBySection(HomeCategorySection.DEALS);
        List<Deal> dealsList = dealRepository.findAll();

        HomeDataResponseDto response = new HomeDataResponseDto();
        response.setGrid(HomeBannerMapper.toHomeCategoryResponseDtoList(gridList));
        response.setShopByCategories(HomeBannerMapper.toHomeCategoryResponseDtoList(shopByList));
        response.setElectricCategories(HomeBannerMapper.toHomeCategoryResponseDtoList(electricList));
        response.setDealCategories(HomeBannerMapper.toHomeCategoryResponseDtoList(dealCategoriesList));
        response.setDeals(HomeBannerMapper.toDealResponseDtoList(dealsList));

        return response;
    }
}

