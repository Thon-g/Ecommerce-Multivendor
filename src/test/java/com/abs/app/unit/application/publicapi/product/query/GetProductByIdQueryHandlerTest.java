package com.abs.app.unit.application.publicapi.product.query;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.publicapi.product.query.GetProductByIdQuery;
import com.abs.app.application.publicapi.product.query.GetProductByIdQueryHandler;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.repository.ProductRepository;
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
class GetProductByIdQueryHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductByIdQueryHandler handler;

    private GetProductByIdQuery query;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        query = new GetProductByIdQuery("PRODUCT_ID");

        mockProduct = new Product();
        mockProduct.setId("PRODUCT_ID");
        mockProduct.setTitle("T-Shirt");
    }

    @Test
    @DisplayName("Lấy chi tiết Product thất bại: Ném ngoại lệ khi không tìm thấy")
    void shouldThrowResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(ProductConstant.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("Lấy chi tiết Product thành công")
    void shouldReturnProductDetailsSuccessfully() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));

        ProductResponseDto response = handler.handle(query);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("PRODUCT_ID");
        assertThat(response.getTitle()).isEqualTo("T-Shirt");

        verify(productRepository).findById("PRODUCT_ID");
    }
}
