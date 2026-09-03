package com.abs.app.domain.repository;

import com.abs.app.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    List<Product> findBySellerId(String sellerId);
    void delete(Product product);
}
