package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.persistence.jpa.SellerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepository {

    private final SellerJpaRepository sellerJpaRepository;

    @Override
    public Seller save(Seller seller) {
        return sellerJpaRepository.save(seller);
    }

    @Override
    public Optional<Seller> findBySellerId(String sellerId) {
        return sellerJpaRepository.findById(sellerId);
    }

    @Override
    public Optional<Seller> findByUserId(String userId) {
        return sellerJpaRepository.findByUserUserId(userId);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return sellerJpaRepository.existsByUserUserId(userId);
    }

    @Override
    public Page<Seller> findByStatus(SellerStatus status, Pageable pageable) {
        return sellerJpaRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Seller> search(String keyword, SellerStatus status, Pageable pageable) {
        return sellerJpaRepository.search(keyword, status, pageable);
    }
}
