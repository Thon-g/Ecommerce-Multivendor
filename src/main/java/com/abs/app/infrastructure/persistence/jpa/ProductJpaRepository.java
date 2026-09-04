package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, String> {
    Page<Product> findBySeller_SellerId(String sellerId, Pageable pageable);
    Page<Product> findByCategory_CategoryId(String categoryId, Pageable pageable);

    @Query("""
        SELECT p FROM Product p 
        WHERE (:keyword IS NULL OR :keyword = '' 
            OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:categoryId IS NULL OR :categoryId = '' OR p.category.categoryId = :categoryId)
        AND (:sellerId IS NULL OR :sellerId = '' OR p.seller.sellerId = :sellerId)
    """)
    Page<Product> search(
            @Param("keyword") String keyword, 
            @Param("categoryId") String categoryId, 
            @Param("sellerId") String sellerId,
            Pageable pageable);
}
