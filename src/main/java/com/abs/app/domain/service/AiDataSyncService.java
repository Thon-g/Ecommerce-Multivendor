package com.abs.app.domain.service;

import com.abs.app.domain.model.AiSyncResult;

public interface AiDataSyncService {

    AiSyncResult syncAll();

    AiSyncResult syncProducts();

    AiSyncResult syncCategories();

    void syncSingleProduct(String productId);

    void deleteProductFromVectorStore(String productId);
}
