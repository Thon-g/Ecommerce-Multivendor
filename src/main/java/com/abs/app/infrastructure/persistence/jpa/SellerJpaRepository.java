package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SellerJpaRepository extends JpaRepository<Seller, String> {

    Optional<Seller> findByUserUserId(String userId);

    boolean existsByUserUserId(String userId);

    Page<Seller> findByStatus(SellerStatus status, Pageable pageable);

    @Query("""
        SELECT s FROM Seller s 
        WHERE (:keyword IS NULL OR :keyword = '' 
            OR LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(s.businessDetails.businessName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:status IS NULL OR s.status = :status)
    """)
    Page<Seller> search(
            @Param("keyword") String keyword, 
            @Param("status") SellerStatus status, 
            Pageable pageable);
}
