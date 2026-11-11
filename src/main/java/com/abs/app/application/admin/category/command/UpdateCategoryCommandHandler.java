package com.abs.app.application.admin.category.command;

import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.mapper.CategoryMapper;
import com.abs.app.domain.service.CategoryTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCategoryCommandHandler {

    private final CategoryRepository categoryRepository;
    private final CategoryTreeService categoryTreeService;

    @Transactional
    public CategoryResponseDto handle(UpdateCategoryCommand command) {
        Category category = categoryRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        if (command.getCommissionRate() != null && (command.getCommissionRate() < 0.0 || command.getCommissionRate() > 100.0)) {
            throw new BusinessException(CategoryConstant.INVALID_COMMISSION_RATE);
        }

        category.setName(command.getName());
        if (command.getCommissionRate() != null) {
            category.setCommissionRate(command.getCommissionRate());
        }
        
        Category savedCategory = categoryRepository.save(category);
        categoryTreeService.refreshTree();
        return CategoryMapper.toCategoryResponseDto(savedCategory);
    }
}
