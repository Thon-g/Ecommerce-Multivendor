package com.abs.app.application.user.cart.command;

import com.abs.app.application.user.cart.dto.CartItemResponseDto;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.CartCalculatorService;
import com.abs.app.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddToCartCommandHandler {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CartCalculatorService cartCalculatorService;

    @Transactional
    public CartItemResponseDto handle(AddToCartCommand command) {
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductConstant.PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(command.getUserId()).orElseGet(() -> {
            User user = userRepository.findById(command.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalItem(0);
            newCart.setTotalMrpPrice(0);
            newCart.setTotalSellingPrice(0.0);
            newCart.setDiscount(0);
            return cartRepository.save(newCart);
        });

        // Check if item exists in cart
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) && item.getSize().equals(command.getSize()))
                .findFirst();

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + command.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setSize(command.getSize());
            cartItem.setQuantity(command.getQuantity());
            cartItem.setMrpPrice(product.getMrpPrice());
            cartItem.setSellingPrice(product.getSellingPrice());
            cartItem.setUserId(command.getUserId());
            cart.getCartItems().add(cartItem);
        }

        // Recalculate
        cartCalculatorService.recalculateCart(cart, cart.getCouponCode() != null ? couponRepository.findByCode(cart.getCouponCode()) : Optional.empty());
        cartRepository.save(cart);

        return CartMapper.toCartItemResponseDto(cartItem);
    }
}
