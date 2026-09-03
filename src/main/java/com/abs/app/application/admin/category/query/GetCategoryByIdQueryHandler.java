package com.abs.app.application.admin.category.query;

import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCategoryByIdQueryHandler {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryResponseDto handle(GetCategoryByIdQuery query) {
        Category category = categoryRepository.findById(query.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        return CategoryMapper.toCategoryResponseDto(category);
    }
}
