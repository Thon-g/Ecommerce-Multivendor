package com.abs.app.unit.application.user.wishlist.command;

import com.abs.app.application.user.wishlist.command.AddProductToWishlistCommand;
import com.abs.app.application.user.wishlist.command.AddProductToWishlistCommandHandler;
import com.abs.app.application.user.wishlist.dto.WishlistResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.WishlistConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.User;
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
class AddProductToWishlistCommandHandlerTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AddProductToWishlistCommandHandler handler;

    private AddProductToWishlistCommand command;
    private Product mockProduct;
    private Wishlist mockWishlist;
    private Seller seller;
    private User sellerUser;

    @BeforeEach
    void setUp() {
        command = new AddProductToWishlistCommand("user123", "prod1");

        sellerUser = new User();
        sellerUser.setUserId("seller123");

        seller = new Seller();
        seller.setUser(sellerUser);

        mockProduct = new Product();
        mockProduct.setId("prod1");
        mockProduct.setSeller(seller);

        mockWishlist = new Wishlist();
        mockWishlist.setId(1L);
        User wlUser = new User();
        wlUser.setUserId("user123");
        mockWishlist.setUser(wlUser);
    }

    @Test
    @DisplayName("Thêm SP thất bại: Ném ngoại lệ khi Sản phẩm không tồn tại")
    void shouldThrowResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.findById("prod1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(ProductConstant.PRODUCT_NOT_FOUND);

        verifyNoInteractions(wishlistRepository);
    }

    @Test
    @DisplayName("Thêm SP thất bại: Ném ngoại lệ khi Cố tình thêm sản phẩm của chính mình")
    void shouldThrowIllegalStateException_WhenUserIsSellerOfProduct() {
        sellerUser.setUserId("user123"); // Same as command userId
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CartConstant.CANNOT_ADD_OWN_PRODUCT);

        verifyNoInteractions(wishlistRepository);
    }

    @Test
    @DisplayName("Thêm SP thất bại: Ném ngoại lệ khi Không tìm thấy Wishlist của User")
    void shouldThrowResourceNotFoundException_WhenWishlistNotFound() {
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(wishlistRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(WishlistConstant.WISHLIST_NOT_FOUND);
    }

    @Test
    @DisplayName("Thêm SP thất bại: Ném ngoại lệ khi Sản phẩm đã có sẵn trong Wishlist")
    void shouldThrowIllegalStateException_WhenProductAlreadyInWishlist() {
        mockWishlist.getProducts().add(mockProduct);
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(wishlistRepository.findByUserId("user123")).thenReturn(Optional.of(mockWishlist));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(WishlistConstant.PRODUCT_ALREADY_IN_WISHLIST);

        verify(wishlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("Thêm SP thành công: Thêm Sản phẩm vào Wishlist và lưu DB")
    void shouldAddProductSuccessfully() {
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(wishlistRepository.findByUserId("user123")).thenReturn(Optional.of(mockWishlist));

        WishlistResponseDto response = handler.handle(command);

        assertThat(mockWishlist.getProducts()).contains(mockProduct);
        assertThat(response).isNotNull();
        
        verify(wishlistRepository).save(mockWishlist);
    }
}
