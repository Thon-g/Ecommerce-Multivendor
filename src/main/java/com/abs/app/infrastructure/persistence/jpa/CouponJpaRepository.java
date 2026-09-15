package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
}
