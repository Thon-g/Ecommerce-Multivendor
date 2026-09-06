package com.abs.app.domain.service;

import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;

    public Wishlist getWishlist(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(WishlistConstant.WISHLIST_NOT_FOUND));
    }
}
