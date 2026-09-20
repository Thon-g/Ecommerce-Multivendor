package com.abs.app.application.user.wishlist.query;

import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.repository.WishlistRepository;
import com.abs.app.infrastructure.mapper.WishlistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetWishlistQueryHandler {
    private final WishlistRepository wishlistRepository;

    @Transactional(readOnly = true)
    public WishlistResponseDto handle(GetWishlistQuery query) {
        Wishlist wishlist = wishlistRepository.findByUserId(query.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(WishlistConstant.WISHLIST_NOT_FOUND));
        return WishlistMapper.toWishlistResponseDto(wishlist);
    }
}
