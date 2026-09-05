package com.abs.app.application.user.cart.command;

import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.repository.CartItemRepository;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.service.CartCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RemoveCartItemCommandHandler {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final CartCalculatorService cartCalculatorService;

    @Transactional
    public void handle(RemoveCartItemCommand command) {
        CartItem cartItem = cartItemRepository.findById(command.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found"));

        if (!cartItem.getUserId().equals(command.getUserId())) {
            throw new ResourceNotFoundException("CartItem not found for this user");
        }

        Cart cart = cartItem.getCart();
        cart.getCartItems().remove(cartItem);
        
        cartCalculatorService.recalculateCart(cart, cart.getCouponCode() != null ? couponRepository.findByCode(cart.getCouponCode()) : Optional.empty());
        
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
    }
}
