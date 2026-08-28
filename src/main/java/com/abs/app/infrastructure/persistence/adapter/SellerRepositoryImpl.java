package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.AccountStatus;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.persistence.jpa.SellerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Seller> findBySellerStatus(AccountStatus status) {
        return sellerJpaRepository.findBySellerStatus(status);
    }

    @Override
    public List<Seller> findAll() {
        return sellerJpaRepository.findAll();
    }
}
