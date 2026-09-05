package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.Coupon;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.infrastructure.persistence.jpa.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findByCode(String code) {
        return couponJpaRepository.findByCode(code);
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public void delete(Coupon coupon) {
        couponJpaRepository.delete(coupon);
    }
}
