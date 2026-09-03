package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    Optional<Category> findByCategoryId(String categoryId);
    List<Category> findAll();
    List<Category> findByParentCategoryId(String parentId);
    void delete(Category category);
    boolean existsByCategoryId(String categoryId);
    boolean existsByParentCategoryId(String parentId);
}
