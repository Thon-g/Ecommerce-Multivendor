package com.abs.app.application.admin.category.query;

import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCategoriesQueryHandler {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> handle(GetAllCategoriesQuery query) {
        List<Category> allCategories = categoryRepository.findAll();
        return CategoryMapper.toCategoryResponseDtoTree(allCategories);
    }
}
