package com.abs.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnProperty(name = "app.ai.embedding.provider", havingValue = "ollama", matchIfMissing = true)
    public EmbeddingModel ollamaEmbeddingModel(
            @Value("${app.ai.embedding.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${app.ai.embedding.ollama.model:nomic-embed-text}") String model) {
        log.info("Initializing OllamaEmbeddingModel (baseUrl={}, model={})", baseUrl, model);
        OllamaApi ollamaApi = new OllamaApi(baseUrl);
        return new OllamaEmbeddingModel(ollamaApi, OllamaOptions.create().withModel(model));
    }

    @Bean
    @ConditionalOnProperty(name = "app.ai.embedding.provider", havingValue = "gemini")
    public EmbeddingModel geminiEmbeddingModel(
            @Value("${app.ai.embedding.gemini.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
            @Value("${app.ai.embedding.gemini.api-key:}") String apiKey,
            @Value("${app.ai.embedding.gemini.model:text-embedding-004}") String model) {
        log.info("Initializing Gemini EmbeddingModel via OpenAI-compatible endpoint (model={})", model);
        OpenAiApi openAiApi = new OpenAiApi(baseUrl + "/v1beta/openai", apiKey);
        return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder().withModel(model).build());
    }

    @Bean
    public ChromaApi chromaApi(
            @Value("${app.ai.vector-store.chroma.host:http://localhost}") String host,
            @Value("${app.ai.vector-store.chroma.port:8000}") int port,
            ObjectMapper objectMapper) {
        String chromaEndpoint = host + ":" + port;
        log.info("Initializing ChromaApi connecting to: {}", chromaEndpoint);
        return new ChromaApi(chromaEndpoint, new RestTemplate(), objectMapper);
    }

    @Bean
    @Primary
    public VectorStore chromaVectorStore(
            EmbeddingModel embeddingModel,
            ChromaApi chromaApi,
            @Value("${app.ai.vector-store.chroma.collection-name:ecommerce_products}") String collectionName) {
        log.info("Initializing lazy ChromaVectorStore (collection={})", collectionName);
        ChromaVectorStore delegate = new ChromaVectorStore(embeddingModel, chromaApi, collectionName, true);

        // Defer collection lookup/creation until first use so app startup never fails if Chroma is offline
        return new VectorStore() {
            private volatile boolean initialized = false;

            private void ensureInitialized() {
                if (!initialized) {
                    synchronized (this) {
                        if (!initialized) {
                            try {
                                ensureCollectionExists();
                                delegate.afterPropertiesSet();
                                initialized = true;
                            } catch (Exception e) {
                                throw new IllegalStateException("Chroma VectorStore is unavailable: " + e.getMessage(), e);
                            }
                        }
                    }
                }
            }

            private void ensureCollectionExists() {
                try {
                    if (chromaApi.getCollection(collectionName) == null) {
                        chromaApi.createCollection(new ChromaApi.CreateCollectionRequest(collectionName));
                    }
                } catch (HttpClientErrorException ex) {
                    if (ex.getResponseBodyAsString().contains("InvalidCollection")
                            || ex.getStatusCode().value() == 400
                            || ex.getStatusCode().value() == 404) {
                        log.info("Creating missing Chroma collection '{}'...", collectionName);
                        chromaApi.createCollection(new ChromaApi.CreateCollectionRequest(collectionName));
                    } else {
                        throw ex;
                    }
                }
            }

            @Override
            public void add(List<Document> documents) {
                ensureInitialized();
                delegate.add(documents);
            }

            @Override
            public Optional<Boolean> delete(List<String> idList) {
                ensureInitialized();
                return delegate.delete(idList);
            }

            @Override
            public List<Document> similaritySearch(SearchRequest request) {
                ensureInitialized();
                return delegate.similaritySearch(request);
            }
        };
    }
}
