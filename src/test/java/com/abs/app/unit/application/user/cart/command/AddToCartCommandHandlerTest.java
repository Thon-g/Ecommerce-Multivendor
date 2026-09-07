package com.abs.app.unit.application.user.cart.command;

import com.abs.app.application.user.cart.command.AddToCartCommand;
import com.abs.app.application.user.cart.command.AddToCartCommandHandler;
import com.abs.app.application.user.cart.dto.CartItemResponseDto;
import com.abs.app.common.constant.CartConstant;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.repository.CartRepository;
import com.abs.app.domain.repository.CouponRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddToCartCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CartCalculatorService cartCalculatorService;

    @InjectMocks
    private AddToCartCommandHandler handler;

    private AddToCartCommand command;
    private Product mockProduct;
    private Cart mockCart;
    private User sellerUser;
    private User buyerUser;

    @BeforeEach
    void setUp() {
        command = new AddToCartCommand("buyer123", "prod1", 1L, 2);

        sellerUser = new User();
        sellerUser.setUserId("seller123");

        Seller seller = new Seller();
        seller.setUser(sellerUser);

        mockProduct = new Product();
        mockProduct.setId("prod1");
        mockProduct.setSeller(seller);
        mockProduct.setMrpPrice(100);
        mockProduct.setSellingPrice(90);
        ProductSku mockSku = new ProductSku();
        mockSku.setId(1L);
        mockSku.setSize("M");
        mockSku.setColor("Red");
        mockProduct.setSkus(java.util.List.of(mockSku));

        buyerUser = new User();
        buyerUser.setUserId("buyer123");

        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setUser(buyerUser);
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng thất bại: Ném ngoại lệ khi Sản phẩm không tồn tại")
    void shouldThrowResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.findById("prod1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(ProductConstant.PRODUCT_NOT_FOUND);

        verifyNoInteractions(cartRepository);
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng thất bại: Ném ngoại lệ khi Người mua tự thêm Sản phẩm của chính mình")
    void shouldThrowIllegalStateException_WhenUserAddsOwnProduct() {
        sellerUser.setUserId("buyer123");
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CartConstant.CANNOT_ADD_OWN_PRODUCT);

        verifyNoInteractions(cartRepository);
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng thất bại: Ném ngoại lệ khi Cart chưa có và User cũng không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCartNotFoundAndUserNotFound() {
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUserId("buyer123")).thenReturn(Optional.empty());
        when(userRepository.findById("buyer123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(UserConstant.USER_NOT_EXIST);
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng thành công: Tạo CartItem mới nếu SP chưa có trong Cart")
    void shouldAddNewCartItem_WhenItemDoesNotExistInCart() {
        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUserId("buyer123")).thenReturn(Optional.of(mockCart));

        CartItemResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getSku().getSize()).isEqualTo("M");
        assertThat(mockCart.getCartItems()).hasSize(1);
        
        verify(cartCalculatorService).recalculateCart(eq(mockCart), any());
        verify(cartRepository).save(mockCart);
    }

    @Test
    @DisplayName("Thêm vào giỏ hàng thành công: Tăng số lượng nếu SP đã có trong Cart với cùng Size")
    void shouldIncreaseQuantity_WhenItemExistsInCartWithSameSize() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(mockProduct);
        existingItem.setSku(mockProduct.getSkus().get(0));
        existingItem.setQuantity(3);
        mockCart.getCartItems().add(existingItem);

        when(productRepository.findById("prod1")).thenReturn(Optional.of(mockProduct));
        when(cartRepository.findByUserId("buyer123")).thenReturn(Optional.of(mockCart));

        CartItemResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getQuantity()).isEqualTo(5); // 3 + 2 = 5
        assertThat(mockCart.getCartItems()).hasSize(1);
        
        verify(cartCalculatorService).recalculateCart(eq(mockCart), any());
        verify(cartRepository).save(mockCart);
    }
}
