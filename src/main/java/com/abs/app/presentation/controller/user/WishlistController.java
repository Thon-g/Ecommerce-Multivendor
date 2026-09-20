package com.abs.app.presentation.controller.user;

import com.abs.app.application.user.wishlist.command.AddProductToWishlistCommand;
import com.abs.app.application.user.wishlist.command.AddProductToWishlistCommandHandler;
import com.abs.app.application.user.wishlist.command.RemoveProductFromWishlistCommand;
import com.abs.app.application.user.wishlist.command.RemoveProductFromWishlistCommandHandler;
import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.application.user.wishlist.query.GetWishlistQuery;
import com.abs.app.application.user.wishlist.query.GetWishlistQueryHandler;
import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/wishlist")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class WishlistController {

    private final GetWishlistQueryHandler getWishlistQueryHandler;
    private final AddProductToWishlistCommandHandler addProductToWishlistCommandHandler;
    private final RemoveProductFromWishlistCommandHandler removeProductFromWishlistCommandHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistResponseDto>> getWishlist() {
        String userId = SecurityUtils.getCurrentUserId();
        WishlistResponseDto response = getWishlistQueryHandler.handle(new GetWishlistQuery(userId));
        return ResponseEntity.ok(new ApiResponse<>(true, WishlistConstant.FETCH_WISHLIST_SUCCESS, response));
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponseDto>> addProductToWishlist(@PathVariable String productId) {
        String userId = SecurityUtils.getCurrentUserId();
        WishlistResponseDto response = addProductToWishlistCommandHandler.handle(
                new AddProductToWishlistCommand(userId, productId)
        );
        return ResponseEntity.ok(new ApiResponse<>(true, WishlistConstant.ADD_PRODUCT_SUCCESS, response));
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<WishlistResponseDto>> removeProductFromWishlist(@PathVariable String productId) {
        String userId = SecurityUtils.getCurrentUserId();
        WishlistResponseDto response = removeProductFromWishlistCommandHandler.handle(
                new RemoveProductFromWishlistCommand(userId, productId)
        );
        return ResponseEntity.ok(new ApiResponse<>(true, WishlistConstant.REMOVE_PRODUCT_SUCCESS, response));
    }
}
