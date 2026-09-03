package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.persistence.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public List<Product> findBySellerId(String sellerId) {
        return productJpaRepository.findBySeller_SellerId(sellerId);
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }
}
