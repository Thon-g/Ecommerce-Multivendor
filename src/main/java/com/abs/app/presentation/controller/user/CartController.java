package com.abs.app.presentation.controller.user;

import com.abs.app.application.user.cart.command.AddToCartCommand;
import com.abs.app.application.user.cart.command.AddToCartCommandHandler;
import com.abs.app.application.user.cart.command.ApplyCouponToCartCommand;
import com.abs.app.application.user.cart.command.ApplyCouponToCartCommandHandler;
import com.abs.app.application.user.cart.command.RemoveCartItemCommand;
import com.abs.app.application.user.cart.command.RemoveCartItemCommandHandler;
import com.abs.app.application.user.cart.command.RemoveCouponFromCartCommand;
import com.abs.app.application.user.cart.command.RemoveCouponFromCartCommandHandler;
import com.abs.app.application.user.cart.command.UpdateCartItemQuantityCommand;
import com.abs.app.application.user.cart.command.UpdateCartItemQuantityCommandHandler;
import com.abs.app.application.user.cart.dto.AddToCartRequestDto;
import com.abs.app.application.user.cart.dto.ApplyCouponRequestDto;
import com.abs.app.application.user.cart.dto.CartItemResponseDto;
import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.application.user.cart.dto.UpdateCartItemRequestDto;
import com.abs.app.application.user.cart.query.GetCartQuery;
import com.abs.app.application.user.cart.query.GetCartQueryHandler;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final GetCartQueryHandler getCartQueryHandler;
    private final AddToCartCommandHandler addToCartCommandHandler;
    private final UpdateCartItemQuantityCommandHandler updateCartItemQuantityCommandHandler;
    private final RemoveCartItemCommandHandler removeCartItemCommandHandler;
    private final ApplyCouponToCartCommandHandler applyCouponToCartCommandHandler;
    private final RemoveCouponFromCartCommandHandler removeCouponFromCartCommandHandler;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponseDto>> getCart() {
        String userId = SecurityUtils.getCurrentUserId();
        CartResponseDto response = getCartQueryHandler.handle(new GetCartQuery(userId));
        return ResponseEntity.ok(new ApiResponse<>(true, CartConstant.FETCH_CART_SUCCESS, response));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> addItemToCart(@Valid @RequestBody AddToCartRequestDto request) {
        String userId = SecurityUtils.getCurrentUserId();
        CartItemResponseDto response = addToCartCommandHandler.handle(
                new AddToCartCommand(userId, request.getProductId(), request.getSkuId(), request.getQuantity())
        );
        return ResponseEntity.ok(new ApiResponse<>(true, CartConstant.ADD_ITEM_SUCCESS, response));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequestDto request) {
        String userId = SecurityUtils.getCurrentUserId();
        CartItemResponseDto response = updateCartItemQuantityCommandHandler.handle(
                new UpdateCartItemQuantityCommand(userId, cartItemId, request.getQuantity())
        );
        return ResponseEntity.ok(new ApiResponse<>(true, CartConstant.UPDATE_ITEM_SUCCESS, response));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(@PathVariable Long cartItemId) {
        String userId = SecurityUtils.getCurrentUserId();
        removeCartItemCommandHandler.handle(new RemoveCartItemCommand(userId, cartItemId));
        return ResponseEntity.ok(new ApiResponse<>(true, CartConstant.REMOVE_ITEM_SUCCESS, null));
    }

    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponseDto>> applyCoupon(@Valid @RequestBody ApplyCouponRequestDto request) {
        String userId = SecurityUtils.getCurrentUserId();
        CartResponseDto response = applyCouponToCartCommandHandler.handle(
                new ApplyCouponToCartCommand(userId, request.getCouponCode())
        );
        return ResponseEntity.ok(new ApiResponse<>(true, CartConstant.APPLY_COUPON_SUCCESS, response));
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponseDto>> removeCoupon() {
        String userId = SecurityUtils.getCurrentUserId();
        CartResponseDto response = removeCouponFromCartCommandHandler.handle(
                new RemoveCouponFromCartCommand(userId)
        );
        return ResponseEntity.ok(new ApiResponse<>(true, CartConstant.REMOVE_COUPON_SUCCESS, response));
    }
}

