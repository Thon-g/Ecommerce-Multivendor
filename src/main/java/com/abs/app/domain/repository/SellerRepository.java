package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findBySellerId(String sellerId);

    Optional<Seller> findByUserId(String userId);

    boolean existsByUserId(String userId);

    Page<Seller> findByStatus(SellerStatus status, Pageable pageable);

    Page<Seller> search(String keyword, SellerStatus status, Pageable pageable);
}
