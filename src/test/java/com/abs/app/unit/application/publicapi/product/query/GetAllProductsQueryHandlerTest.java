package com.abs.app.unit.application.publicapi.product.query;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.publicapi.product.query.GetAllProductsQuery;
import com.abs.app.application.publicapi.product.query.GetAllProductsQueryHandler;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllProductsQueryHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetAllProductsQueryHandler handler;

    private GetAllProductsQuery query;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        query = new GetAllProductsQuery("keyword", "CATEGORY_ID", 0, 10);

        mockProduct = new Product();
        mockProduct.setId("PRODUCT_ID");
        mockProduct.setTitle("T-Shirt");
    }

    @Test
    @DisplayName("Lấy danh sách Product thành công (public)")
    void shouldReturnProductsSuccessfully() {
        Page<Product> page = new PageImpl<>(List.of(mockProduct));

        // sellerId is null for public API
        when(productRepository.search(eq("keyword"), eq("CATEGORY_ID"), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<ProductResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getTitle()).isEqualTo("T-Shirt");
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getLimit()).isEqualTo(10);

        verify(productRepository).search(eq("keyword"), eq("CATEGORY_ID"), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Lấy danh sách Product thành công (Empty)")
    void shouldReturnEmptyProductsSuccessfully() {
        Page<Product> page = new PageImpl<>(Collections.emptyList());

        when(productRepository.search(eq("keyword"), eq("CATEGORY_ID"), isNull(), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<ProductResponseDto> response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotal()).isEqualTo(0);

        verify(productRepository).search(eq("keyword"), eq("CATEGORY_ID"), isNull(), any(Pageable.class));
    }
}
