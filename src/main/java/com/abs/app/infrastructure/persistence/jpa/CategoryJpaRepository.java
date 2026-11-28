package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategoryId(String categoryId);
    boolean existsByCategoryId(String categoryId);
    boolean existsByParentCategoryId(String parentId);
    Page<Category> findByParentCategoryId(String parentId, Pageable pageable);

    @Query("""
            SELECT c FROM Category c 
            WHERE (:keyword IS NULL OR :keyword = '' 
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Category> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT c FROM Category c 
            WHERE (:keyword IS NULL OR :keyword = '' 
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.categoryId) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:level IS NULL OR c.level = :level)
            """)
    Page<Category> search(@Param("keyword") String keyword, @Param("level") Integer level, Pageable pageable);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory")
    java.util.List<Category> findAllWithParent();

    @Query("SELECT c FROM Category c WHERE c.id IN :ids")
    java.util.List<Category> findAllByIdIn(@Param("ids") java.util.List<String> ids);

    @Query("SELECT c FROM Category c WHERE c.categoryId IN :categoryIds")
    java.util.List<Category> findAllByCategoryIdIn(@Param("categoryIds") java.util.List<String> categoryIds);
}
