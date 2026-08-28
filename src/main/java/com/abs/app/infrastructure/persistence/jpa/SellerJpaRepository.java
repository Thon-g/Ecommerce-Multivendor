package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerJpaRepository extends JpaRepository<Seller, String> {

    Optional<Seller> findByUserUserId(String userId);

    boolean existsByUserUserId(String userId);

    List<Seller> findByStatus(SellerStatus status);
}
