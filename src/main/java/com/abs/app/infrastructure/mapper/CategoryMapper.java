package com.abs.app.infrastructure.mapper;

import com.abs.app.application.admin.category.dto.CategoryResponseDto;
import com.abs.app.domain.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryMapper {
    private CategoryMapper() {}

    public static CategoryResponseDto toCategoryResponseDto(Category category) {
        if (category == null) return null;
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCategoryId(category.getCategoryId());
        dto.setLevel(category.getLevel());
        if (category.getParentCategory() != null) {
            dto.setParentCategoryId(category.getParentCategory().getId());
        }
        return dto;
    }

    public static List<CategoryResponseDto> toCategoryResponseDtoTree(List<Category> allCategories) {
        if (allCategories == null || allCategories.isEmpty()) {
            return new ArrayList<>();
        }

        // Map DTOs
        Map<String, CategoryResponseDto> dtoMap = allCategories.stream()
                .map(CategoryMapper::toCategoryResponseDto)
                .collect(Collectors.toMap(CategoryResponseDto::getId, dto -> dto));

        List<CategoryResponseDto> rootNodes = new ArrayList<>();

        // Build tree
        for (Category category : allCategories) {
            CategoryResponseDto dto = dtoMap.get(category.getId());
            if (category.getParentCategory() == null) {
                rootNodes.add(dto);
            } else {
                CategoryResponseDto parentDto = dtoMap.get(category.getParentCategory().getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                } else {
                    // Fallback if parent is missing in the provided list
                    rootNodes.add(dto);
                }
            }
        }

        return rootNodes;
    }
}
