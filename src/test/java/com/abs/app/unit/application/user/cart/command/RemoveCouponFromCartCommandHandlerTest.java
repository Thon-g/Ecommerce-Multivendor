package com.abs.app.unit.application.user.cart.command;

import com.abs.app.application.user.cart.command.RemoveCouponFromCartCommand;
import com.abs.app.application.user.cart.command.RemoveCouponFromCartCommandHandler;
import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.service.CartCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveCouponFromCartCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartCalculatorService cartCalculatorService;

    @InjectMocks
    private RemoveCouponFromCartCommandHandler handler;

    private RemoveCouponFromCartCommand command;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        command = new RemoveCouponFromCartCommand("user123");

        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setCouponCode("DISCOUNT10");
    }

    @Test
    @DisplayName("Gỡ mã giảm giá thất bại: Ném ngoại lệ khi Giỏ hàng không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCartNotFound() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.CART_NOT_FOUND);

        verifyNoInteractions(cartCalculatorService);
    }

    @Test
    @DisplayName("Gỡ mã giảm giá thành công: Xóa couponCode và tính lại giỏ hàng")
    void shouldRemoveCouponSuccessfully() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));

        CartResponseDto response = handler.handle(command);

        assertThat(mockCart.getCouponCode()).isNull();
        assertThat(response).isNotNull();

        verify(cartCalculatorService).recalculateCart(mockCart, Optional.empty());
        verify(cartRepository).save(mockCart);
    }
}
