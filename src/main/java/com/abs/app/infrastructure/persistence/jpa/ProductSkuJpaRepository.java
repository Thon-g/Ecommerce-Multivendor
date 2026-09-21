package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.ProductSku;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductSkuJpaRepository extends JpaRepository<ProductSku, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ProductSku s WHERE s.id = :id")
    Optional<ProductSku> findByIdWithLock(@Param("id") Long id);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE ProductSku s SET s.quantity = s.quantity - :qty WHERE s.id = :id AND s.quantity >= :qty")
    int deductStock(@Param("id") Long id, @Param("qty") int qty);
}
