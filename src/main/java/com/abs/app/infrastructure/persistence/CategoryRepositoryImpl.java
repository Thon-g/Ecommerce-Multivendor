package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.Category;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.infrastructure.persistence.jpa.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Category save(Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public Optional<Category> findById(String id) {
        return categoryJpaRepository.findById(id);
    }

    @Override
    public Optional<Category> findByCategoryId(String categoryId) {
        return categoryJpaRepository.findByCategoryId(categoryId);
    }

    @Override
    public Page<Category> search(String keyword, Pageable pageable) {
        return categoryJpaRepository.search(keyword, pageable);
    }

    @Override
    public Page<Category> findByParentCategoryId(String parentId, Pageable pageable) {
        return categoryJpaRepository.findByParentCategoryId(parentId, pageable);
    }

    @Override
    public void delete(Category category) {
        categoryJpaRepository.delete(category);
    }

    @Override
    public boolean existsByCategoryId(String categoryId) {
        return categoryJpaRepository.existsByCategoryId(categoryId);
    }

    @Override
    public boolean existsByParentCategoryId(String parentId) {
        return categoryJpaRepository.existsByParentCategoryId(parentId);
    }
}
