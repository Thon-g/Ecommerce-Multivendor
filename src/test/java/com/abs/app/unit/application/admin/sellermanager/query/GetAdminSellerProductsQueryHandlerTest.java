package com.abs.app.unit.application.admin.sellermanager.query;

import com.abs.app.application.admin.sellermanager.query.GetAdminSellerProductsQuery;
import com.abs.app.application.admin.sellermanager.query.GetAdminSellerProductsQueryHandler;
import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminSellerProductsQueryHandlerTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetAdminSellerProductsQueryHandler handler;

    private Seller mockSeller;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_01");

        mockProduct = new Product();
        mockProduct.setId("PROD_01");
        mockProduct.setTitle("Cotton T-Shirt");
        mockProduct.setDescription("High quality cotton t-shirt");
        mockProduct.setMrpPrice(200000);
        mockProduct.setSellingPrice(150000);
        mockProduct.setDiscountPercent(25);
    }

    @Test
    @DisplayName("Lấy danh sách sản phẩm của Seller thành công khi có sản phẩm")
    void shouldReturnSellerProductsSuccessfully() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct));

        when(sellerRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockSeller));
        when(productRepository.findBySellerIdAndKeyword(eq("SELLER_01"), eq("t-shirt"), any(Pageable.class))).thenReturn(page);

        GetAdminSellerProductsQuery query = new GetAdminSellerProductsQuery("SELLER_01", "t-shirt", 1, 10);
        PageResponse<ProductResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getId()).isEqualTo("PROD_01");
        assertThat(response.getItems().get(0).getTitle()).isEqualTo("Cotton T-Shirt");
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getLimit()).isEqualTo(10);

        verify(sellerRepository).findBySellerId("SELLER_01");
        verify(productRepository).findBySellerIdAndKeyword(eq("SELLER_01"), eq("t-shirt"), any(Pageable.class));
    }

    @Test
    @DisplayName("Lấy danh sách sản phẩm của Seller rỗng thành công")
    void shouldReturnEmptyProductsSuccessfully() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());

        when(sellerRepository.findBySellerId("SELLER_01")).thenReturn(Optional.of(mockSeller));
        when(productRepository.findBySellerIdAndKeyword(eq("SELLER_01"), eq(null), any(Pageable.class))).thenReturn(page);

        GetAdminSellerProductsQuery query = new GetAdminSellerProductsQuery("SELLER_01", null, 1, 10);
        PageResponse<ProductResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0);
    }

    @Test
    @DisplayName("Lấy danh sách sản phẩm thất bại: Ném ResourceNotFoundException khi Seller không tồn tại")
    void shouldThrowException_WhenSellerNotFound() {
        when(sellerRepository.findBySellerId("UNKNOWN")).thenReturn(Optional.empty());

        GetAdminSellerProductsQuery query = new GetAdminSellerProductsQuery("UNKNOWN", null, 1, 10);

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }
}

