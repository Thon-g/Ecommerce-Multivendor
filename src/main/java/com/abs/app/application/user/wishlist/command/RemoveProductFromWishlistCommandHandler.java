package com.abs.app.application.user.wishlist.command;

import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.WishlistRepository;
import com.abs.app.domain.service.WishlistService;
import com.abs.app.infrastructure.mapper.WishlistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveProductFromWishlistCommandHandler {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistService wishlistService;

    @Transactional
    public WishlistResponseDto handle(RemoveProductFromWishlistCommand command) {
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstant.PRODUCT_NOT_FOUND));

        Wishlist wishlist = wishlistService.getWishlist(command.getUserId());

        if (!wishlist.getProducts().contains(product)) {
            throw new IllegalStateException(WishlistConstant.PRODUCT_NOT_IN_WISHLIST);
        }

        wishlist.getProducts().remove(product);
        wishlistRepository.save(wishlist);

        return WishlistMapper.toWishlistResponseDto(wishlist);
    }
}
