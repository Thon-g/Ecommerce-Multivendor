package com.abs.app.unit.infrastructure.ai;

import com.abs.app.common.constant.ChatConstant;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.ProductSku;
import com.abs.app.domain.entity.Seller;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.model.AiSyncResult;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.infrastructure.ai.AiDataSyncServiceImpl;
import com.abs.app.infrastructure.mapper.VectorDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiDataSyncServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private VectorDocumentMapper vectorDocumentMapper = new VectorDocumentMapper();

    @Mock
    private VectorStore vectorStore;

    @InjectMocks
    private AiDataSyncServiceImpl syncService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        Seller seller = new Seller();
        seller.setSellerId("seller-1");
        seller.setShopName("Official Shop");
        seller.setStatus(SellerStatus.ACTIVE);

        testCategory = new Category();
        testCategory.setCategoryId("cat-electronics");
        testCategory.setName("Điện tử");
        testCategory.setLevel(1);

        ProductSku sku = new ProductSku();
        sku.setId(1L);
        sku.setColor("Đen");
        sku.setSize("Standard");
        sku.setQuantity(10);

        testProduct = new Product();
        testProduct.setId("prod-phone-01");
        testProduct.setTitle("Điện thoại thông minh");
        testProduct.setDescription("Mô tả điện thoại");
        testProduct.setSellingPrice(15000000);
        testProduct.setMrpPrice(18000000);
        testProduct.setDiscountPercent(16);
        testProduct.setSeller(seller);
        testProduct.setCategory(testCategory);
        testProduct.setSkus(List.of(sku));
    }

    @Test
    @DisplayName("syncAll successfully syncs active products and categories")
    void testSyncAll_Success() {
        when(productRepository.findAllActiveProductsWithStock()).thenReturn(List.of(testProduct));
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        doNothing().when(vectorStore).add(anyList());

        AiSyncResult result = syncService.syncAll();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotalProductsSynced()).isEqualTo(1);
        assertThat(result.getTotalCategoriesSynced()).isEqualTo(1);
        assertThat(result.getMessage()).isEqualTo(ChatConstant.ALL_SYNC_SUCCESS);
        verify(vectorStore, times(2)).add(anyList());
    }

    @Test
    @DisplayName("syncProducts successfully syncs active products only")
    void testSyncProducts_Success() {
        when(productRepository.findAllActiveProductsWithStock()).thenReturn(List.of(testProduct));
        doNothing().when(vectorStore).add(anyList());

        AiSyncResult result = syncService.syncProducts();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotalProductsSynced()).isEqualTo(1);
        assertThat(result.getMessage()).isEqualTo(ChatConstant.PRODUCT_SYNC_SUCCESS);
        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    @DisplayName("syncCategories successfully syncs categories only")
    void testSyncCategories_Success() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        doNothing().when(vectorStore).add(anyList());

        AiSyncResult result = syncService.syncCategories();

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotalCategoriesSynced()).isEqualTo(1);
        assertThat(result.getMessage()).isEqualTo(ChatConstant.CATEGORY_SYNC_SUCCESS);
        verify(vectorStore, times(1)).add(anyList());
    }

    @Test
    @DisplayName("syncAll returns error result gracefully when VectorStore throws exception")
    void testSyncAll_WhenVectorStoreFails_ReturnsFailure() {
        when(productRepository.findAllActiveProductsWithStock()).thenReturn(List.of(testProduct));
        doThrow(new RuntimeException("Connection refused to Chroma DB")).when(vectorStore).add(anyList());

        AiSyncResult result = syncService.syncAll();

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains(ChatConstant.SYNC_FAILED);
        assertThat(result.getMessage()).contains("Connection refused to Chroma DB");
    }

    @Test
    @DisplayName("syncSingleProduct updates product in vector store if active and in stock")
    void testSyncSingleProduct_ActiveAndInStock() {
        when(productRepository.findById("prod-phone-01")).thenReturn(Optional.of(testProduct));
        doNothing().when(vectorStore).add(anyList());

        syncService.syncSingleProduct("prod-phone-01");

        verify(vectorStore, times(1)).add(anyList());
        verify(vectorStore, never()).delete(anyList());
    }

    @Test
    @DisplayName("syncSingleProduct deletes product from vector store if out of stock")
    void testSyncSingleProduct_OutOfStock_DeletesFromVectorStore() {
        testProduct.setSkus(Collections.emptyList());
        when(productRepository.findById("prod-phone-01")).thenReturn(Optional.of(testProduct));

        syncService.syncSingleProduct("prod-phone-01");

        verify(vectorStore, never()).add(anyList());
        verify(vectorStore, times(1)).delete(List.of("doc_prod_prod-phone-01"));
    }

    @Test
    @DisplayName("deleteProductFromVectorStore invokes vectorStore.delete with correct docId")
    void testDeleteProductFromVectorStore() {
        syncService.deleteProductFromVectorStore("prod-phone-01");

        verify(vectorStore, times(1)).delete(List.of("doc_prod_prod-phone-01"));
    }
}
