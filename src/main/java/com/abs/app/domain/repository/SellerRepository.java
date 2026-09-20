package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findBySellerId(String sellerId);

    /** Tìm gian hàng theo userId của User liên kết */
    Optional<Seller> findByUserId(String userId);

    /** Kiểm tra User đã có gian hàng chưa (tránh đăng ký trùng) */
    boolean existsByUserId(String userId);

    /** Ly danh sAch seller theo trng thAi (dA1ng cho Admin) */
    Page<Seller> findByStatus(SellerStatus status, Pageable pageable);

    /** Tm kim seller */
    Page<Seller> search(String keyword, SellerStatus status, Pageable pageable);
}
