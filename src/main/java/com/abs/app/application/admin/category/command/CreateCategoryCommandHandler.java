package com.abs.app.application.admin.category.command;

import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.util.GenerateIdUtil;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCategoryCommandHandler {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto handle(CreateCategoryCommand command) {
        if (categoryRepository.existsByCategoryId(command.getCategoryId())) {
            throw new BusinessException(CategoryConstant.CATEGORY_ID_ALREADY_EXISTS);
        }

        Category category = new Category();
        category.setId(GenerateIdUtil.GenerateId());
        category.setName(command.getName());
        category.setCategoryId(command.getCategoryId());
        category.setLevel(command.getLevel());

        if (command.getParentCategoryId() != null && !command.getParentCategoryId().isBlank()) {
            Category parent = categoryRepository.findById(command.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));
            category.setParentCategory(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryResponseDto(savedCategory);
    }
}
