package com.abs.app.unit.application.user.cart.query;

import com.abs.app.application.user.cart.dto.CartResponseDto;
import com.abs.app.application.user.cart.query.GetCartQuery;
import com.abs.app.application.user.cart.query.GetCartQueryHandler;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Cart;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCartQueryHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetCartQueryHandler handler;

    private GetCartQuery query;
    private Cart mockCart;
    private User mockUser;

    @BeforeEach
    void setUp() {
        query = new GetCartQuery("user123");

        mockUser = new User();
        mockUser.setUserId("user123");

        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setUser(mockUser);
        mockCart.setTotalItem(2);
        mockCart.setTotalMrpPrice(100);
        mockCart.setTotalSellingPrice(90.0);
        mockCart.setDiscount(10);
    }

    @Test
    @DisplayName("Lấy giỏ hàng thành công: Trả về giỏ hàng hiện có")
    void shouldReturnExistingCart_WhenCartExists() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.of(mockCart));

        CartResponseDto response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTotalItem()).isEqualTo(2);
        
        verifyNoInteractions(userRepository);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lấy giỏ hàng thất bại: Ném ngoại lệ khi Cart chưa có và User cũng không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCartNotFoundAndUserNotFound() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);

        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lấy giỏ hàng thành công: Tạo giỏ hàng mới tinh nếu chưa có (User tồn tại)")
    void shouldCreateAndReturnNewCart_WhenCartNotFoundButUserExists() {
        when(cartRepository.findByUserId("user123")).thenReturn(Optional.empty());
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        
        Cart newCart = new Cart();
        newCart.setId(2L);
        newCart.setTotalItem(0);
        
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        CartResponseDto response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTotalItem()).isEqualTo(0);

        verify(cartRepository).save(any(Cart.class));
    }
}
