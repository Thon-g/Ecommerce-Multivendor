package com.abs.app.application.admin.banner.command;

import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.repository.DealRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.persistence.jpa.DealJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteHomeCategoryCommandHandler {

    private final HomeCategoryRepository homeCategoryRepository;
    private final DealJpaRepository dealJpaRepository;

    @Transactional
    public void handle(DeleteHomeCategoryCommand command) {
        HomeCategory homeCategory = homeCategoryRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(BannerConstant.HOME_CATEGORY_NOT_FOUND));

        dealJpaRepository.findByCategoryId(homeCategory.getId()).ifPresent(dealJpaRepository::delete);
        homeCategoryRepository.delete(homeCategory);
    }
}

