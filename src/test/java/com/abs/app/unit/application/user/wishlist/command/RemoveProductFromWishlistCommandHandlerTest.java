package com.abs.app.unit.application.user.wishlist.command;

import com.abs.app.application.user.wishlist.command.RemoveProductFromWishlistCommand;
import com.abs.app.application.user.wishlist.command.RemoveProductFromWishlistCommandHandler;
import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Wishlist;
import com.abs.app.domain.repository.ProductRepository;
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
class RemoveProductFromWishlistCommandHandlerTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private RemoveProductFromWishlistCommandHandler handler;

    private RemoveProductFromWishlistCommand command;
    private Product mockProduct;
    private Wishlist mockWishlist;

    @BeforeEach
    void setUp() {
        command = new RemoveProductFromWishlistCommand("user123", "prod1");

        mockProduct = new Product();
        mockProduct.setId("prod1");

        mockWishlist = new Wishlist();
        mockWishlist.setId(1L);
        com.abs.app.domain.entity.User wlUser = new com.abs.app.domain.entity.User();
        wlUser.setUserId("user123");
        mockWishlist.setUser(wlUser);
    }

    @Test
    @DisplayName("Xóa SP thất bại: Ném ngoại lệ khi Sản phẩm không tồn tại")
    void shouldThrowResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.findById("prod1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(ProductConstant.PRODUCT_NOT_FOUND);

        verifyNoInteractions(wishlistRepository);
    }

    @Test
    @DisplayName("Xóa SP thất bại: Ném ngoại lệ khi Không tìm thấy Wishlist của User")
    void shouldThrowResourceNotFoundException_WhenWishlistNotFound() {
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(wishlistRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(WishlistConstant.WISHLIST_NOT_FOUND);
    }

    @Test
    @DisplayName("Xóa SP thất bại: Ném ngoại lệ khi Sản phẩm không nằm trong Wishlist")
    void shouldThrowIllegalStateException_WhenProductNotInWishlist() {
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(wishlistRepository.findByUserId("user123")).thenReturn(Optional.of(mockWishlist));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(WishlistConstant.PRODUCT_NOT_IN_WISHLIST);

        verify(wishlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Xóa SP thành công: Xóa Sản phẩm khỏi Wishlist và lưu DB")
    void shouldRemoveProductSuccessfully() {
        mockWishlist.getProducts().add(mockProduct);
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(wishlistRepository.findByUserId("user123")).thenReturn(Optional.of(mockWishlist));

        WishlistResponseDto response = handler.handle(command);

        assertThat(mockWishlist.getProducts()).doesNotContain(mockProduct);
        assertThat(response).isNotNull();

        verify(wishlistRepository).save(mockWishlist);
    }
}
