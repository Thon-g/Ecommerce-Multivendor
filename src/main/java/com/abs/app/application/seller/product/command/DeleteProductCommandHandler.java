package com.abs.app.application.seller.product.command;

import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteProductCommandHandler {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    @Transactional
    public void handle(DeleteProductCommand command) {
        Product product = productRepository.findById(command.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstant.PRODUCT_NOT_FOUND));

        Seller seller = sellerRepository.findByUserId(command.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(SellerConstant.SELLER_NOT_FOUND));

        if (!product.getSeller().getSellerId().equals(seller.getSellerId())) {
            throw new BusinessException(ProductConstant.PRODUCT_FORBIDDEN);
        }

        productRepository.delete(product);
    }
}
