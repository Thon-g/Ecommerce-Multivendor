package com.abs.app.application.admin.banner.command;

import com.abs.app.application.admin.banner.dto.DealResponseDto;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Deal;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.mapper.HomeBannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDealCommandHandler {

    private final DealRepository dealRepository;
    private final HomeCategoryRepository homeCategoryRepository;

    @Transactional
    public DealResponseDto handle(CreateDealCommand command) {
        if (command.getDiscount() == null || command.getDiscount() < 1 || command.getDiscount() > 100) {
            throw new BusinessException(BannerConstant.INVALID_DISCOUNT_PERCENT);
        }

        HomeCategory category = homeCategoryRepository.findById(command.getHomeCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(BannerConstant.HOME_CATEGORY_NOT_FOUND));

        if (dealRepository.existsByCategoryId(category.getId())) {
            throw new BusinessException(BannerConstant.DEAL_ALREADY_EXISTS);
        }

        Deal deal = new Deal();
        deal.setDiscount(command.getDiscount());
        deal.setCategory(category);

        Deal saved = dealRepository.save(deal);
        return HomeBannerMapper.toDealResponseDto(saved);
    }
}

