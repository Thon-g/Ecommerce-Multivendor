package com.abs.app.application.user.cart.command;

import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.service.CartCalculatorService;
import com.abs.app.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RemoveCouponFromCartCommandHandler {

    private final CartRepository cartRepository;
    private final CartCalculatorService cartCalculatorService;

    @Transactional
    public CartResponseDto handle(RemoveCouponFromCartCommand command) {
        Cart cart = cartRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(CartConstant.CART_NOT_FOUND));

        cart.setCouponCode(null);
        cartCalculatorService.recalculateCart(cart, Optional.empty());
        cartRepository.save(cart);

        return CartMapper.toCartResponseDto(cart);
    }
}
