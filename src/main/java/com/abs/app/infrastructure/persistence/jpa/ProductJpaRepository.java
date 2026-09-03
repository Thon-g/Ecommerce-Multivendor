package com.abs.app.infrastructure.persistence.jpa;

import com.abs.app.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, String> {
    List<Product> findBySeller_SellerId(String sellerId);
}
