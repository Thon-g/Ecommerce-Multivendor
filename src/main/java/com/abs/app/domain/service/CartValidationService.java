package com.abs.app.domain.service;

import com.abs.app.common.constant.CartConstant;
import com.abs.app.domain.entity.ProductSku;
import org.springframework.stereotype.Service;

@Service
public class CartValidationService {

    public void validateStock(ProductSku sku, int requestedQuantity) {
        if (sku != null && requestedQuantity > sku.getQuantity()) {
            throw new IllegalStateException(CartConstant.OUT_OF_STOCK);
        }
    }
}
