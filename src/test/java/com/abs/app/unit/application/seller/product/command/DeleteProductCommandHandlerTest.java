package com.abs.app.unit.application.seller.product.command;

import com.abs.app.application.seller.product.command.DeleteProductCommand;
import com.abs.app.application.seller.product.command.DeleteProductCommandHandler;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductCommandHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private DeleteProductCommandHandler handler;

    private DeleteProductCommand command;
    private Product mockProduct;
    private Seller mockSeller;

    @BeforeEach
    void setUp() {
        command = new DeleteProductCommand("PRODUCT_ID", "user123");

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");
        com.abs.app.domain.entity.User mockUser = new com.abs.app.domain.entity.User();
        mockUser.setUserId("user123");
        mockSeller.setUser(mockUser);
        mockSeller.setStatus(com.abs.app.domain.entity.enums.SellerStatus.ACTIVE);

        mockProduct = new Product();
        mockProduct.setId("PRODUCT_ID");
        mockProduct.setSeller(mockSeller);
    }

    @Test
    @DisplayName("Xóa Product thất bại: Ném ngoại lệ khi Product không tồn tại")
    void shouldThrowResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(ProductConstant.PRODUCT_NOT_FOUND);

        verifyNoInteractions(sellerRepository);
    }

    @Test
    @DisplayName("Xóa Product thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Xóa Product thất bại: Ném ngoại lệ khi Product không thuộc về Seller")
    void shouldThrowBusinessException_WhenProductForbidden() {
        Seller anotherSeller = new Seller();
        anotherSeller.setSellerId("ANOTHER_SELLER_ID");
        mockProduct.setSeller(anotherSeller);

        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ProductConstant.PRODUCT_FORBIDDEN);

        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Xóa Product thành công")
    void shouldDeleteProductSuccessfully() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));

        handler.handle(command);

        verify(productRepository).delete(mockProduct);
    }
}
