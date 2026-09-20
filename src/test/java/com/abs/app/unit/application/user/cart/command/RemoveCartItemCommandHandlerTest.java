package com.abs.app.unit.application.user.cart.command;

import com.abs.app.application.user.cart.command.RemoveCartItemCommand;
import com.abs.app.application.user.cart.command.RemoveCartItemCommandHandler;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.CartItem;
import com.abs.app.domain.repository.CartItemRepository;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
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
class RemoveCartItemCommandHandlerTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CartCalculatorService cartCalculatorService;

    @InjectMocks
    private RemoveCartItemCommandHandler handler;

    private RemoveCartItemCommand command;
    private CartItem mockCartItem;
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        command = new RemoveCartItemCommand("user123", 1L);

        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setCouponCode(null);

        mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setUserId("user123");
        mockCartItem.setCart(mockCart);
        
        mockCart.getCartItems().add(mockCartItem);
    }

    @Test
    @DisplayName("Xóa sản phẩm khỏi giỏ hàng thất bại: Ném ngoại lệ khi CartItem không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCartItemNotFound() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.CART_ITEM_NOT_FOUND);

        verifyNoInteractions(cartRepository, cartCalculatorService);
    }

    @Test
    @DisplayName("Xóa sản phẩm khỏi giỏ hàng thất bại: Ném ngoại lệ khi CartItem không thuộc về User")
    void shouldThrowResourceNotFoundException_WhenCartItemDoesNotBelongToUser() {
        mockCartItem.setUserId("anotherUser"); // Different user
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CartConstant.CART_ITEM_NOT_BELONG_TO_USER);

        verifyNoInteractions(cartRepository, cartCalculatorService);
    }

    @Test
    @DisplayName("Xóa sản phẩm khỏi giỏ hàng thành công: Xóa item, tính toán lại giỏ hàng và lưu")
    void shouldRemoveCartItemSuccessfully() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        handler.handle(command);

        assertThat(mockCart.getCartItems()).doesNotContain(mockCartItem);

        verify(cartCalculatorService).recalculateCart(mockCart, Optional.empty());
        verify(cartItemRepository).delete(mockCartItem);
        verify(cartRepository).save(mockCart);
    }
}
