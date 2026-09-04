package com.abs.app.infrastructure.persistence;

import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.persistence.jpa.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
    public Page<Product> search(String keyword, String categoryId, String sellerId, Pageable pageable) {
        return productJpaRepository.search(keyword, categoryId, sellerId, pageable);
    }

    @Override
    public Page<Product> findByCategoryId(String categoryId, Pageable pageable) {
        return productJpaRepository.findByCategory_CategoryId(categoryId, pageable);
    }

    @Override
    public Page<Product> findBySellerId(String sellerId, Pageable pageable) {
        return productJpaRepository.findBySeller_SellerId(sellerId, pageable);
    }

    @Override
    public void delete(Product product) {
        productJpaRepository.delete(product);
    }
}
