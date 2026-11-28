package com.abs.app.infrastructure.ai;

import com.abs.app.common.constant.ChatConstant;
import com.abs.app.domain.service.AiSearchService;
import com.abs.app.infrastructure.mapper.VectorDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AiSearchServiceImpl implements AiSearchService {

    private final VectorStore vectorStore;

    public AiSearchServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public String searchRelevantContext(String query, int topK) {
        if (query == null || query.isBlank()) {
            return ChatConstant.EMPTY_CONTEXT_MESSAGE;
        }

        int k = topK > 0 ? topK : 5;
        List<Document> productDocs = searchByDocType(query, k, VectorDocumentMapper.DOC_TYPE_PRODUCT);
        List<Document> categoryDocs = searchByDocType(query, k, VectorDocumentMapper.DOC_TYPE_CATEGORY);

        List<Document> combined = new ArrayList<>();
        combined.addAll(productDocs);
        combined.addAll(categoryDocs);

        log.debug("Retrieved {} products and {} categories from vector store for query '{}'",
                productDocs.size(), categoryDocs.size(), query);

        return formatDocuments(combined);
    }

    private List<Document> searchByDocType(String query, int topK, String docType) {
        try {
            FilterExpressionBuilder builder = new FilterExpressionBuilder();
            SearchRequest request = SearchRequest.query(query)
                    .withTopK(topK)
                    .withSimilarityThreshold(SearchRequest.SIMILARITY_THRESHOLD_ACCEPT_ALL)
                    .withFilterExpression(builder.eq(VectorDocumentMapper.META_DOC_TYPE, docType).build());

            List<Document> results = vectorStore.similaritySearch(request);
            return results != null ? results : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error performing similarity search for docType '{}' and query '{}': {}", docType, query, e.getMessage());
            // Degrade gracefully when vector store is unreachable so chat remains functional
            return Collections.emptyList();
        }
    }

    private String formatDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return ChatConstant.EMPTY_CONTEXT_MESSAGE;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            String docType = String.valueOf(doc.getMetadata().getOrDefault(VectorDocumentMapper.META_DOC_TYPE, ""));

            if (VectorDocumentMapper.DOC_TYPE_PRODUCT.equalsIgnoreCase(docType)) {
                appendProductChunk(sb, i + 1, doc);
            } else if (VectorDocumentMapper.DOC_TYPE_CATEGORY.equalsIgnoreCase(docType)) {
                appendCategoryChunk(sb, i + 1, doc);
            } else {
                sb.append(String.format("[%d] %s\n", i + 1, doc.getContent()));
            }
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private void appendProductChunk(StringBuilder sb, int index, Document doc) {
        var meta = doc.getMetadata();
        sb.append(String.format("[%d] SẢN PHẨM: (ID: %s) %s\n",
                index,
                meta.getOrDefault(VectorDocumentMapper.META_PRODUCT_ID, ""),
                meta.getOrDefault(VectorDocumentMapper.META_TITLE, "")));
        sb.append(String.format("   - Danh mục: %s | Shop: %s\n",
                meta.getOrDefault(VectorDocumentMapper.META_CATEGORY_NAME, ""),
                meta.getOrDefault(VectorDocumentMapper.META_SHOP_NAME, "")));
        sb.append(String.format("   - Giá bán: %sđ (Giá gốc: %sđ, Giảm: %s%%) | Đánh giá: %s⭐\n",
                meta.get(VectorDocumentMapper.META_SELLING_PRICE),
                meta.get(VectorDocumentMapper.META_MRP_PRICE),
                meta.get(VectorDocumentMapper.META_DISCOUNT_PERCENT),
                meta.get(VectorDocumentMapper.META_AVERAGE_RATING)));
        appendCleanContent(sb, doc.getContent());
    }

    private void appendCategoryChunk(StringBuilder sb, int index, Document doc) {
        var meta = doc.getMetadata();
        sb.append(String.format("[%d] DANH MỤC: (ID: %s) %s (Cấp: %s)\n",
                index,
                meta.getOrDefault(VectorDocumentMapper.META_CATEGORY_ID, ""),
                meta.getOrDefault(VectorDocumentMapper.META_CATEGORY_NAME, ""),
                meta.get(VectorDocumentMapper.META_LEVEL)));
        appendCleanContent(sb, doc.getContent());
    }

    private void appendCleanContent(StringBuilder sb, String content) {
        if (content != null && !content.isBlank()) {
            sb.append(String.format("   - Thông tin: %s\n", content.replace("\n", " ").trim()));
        }
    }
}
