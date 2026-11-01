package com.abs.app.domain.service;

import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.constant.OrderConstant;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductSku;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CartValidationService {

    public void validateStock(ProductSku sku, int requestedQuantity) {
        if (sku != null && requestedQuantity > sku.getQuantity()) {
            throw new IllegalStateException(CartConstant.OUT_OF_STOCK);
        }
    }

    public void validateSellerActiveForProduct(Product product) {
        if (product == null || product.getSeller() == null || product.getSeller().getStatus() != SellerStatus.ACTIVE) {
            throw new BusinessException(CartConstant.SELLER_NOT_ACTIVE);
        }
    }

    public void validateCartItemsForCheckout(Collection<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return;
        }

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            Seller seller = product != null ? product.getSeller() : null;

            if (seller == null || seller.getStatus() != SellerStatus.ACTIVE) {
                String productTitle = product != null ? product.getTitle() : ProductConstant.DEFAULT_PRODUCT_NAME;
                String shopName = seller != null ? seller.getShopName() : "";
                throw new BusinessException(String.format(OrderConstant.SELLER_NOT_ACTIVE, productTitle, shopName));
            }
        }
    }
}
