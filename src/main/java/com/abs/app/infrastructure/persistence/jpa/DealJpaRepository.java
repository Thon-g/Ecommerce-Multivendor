package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DealJpaRepository extends JpaRepository<Deal, Long> {
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Deal d WHERE d.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT d FROM Deal d WHERE d.category.id = :categoryId")
    Optional<Deal> findByCategoryId(@Param("categoryId") Long categoryId);
}

