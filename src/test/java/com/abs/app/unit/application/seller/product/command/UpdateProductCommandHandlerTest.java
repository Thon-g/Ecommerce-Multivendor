package com.abs.app.unit.application.seller.product.command;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.seller.product.command.UpdateProductCommand;
import com.abs.app.application.seller.product.command.UpdateProductCommandHandler;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.constant.ProductConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.BusinessException;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductImage;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.repository.SellerRepository;
import com.abs.app.infrastructure.file.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductCommandHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UpdateProductCommandHandler handler;

    private UpdateProductCommand command;
    private Product mockProduct;
    private Seller mockSeller;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        command = new UpdateProductCommand(
                "PRODUCT_ID", "Updated T-Shirt", "Updated description", 300000, 250000, 50, "Blue", "L", null, "CATEGORY_ID", "user123"
        );

        mockSeller = new Seller();
        mockSeller.setSellerId("SELLER_ID");
        com.abs.app.domain.entity.User mockUser = new com.abs.app.domain.entity.User();
        mockUser.setUserId("user123");
        mockSeller.setUser(mockUser);
        mockSeller.setStatus(com.abs.app.domain.entity.enums.SellerStatus.ACTIVE);

        mockCategory = new Category();
        mockCategory.setId("CATEGORY_ID");
        mockCategory.setName("Fashion");

        mockProduct = new Product();
        mockProduct.setId("PRODUCT_ID");
        mockProduct.setSeller(mockSeller);
        mockProduct.setImages(new ArrayList<>());
    }

    @Test
    @DisplayName("Cập nhật Product thất bại: Ném ngoại lệ khi Product không tồn tại")
    void shouldThrowResourceNotFoundException_WhenProductNotFound() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(ProductConstant.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("Cập nhật Product thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);
    }

    @Test
    @DisplayName("Cập nhật Product thất bại: Ném ngoại lệ khi Product không thuộc về Seller")
    void shouldThrowBusinessException_WhenProductForbidden() {
        Seller anotherSeller = new Seller();
        anotherSeller.setSellerId("ANOTHER_SELLER_ID");
        mockProduct.setSeller(anotherSeller);

        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ProductConstant.PRODUCT_FORBIDDEN);
    }

    @Test
    @DisplayName("Cập nhật Product thất bại: Ném ngoại lệ khi Category không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("Cập nhật Product thành công: Không có ảnh mới")
    void shouldUpdateProductSuccessfully_WithoutNewImages() {
        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated T-Shirt");
        assertThat(response.getDiscountPercent()).isEqualTo(17); // (300k - 250k)/300k = 16.67% -> 17%

        verify(productRepository).save(any(Product.class));
        verifyNoInteractions(fileStorageService);
    }

    @Test
    @DisplayName("Cập nhật Product thành công: Có ảnh mới")
    void shouldUpdateProductSuccessfully_WithNewImages() {
        MultipartFile mockFile = mock(MultipartFile.class);
        command.setImages(List.of(mockFile));

        when(productRepository.findById("PRODUCT_ID")).thenReturn(Optional.of(mockProduct));
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(fileStorageService.storeProduct(eq(mockFile), eq("PRODUCT_ID"))).thenReturn("path/to/new_image.jpg");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(mockProduct.getImages()).hasSize(1);
        assertThat(mockProduct.getImages().get(0).getImageUrl()).isEqualTo("path/to/new_image.jpg");

        verify(fileStorageService).storeProduct(eq(mockFile), eq("PRODUCT_ID"));
        verify(productRepository).save(any(Product.class));
    }
}
