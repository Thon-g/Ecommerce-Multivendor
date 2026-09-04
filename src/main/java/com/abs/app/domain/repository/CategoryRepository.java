package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    Optional<Category> findByCategoryId(String categoryId);
    Page<Category> search(String keyword, Pageable pageable);
    Page<Category> findByParentCategoryId(String parentId, Pageable pageable);
    void delete(Category category);
    boolean existsByCategoryId(String categoryId);
    boolean existsByParentCategoryId(String parentId);
}
