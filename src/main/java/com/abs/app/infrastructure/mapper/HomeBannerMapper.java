package com.abs.app.infrastructure.mapper;

import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;

import java.util.Collections;
import java.util.List;

public final class HomeBannerMapper {

    private HomeBannerMapper() {}

    public static HomeCategoryResponseDto toHomeCategoryResponseDto(HomeCategory entity) {
        if (entity == null) {
            return null;
        }
        return new HomeCategoryResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getImage(),
                entity.getCategoryId(),
                entity.getSection()
        );
    }

    public static List<HomeCategoryResponseDto> toHomeCategoryResponseDtoList(List<HomeCategory> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(HomeBannerMapper::toHomeCategoryResponseDto)
                .toList();
    }

    public static DealResponseDto toDealResponseDto(Deal deal) {
        if (deal == null) {
            return null;
        }
        return new DealResponseDto(
                deal.getId(),
                deal.getDiscount(),
                toHomeCategoryResponseDto(deal.getCategory())
        );
    }

    public static List<DealResponseDto> toDealResponseDtoList(List<Deal> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(HomeBannerMapper::toDealResponseDto)
                .toList();
    }
}

