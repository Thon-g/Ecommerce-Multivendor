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
public class CreateHomeCategoryCommandHandler {

    private final HomeCategoryRepository homeCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public HomeCategoryResponseDto handle(CreateHomeCategoryCommand command) {
        if (!categoryRepository.existsByCategoryId(command.getCategoryId())) {
            throw new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND);
        }

        String imagePath = null;
        if (command.getImage() != null && !command.getImage().isEmpty()) {
            imagePath = fileStorageService.storeBanner(command.getImage(), BannerConstant.BANNER_PREFIX + command.getCategoryId());
        }

        HomeCategory homeCategory = new HomeCategory();
        homeCategory.setName(command.getName());
        homeCategory.setImage(imagePath);
        homeCategory.setCategoryId(command.getCategoryId());
        homeCategory.setSection(command.getSection());

        HomeCategory saved = homeCategoryRepository.save(homeCategory);
        return HomeBannerMapper.toHomeCategoryResponseDto(saved);
    }
}
