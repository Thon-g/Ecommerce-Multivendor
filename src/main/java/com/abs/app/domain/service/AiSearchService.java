package com.abs.app.domain.service;

public interface AiSearchService {

    String searchRelevantContext(String query, int topK);
}
