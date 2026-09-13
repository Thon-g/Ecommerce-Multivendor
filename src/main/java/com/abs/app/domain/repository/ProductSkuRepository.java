package com.abs.app.domain.repository;

import com.abs.app.domain.entity.ProductSku;
import java.util.Optional;

public interface ProductSkuRepository {
    Optional<ProductSku> findById(Long id);
    Optional<ProductSku> findByIdWithLock(Long id);
    ProductSku save(ProductSku sku);
}
