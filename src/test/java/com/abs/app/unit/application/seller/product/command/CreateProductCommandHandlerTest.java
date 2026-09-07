package com.abs.app.unit.application.seller.product.command;

import com.abs.app.application.publicapi.product.dto.ProductResponseDto;
import com.abs.app.application.seller.product.command.CreateProductCommand;
import com.abs.app.application.seller.product.command.CreateProductCommandHandler;
import com.abs.app.common.constant.CategoryConstant;
import com.abs.app.common.constant.SellerConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductCommandHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CreateProductCommandHandler handler;

    private CreateProductCommand command;
    private Seller mockSeller;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        command = new CreateProductCommand(
                "T-Shirt", "Cool T-Shirt", 200000, 150000, List.of(new com.abs.app.application.seller.product.dto.SkuRequestDto("SKU1", "Red", "M,L", 100, 150000)), null, "CATEGORY_ID", "user123"
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
    }

    @Test
    @DisplayName("Tạo Product thất bại: Ném ngoại lệ khi Seller không tồn tại")
    void shouldThrowResourceNotFoundException_WhenSellerNotFound() {
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(SellerConstant.SELLER_NOT_FOUND);

        verifyNoInteractions(productRepository, fileStorageService);
    }

    @Test
    @DisplayName("Tạo Product thất bại: Ném ngoại lệ khi Category không tồn tại")
    void shouldThrowResourceNotFoundException_WhenCategoryNotFound() {
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(CategoryConstant.CATEGORY_NOT_FOUND);

        verifyNoInteractions(productRepository, fileStorageService);
    }

    @Test
    @DisplayName("Tạo Product thành công: Không có images")
    void shouldCreateProductSuccessfully_WithoutImages() {
        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("T-Shirt");
        assertThat(response.getDiscountPercent()).isEqualTo(25); // (200k - 150k)/200k = 25%
        assertThat(response.getCategory().getName()).isEqualTo("Fashion");

        verify(productRepository).save(any(Product.class));
        verifyNoInteractions(fileStorageService);
    }

    @Test
    @DisplayName("Tạo Product thành công: Cùng với images")
    void shouldCreateProductSuccessfully_WithImages() {
        MultipartFile mockFile = mock(MultipartFile.class);
        command.setImages(List.of(mockFile));

        when(sellerRepository.findByUserId("user123")).thenReturn(Optional.of(mockSeller));
        when(categoryRepository.findById("CATEGORY_ID")).thenReturn(Optional.of(mockCategory));
        when(fileStorageService.storeProduct(eq(mockFile), anyString())).thenReturn("path/to/image.jpg");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponseDto response = handler.handle(command);

        assertThat(response).isNotNull();
        assertThat(response.getImages()).isNotEmpty();
        assertThat(response.getImages().get(0)).isEqualTo("path/to/image.jpg"); // the mapper might extract url

        verify(fileStorageService).storeProduct(eq(mockFile), anyString());
        verify(productRepository).save(any(Product.class));
    }
}
