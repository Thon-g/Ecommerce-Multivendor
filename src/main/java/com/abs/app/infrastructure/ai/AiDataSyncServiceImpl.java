package com.abs.app.infrastructure.ai;

import com.abs.app.common.constant.ChatConstant;
import com.abs.app.domain.entity.Category;
import com.abs.app.domain.entity.Product;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.model.AiSyncResult;
import com.abs.app.domain.repository.CategoryRepository;
import com.abs.app.domain.repository.ProductRepository;
import com.abs.app.domain.service.AiDataSyncService;
import com.abs.app.infrastructure.mapper.VectorDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiDataSyncServiceImpl implements AiDataSyncService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VectorDocumentMapper vectorDocumentMapper;
    private final VectorStore vectorStore;

    @Override
    @Transactional(readOnly = true)
    public AiSyncResult syncAll() {
        long startTime = System.currentTimeMillis();
        log.info("Starting full synchronization of Products and Categories to Vector Database...");

        try {
            int productsCount = internalSyncProducts();
            int categoriesCount = internalSyncCategories();
            long duration = System.currentTimeMillis() - startTime;

            return AiSyncResult.builder()
                    .success(true)
                    .message(ChatConstant.ALL_SYNC_SUCCESS)
                    .totalProductsSynced(productsCount)
                    .totalCategoriesSynced(categoriesCount)
                    .durationMillis(duration)
                    .build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to synchronize all data to Vector Database: {}", e.getMessage(), e);
            return AiSyncResult.builder()
                    .success(false)
                    .message(ChatConstant.SYNC_FAILED + ": " + e.getMessage())
                    .durationMillis(duration)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AiSyncResult syncProducts() {
        long startTime = System.currentTimeMillis();
        try {
            int productsCount = internalSyncProducts();
            long duration = System.currentTimeMillis() - startTime;

            return AiSyncResult.builder()
                    .success(true)
                    .message(ChatConstant.PRODUCT_SYNC_SUCCESS)
                    .totalProductsSynced(productsCount)
                    .durationMillis(duration)
                    .build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to synchronize products to Vector Database: {}", e.getMessage(), e);
            return AiSyncResult.builder()
                    .success(false)
                    .message(ChatConstant.SYNC_FAILED + ": " + e.getMessage())
                    .durationMillis(duration)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AiSyncResult syncCategories() {
        long startTime = System.currentTimeMillis();
        try {
            int categoriesCount = internalSyncCategories();
            long duration = System.currentTimeMillis() - startTime;

            return AiSyncResult.builder()
                    .success(true)
                    .message(ChatConstant.CATEGORY_SYNC_SUCCESS)
                    .totalCategoriesSynced(categoriesCount)
                    .durationMillis(duration)
                    .build();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to synchronize categories to Vector Database: {}", e.getMessage(), e);
            return AiSyncResult.builder()
                    .success(false)
                    .message(ChatConstant.SYNC_FAILED + ": " + e.getMessage())
                    .durationMillis(duration)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void syncSingleProduct(String productId) {
        if (productId == null || productId.isBlank()) {
            return;
        }

        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product p = productOpt.get();
                boolean isSellerActive = p.getSeller() != null && SellerStatus.ACTIVE.equals(p.getSeller().getStatus());
                boolean hasStock = p.getSkus() != null && p.getSkus().stream().anyMatch(s -> s.getQuantity() != null && s.getQuantity() > 0);

                if (isSellerActive && hasStock) {
                    Document doc = vectorDocumentMapper.toProductDocument(p);
                    if (doc != null) {
                        vectorStore.add(List.of(doc));
                        return;
                    }
                }
            }
            deleteProductFromVectorStore(productId);
        } catch (Exception e) {
            log.warn("Failed to sync single product to vector store (productId={}): {}", productId, e.getMessage());
        }
    }

    @Override
    public void deleteProductFromVectorStore(String productId) {
        if (productId == null || productId.isBlank()) {
            return;
        }

        try {
            vectorStore.delete(List.of("doc_prod_" + productId));
        } catch (Exception e) {
            log.warn("Failed to delete product from vector store (productId={}): {}", productId, e.getMessage());
        }
    }

    private int internalSyncProducts() {
        List<Product> activeProducts = productRepository.findAllActiveProductsWithStock();
        if (activeProducts == null || activeProducts.isEmpty()) {
            return 0;
        }

        List<Document> documents = activeProducts.stream()
                .map(vectorDocumentMapper::toProductDocument)
                .filter(Objects::nonNull)
                .toList();

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
        return documents.size();
    }

    private int internalSyncCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories == null || categories.isEmpty()) {
            return 0;
        }

        List<Document> documents = categories.stream()
                .map(vectorDocumentMapper::toCategoryDocument)
                .filter(Objects::nonNull)
                .toList();

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
        return documents.size();
    }
}
