package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findById(Long id);
    Coupon save(Coupon coupon);
    void delete(Coupon coupon);
}
