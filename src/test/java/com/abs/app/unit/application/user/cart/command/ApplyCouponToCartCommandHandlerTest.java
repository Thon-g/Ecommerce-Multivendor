package com.abs.app.unit.application.user.cart.command;

import com.abs.app.application.user.cart.command.ApplyCouponToCartCommand;
import com.abs.app.application.user.cart.command.ApplyCouponToCartCommandHandler;
import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.entity.Coupon;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.CouponStatus;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.CartCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplyCouponToCartCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartCalculatorService cartCalculatorService;

    @InjectMocks
    private ApplyCouponToCartCommandHandler handler;

    private ApplyCouponToCartCommand command;
    private Cart mockCart;
    private Coupon mockCoupon;
    private User mockUser;

    @BeforeEach
    void setUp() {
        command = new ApplyCouponToCartCommand("user123", "DISCOUNT10");

        mockCart = new Cart();
        mockCart.setId(1L);

        mockCoupon = new Coupon();
        mockCoupon.setId(1L);
        mockCoupon.setCode("DISCOUNT10");
        mockCoupon.setStatus(CouponStatus.ACTIVE);
        mockCoupon.setStartDate(LocalDate.now().minusDays(1));
        mockCoupon.setEndDate(LocalDate.now().plusDays(1));

        mockUser = new User();
        mockUser.setUserId("user123");
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi Giỏ hàng không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCartNotFound() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.CART_NOT_FOUND);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi Giỏ hàng trống")
    void shouldThrowIllegalStateException_WhenCartIsEmpty() {
        // mockCart.getCartItems() is empty by default
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CartConstant.CART_EMPTY);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi Mã giảm giá không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCouponNotFound() {
        mockCart.getCartItems().add(new CartItem());
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.COUPON_NOT_FOUND);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi Mã giảm giá không ACTIVE")
    void shouldThrowIllegalStateException_WhenCouponNotActive() {
        mockCart.getCartItems().add(new CartItem());
        mockCoupon.setStatus(CouponStatus.INACTIVE);
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.of(mockCoupon));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CartConstant.COUPON_EXPIRED);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi Ngoài thời gian sử dụng")
    void shouldThrowIllegalStateException_WhenCouponNotInValidPeriod() {
        mockCart.getCartItems().add(new CartItem());
        mockCoupon.setStartDate(LocalDate.now().plusDays(1)); // Start date in future
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.of(mockCoupon));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CartConstant.COUPON_NOT_IN_VALID_PERIOD);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi Không tìm thấy User")
    void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        mockCart.getCartItems().add(new CartItem());
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.of(mockCoupon));
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thất bại: Ném ngoại lệ khi User đã sử dụng mã này")
    void shouldThrowIllegalStateException_WhenUserAlreadyUsedCoupon() {
        mockCart.getCartItems().add(new CartItem());
        mockUser.getUsedCoupons().add(mockCoupon); // User already used this coupon
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.of(mockCoupon));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CartConstant.COUPON_ALREADY_USED);
    }

    @Test
    @DisplayName("Áp dụng mã giảm giá thành công: Tính toán lại giỏ hàng và lưu DB")
    void shouldApplyCouponSuccessfully() {
        mockCart.getCartItems().add(new CartItem());
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));
        when(couponRepository.findByCode("DISCOUNT10")).thenReturn(Optional.of(mockCoupon));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));

        CartResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        verify(cartCalculatorService).recalculateCart(mockCart, Optional.of(mockCoupon));
        verify(cartRepository).save(mockCart);
    }
}
