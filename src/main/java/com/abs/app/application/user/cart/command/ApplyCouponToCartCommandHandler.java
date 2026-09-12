package com.abs.app.application.user.cart.command;

import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.Coupon;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.CouponStatus;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.CartCalculatorService;
import com.abs.app.infrastructure.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplyCouponToCartCommandHandler {

    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CartCalculatorService cartCalculatorService;

    @Transactional
    public CartResponseDto handle(ApplyCouponToCartCommand command) {
        Cart cart = cartRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(CartConstant.CART_NOT_FOUND));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException(CartConstant.CART_EMPTY);
        }

        Coupon coupon = couponRepository.findByCode(command.getCouponCode())
                .orElseThrow(() -> new ResourceNotFoundException(CartConstant.COUPON_NOT_FOUND));

        if (coupon.getStatus() != CouponStatus.ACTIVE) {
            throw new IllegalStateException(CartConstant.COUPON_EXPIRED);
        }

        LocalDate now = LocalDate.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            throw new IllegalStateException(CartConstant.COUPON_NOT_IN_VALID_PERIOD);
        }

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        if (user.getUsedCoupons().contains(coupon)) {
            throw new IllegalStateException(CartConstant.COUPON_ALREADY_USED);
        }

        cartCalculatorService.recalculateCart(cart, Optional.of(coupon));
        cartRepository.save(cart);

        return CartMapper.toCartResponseDto(cart);
    }
}
