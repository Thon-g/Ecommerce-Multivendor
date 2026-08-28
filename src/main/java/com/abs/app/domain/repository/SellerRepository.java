package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;

import java.util.List;
import java.util.Optional;

public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findBySellerId(String sellerId);

    /** Tìm gian hàng theo userId của User liên kết */
    Optional<Seller> findByUserId(String userId);

    /** Kiểm tra User đã có gian hàng chưa (tránh đăng ký trùng) */
    boolean existsByUserId(String userId);

    /** Lấy danh sách seller theo trạng thái (dùng cho Admin) */
    List<Seller> findByStatus(SellerStatus status);

    /** Lấy tất cả sellers (Admin dashboard) */
    List<Seller> findAll();
}
