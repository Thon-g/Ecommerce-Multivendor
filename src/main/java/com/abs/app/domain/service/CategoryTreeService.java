package com.abs.app.domain.service;

import com.abs.app.application.publicapi.category.dto.CategoryTreeResponseDto;
import com.abs.app.domain.entity.Category;
import com.abs.app.infrastructure.persistence.jpa.CategoryJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
public class CategoryTreeService {

    private final CategoryJpaRepository categoryJpaRepository;

    private List<CategoryTreeResponseDto> categoryTree = new ArrayList<>();
    private final Map<String, List<String>> parentToChildrenIds = new HashMap<>();

    @PostConstruct
    public void init() {
        refreshTree();
    }

    public synchronized void refreshTree() {
        List<Category> allCategories = categoryJpaRepository.findAll();
        
        Map<String, List<CategoryTreeResponseDto>> childrenMap = new HashMap<>();
        Map<String, CategoryTreeResponseDto> dtoMap = new HashMap<>();

        for (Category category : allCategories) {
            CategoryTreeResponseDto dto = new CategoryTreeResponseDto(
                    category.getCategoryId(),
                    category.getName(),
                    category.getParentCategory() != null ? category.getParentCategory().getCategoryId() : null,
                    category.getLevel(),
                    new ArrayList<>()
            );
            dtoMap.put(category.getCategoryId(), dto);
        }

        for (CategoryTreeResponseDto dto : dtoMap.values()) {
            if (dto.getParentCategoryId() != null) {
                childrenMap.computeIfAbsent(dto.getParentCategoryId(), k -> new ArrayList<>()).add(dto);
            }
        }

        List<CategoryTreeResponseDto> roots = new ArrayList<>();
        for (CategoryTreeResponseDto dto : dtoMap.values()) {
            List<CategoryTreeResponseDto> children = childrenMap.getOrDefault(dto.getCategoryId(), new ArrayList<>());
            dto.setChildren(children);

            if (dto.getParentCategoryId() == null) {
                roots.add(dto);
            }
        }
        
        this.categoryTree = roots;

        parentToChildrenIds.clear();
        for (Category category : allCategories) {
            List<String> childIds = new ArrayList<>();
            collectAllChildIdsDFS(category.getCategoryId(), childrenMap, childIds);
            parentToChildrenIds.put(category.getCategoryId(), childIds);
        }
    }

    private void collectAllChildIdsDFS(String parentId, Map<String, List<CategoryTreeResponseDto>> childrenMap, List<String> result) {
        result.add(parentId); 
        
        List<CategoryTreeResponseDto> children = childrenMap.get(parentId);
        if (children != null) {
            for (CategoryTreeResponseDto child : children) {
                collectAllChildIdsDFS(child.getCategoryId(), childrenMap, result);
            }
        }
    }

    public List<String> getAllChildCategoryIds(String categoryId) {
        return this.parentToChildrenIds.getOrDefault(categoryId, List.of(categoryId));
    }
}
