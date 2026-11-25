package com.abs.app.unit.infrastructure.ai;

import com.abs.app.infrastructure.ai.AiSearchServiceImpl;
import com.abs.app.infrastructure.mapper.VectorDocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiSearchServiceImplTest {

    @Mock
    private VectorStore vectorStore;

    private AiSearchServiceImpl aiSearchService;

    @BeforeEach
    void setUp() {
        aiSearchService = new AiSearchServiceImpl(vectorStore);
    }

    @Test
    @DisplayName("Should return fallback message when query is blank")
    void shouldReturnFallbackWhenQueryIsBlank() {
        String context = aiSearchService.searchRelevantContext("   ", 5);
        assertEquals("Không tìm thấy sản phẩm hoặc danh mục phù hợp trong kho tri thức.", context);
        verifyNoInteractions(vectorStore);
    }

    @Test
    @DisplayName("Should format product and category documents when search succeeds")
    void shouldFormatRagContextWhenSearchSucceeds() {
        Document prodDoc = new Document("Áo polo cao cấp thoáng khí", Map.of(
                VectorDocumentMapper.META_DOC_TYPE, VectorDocumentMapper.DOC_TYPE_PRODUCT,
                VectorDocumentMapper.META_PRODUCT_ID, "prod-100",
                VectorDocumentMapper.META_TITLE, "Áo Polo Nam",
                VectorDocumentMapper.META_CATEGORY_NAME, "Thời trang nam",
                VectorDocumentMapper.META_SELLING_PRICE, 250000,
                VectorDocumentMapper.META_MRP_PRICE, 350000,
                VectorDocumentMapper.META_DISCOUNT_PERCENT, 28,
                VectorDocumentMapper.META_AVERAGE_RATING, 4.8,
                VectorDocumentMapper.META_SHOP_NAME, "Shop Thoi Trang"
        ));

        Document catDoc = new Document("Danh mục thời trang nam", Map.of(
                VectorDocumentMapper.META_DOC_TYPE, VectorDocumentMapper.DOC_TYPE_CATEGORY,
                VectorDocumentMapper.META_CATEGORY_ID, "men-fashion",
                VectorDocumentMapper.META_CATEGORY_NAME, "Thời trang nam",
                VectorDocumentMapper.META_LEVEL, 1
        ));

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(prodDoc, catDoc));

        String context = aiSearchService.searchRelevantContext("áo thun", 5);

        assertNotNull(context);
        assertTrue(context.contains("SẢN PHẨM: (ID: prod-100) Áo Polo Nam"));
        assertTrue(context.contains("Shop Thoi Trang"));
        assertTrue(context.contains("250000đ"));
        assertTrue(context.contains("DANH MỤC: (ID: men-fashion) Thời trang nam"));
    }

    @Test
    @DisplayName("Should gracefully handle vector store exception and return fallback context")
    void shouldHandleExceptionGracefully() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenThrow(new RuntimeException("Chroma connection refused"));

        String context = aiSearchService.searchRelevantContext("áo polo", 5);

        assertEquals("Không tìm thấy sản phẩm hoặc danh mục phù hợp trong kho tri thức.", context);
    }

    @Test
    @DisplayName("Should return fallback message when search returns empty list")
    void shouldReturnFallbackWhenEmptyResults() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(Collections.emptyList());

        String context = aiSearchService.searchRelevantContext("không tồn tại", 5);

        assertEquals("Không tìm thấy sản phẩm hoặc danh mục phù hợp trong kho tri thức.", context);
    }
}
