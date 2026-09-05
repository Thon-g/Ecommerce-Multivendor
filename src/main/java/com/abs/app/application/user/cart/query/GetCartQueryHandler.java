package com.abs.app.application.user.cart.query;

import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetCartQueryHandler {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponseDto handle(GetCartQuery query) {
        Cart cart = cartRepository.findByUserId(query.getUserId()).orElseGet(() -> {
            User user = userRepository.findById(query.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalItem(0);
            newCart.setTotalMrpPrice(0);
            newCart.setTotalSellingPrice(0.0);
            newCart.setDiscount(0);
            return cartRepository.save(newCart);
        });
        return CartMapper.toCartResponseDto(cart);
    }
}
