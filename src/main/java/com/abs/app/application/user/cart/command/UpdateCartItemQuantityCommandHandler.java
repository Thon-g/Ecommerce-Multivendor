package com.abs.app.application.user.cart.command;

import com.abs.app.application.user.cart.dto.CartItemResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.repository.CartItemRepository;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.service.CartCalculatorService;
import com.abs.app.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityCommandHandler {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final CartCalculatorService cartCalculatorService;

    @Transactional
    public CartItemResponseDto handle(UpdateCartItemQuantityCommand command) {
        CartItem cartItem = cartItemRepository.findById(command.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException(CartConstant.CART_ITEM_NOT_FOUND));

        if (!cartItem.getUserId().equals(command.getUserId())) {
            throw new ResourceNotFoundException(CartConstant.CART_ITEM_NOT_BELONG_TO_USER);
        }

        cartItem.setQuantity(command.getQuantity());

        Cart cart = cartItem.getCart();
        cartCalculatorService.recalculateCart(cart, cart.getCouponCode() != null ? couponRepository.findByCode(cart.getCouponCode()) : Optional.empty());
        
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        return CartMapper.toCartItemResponseDto(cartItem);
    }
}
