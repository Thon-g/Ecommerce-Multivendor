package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.ProductSku;
import com.abs.app.domain.repository.ProductSkuRepository;
import com.abs.app.infrastructure.persistence.jpa.ProductSkuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSkuRepositoryImpl implements ProductSkuRepository {

    private final ProductSkuJpaRepository jpaRepository;

    @Override
    public Optional<ProductSku> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<ProductSku> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLock(id);
    }

    @Override
    public ProductSku save(ProductSku sku) {
        return jpaRepository.save(sku);
    }
}
