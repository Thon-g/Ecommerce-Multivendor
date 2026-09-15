package com.abs.app.unit.application.user.wishlist.query;

import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.application.user.wishlist.query.GetWishlistQuery;
import com.abs.app.application.user.wishlist.query.GetWishlistQueryHandler;
import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.repository.WishlistRepository;
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
class GetWishlistQueryHandlerTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private GetWishlistQueryHandler handler;

    private GetWishlistQuery query;
    private Wishlist mockWishlist;

    @BeforeEach
    void setUp() {
        query = new GetWishlistQuery("user123");

        mockWishlist = new Wishlist();
        mockWishlist.setId(1L);
        com.abs.app.domain.entity.User wlUser = new com.abs.app.domain.entity.User();
        wlUser.setUserId("user123");
        mockWishlist.setUser(wlUser);
        
        Product p = new Product();
        p.setId("prod1");
        mockWishlist.getProducts().add(p);
    }

    @Test
    @DisplayName("Lấy danh sách yêu thích thất bại: Ném ngoại lệ khi Wishlist không tồn tại")
    void shouldThrowResourceNotFoundException_WhenWishlistNotFound() {
        when(wishlistRepository.findByUserId(query.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(WishlistConstant.WISHLIST_NOT_FOUND);
    }

    @Test
    @DisplayName("Lấy danh sách yêu thích thành công: Trả về WishlistResponseDto chứa thông tin")
    void shouldReturnWishlistResponseDto_WhenWishlistExists() {
        when(wishlistRepository.findByUserId(query.getUserId())).thenReturn(Optional.of(mockWishlist));

        WishlistResponseDto response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        // Ensure static mapper was called successfully
        assertThat(response.getProducts()).isNotNull();

        verify(wishlistRepository).findByUserId("user123");
    }
}
