package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    Page<Product> search(String keyword, String categoryId, String sellerId, Pageable pageable);
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);
    Page<Product> findBySellerId(String sellerId, Pageable pageable);
    void delete(Product product);
}
