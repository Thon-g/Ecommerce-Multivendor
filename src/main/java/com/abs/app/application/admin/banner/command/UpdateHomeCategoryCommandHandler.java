package com.abs.app.application.admin.banner.command;

import com.abs.app.application.admin.banner.dto.HomeCategoryResponseDto;
import com.abs.app.common.constant.BannerConstant;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.HomeCategory;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.HomeCategoryRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import com.abs.app.infrastructure.mapper.HomeBannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateHomeCategoryCommandHandler {

    private final HomeCategoryRepository homeCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public HomeCategoryResponseDto handle(UpdateHomeCategoryCommand command) {
        HomeCategory homeCategory = homeCategoryRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(BannerConstant.HOME_CATEGORY_NOT_FOUND));

        if (command.getCategoryId() != null && !command.getCategoryId().equals(homeCategory.getCategoryId())) {
            if (!categoryRepository.existsByCategoryId(command.getCategoryId())) {
                throw new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND);
            }
            homeCategory.setCategoryId(command.getCategoryId());
        }

        if (command.getName() != null) {
            homeCategory.setName(command.getName());
        }
        if (command.getSection() != null) {
            homeCategory.setSection(command.getSection());
        }
        if (command.getImage() != null && !command.getImage().isEmpty()) {
            String imagePath = fileStorageService.storeBanner(command.getImage(), BannerConstant.BANNER_PREFIX + homeCategory.getId());
            homeCategory.setImage(imagePath);
        }

        HomeCategory saved = homeCategoryRepository.save(homeCategory);
        return HomeBannerMapper.toHomeCategoryResponseDto(saved);
    }
}
