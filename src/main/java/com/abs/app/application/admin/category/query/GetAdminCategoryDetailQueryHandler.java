package com.abs.app.application.admin.category.query;

import com.abs.app.application.publicapi.category.dto.CategoryResponseDto;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAdminCategoryDetailQueryHandler {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryResponseDto handle(GetAdminCategoryDetailQuery query) {
        Category category = categoryRepository.findById(query.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CategoryConstant.CATEGORY_NOT_FOUND));

        CategoryResponseDto response = CategoryMapper.toCategoryResponseDto(category);

        Page<Category> children = categoryRepository.findByParentCategoryId(category.getId(), Pageable.unpaged());
        List<CategoryResponseDto> childDtos = children.getContent().stream()
                .map(CategoryMapper::toCategoryResponseDto)
                .toList();

        response.setChildren(childDtos);
        return response;
    }
}
