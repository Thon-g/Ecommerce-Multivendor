package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategoryId(String categoryId);
    boolean existsByCategoryId(String categoryId);
    boolean existsByParentCategoryId(String parentId);
    List<Category> findByParentCategoryId(String parentId);
}
