package com.abs.app.unit.application.seller.product.query;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.seller.product.query.GetSellerProductsQuery;
import com.abs.app.application.seller.product.query.GetSellerProductsQueryHandler;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.response.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSellerProductsQueryHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private GetSellerProductsQueryHandler handler;

    private GetSellerProductsQuery query;
    private Seller mockSeller;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        query = new GetSellerProductsQuery("keyword", "CATEGORY_ID", "user123", 0, 10);

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");
        com.abs.app.domain.entity.User mockUser = new com.abs.app.domain.entity.User();
        mockUser.setUserId("user123");
        mockSeller.setUser(mockUser);
        mockSeller.setStatus(com.abs.app.domain.entity.enums.SellerStatus.ACTIVE);

        mockProduct = new Product();
        mockProduct.setId("PRODUCT_ID");
        mockProduct.setTitle("T-Shirt");
        mockProduct.setSeller(mockSeller);
    }

    @Test
    @DisplayName("Lấy danh sách Product của Seller thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Lấy danh sách Product của Seller thành công")
    void shouldReturnSellerProductsSuccessfully() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct));

        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(productRepository.search(eq("keyword"), eq("CATEGORY_ID"), eq("SELLER_ID"), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<ProductResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getTitle()).isEqualTo("T-Shirt");
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getLimit()).isEqualTo(10);

        verify(sellerRepository).findByUserId("user123");
        verify(productRepository).search(eq("keyword"), eq("CATEGORY_ID"), eq("SELLER_ID"), any(Pageable.class));
    }
}
