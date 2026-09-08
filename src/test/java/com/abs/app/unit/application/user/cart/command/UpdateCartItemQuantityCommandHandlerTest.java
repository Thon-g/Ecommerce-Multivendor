package com.abs.app.unit.application.user.cart.command;

import com.abs.app.application.user.cart.command.UpdateCartItemQuantityCommand;
import com.abs.app.application.user.cart.command.UpdateCartItemQuantityCommandHandler;
import com.abs.app.application.user.cart.dto.CartItemResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.repository.CartItemRepository;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.service.CartCalculatorService;
import com.abs.app.domain.service.CartValidationService;
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
class UpdateCartItemQuantityCommandHandlerTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CartCalculatorService cartCalculatorService;

    @Mock
    private CartValidationService cartValidationService;

    @InjectMocks
    private UpdateCartItemQuantityCommandHandler handler;

    private UpdateCartItemQuantityCommand command;
    private CartItem mockCartItem;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        command = new UpdateCartItemQuantityCommand("user123", 1L, 5);

        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setCouponCode(null);

        mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setUserId("user123");
        mockCartItem.setQuantity(2);
        mockCartItem.setCart(mockCart);
    }

    @Test
    @DisplayName("Cập nhật số lượng thất bại: Ném ngoại lệ khi CartItem không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCartItemNotFound() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.CART_ITEM_NOT_FOUND);

        verifyNoInteractions(cartRepository, cartCalculatorService);
    }

    @Test
    @DisplayName("Cập nhật số lượng thất bại: Ném ngoại lệ khi CartItem không thuộc về User")
    void shouldThrowResourceNotFoundException_WhenCartItemDoesNotBelongToUser() {
        mockCartItem.setUserId("anotherUser"); // Different user
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.CART_ITEM_NOT_BELONG_TO_USER);

        verifyNoInteractions(cartRepository, cartCalculatorService);
    }

    @Test
    @DisplayName("Cập nhật số lượng thành công: Thay đổi số lượng, tính toán lại giỏ hàng và lưu")
    void shouldUpdateCartItemQuantitySuccessfully() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));
        doNothing().when(cartValidationService).validateStock(any(), anyInt());

        CartItemResponseDto response = handler.handle(command);

        assertThat(mockCartItem.getQuantity()).isEqualTo(5);
        assertThat(response).isNotNull();
        assertThat(response.getQuantity()).isEqualTo(5);

        verify(cartCalculatorService).recalculateCart(mockCart, Optional.empty());
        verify(cartItemRepository).save(mockCartItem);
        verify(cartRepository).save(mockCart);
    }
}
