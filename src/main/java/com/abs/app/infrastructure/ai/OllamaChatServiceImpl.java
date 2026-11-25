package com.abs.app.infrastructure.ai;

import com.abs.app.common.constant.ChatConstant;
import com.abs.app.domain.model.AiChatResponse;
import com.abs.app.domain.model.AiMessage;
import com.abs.app.domain.service.AiChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.ai.chat.provider", havingValue = "ollama")
public class OllamaChatServiceImpl implements AiChatService {

    private final String model;
    private final Double temperature;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OllamaChatServiceImpl(
            @Value("${app.ai.chat.ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${app.ai.chat.ollama.model:llama3:8b}") String model,
            @Value("${app.ai.chat.ollama.temperature:0.7}") Double temperature,
            ObjectMapper objectMapper) {
        this.model = model;
        this.temperature = temperature;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public AiChatResponse generateResponse(String systemPrompt, List<AiMessage> conversationHistory, String userMessage) {
        try {
            Map<String, Object> requestBody = buildOllamaPayload(systemPrompt, conversationHistory, userMessage);

            String responseString = restClient.post()
                    .uri("/api/chat")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            String content = extractOllamaMessageContent(responseString);
            return AiResponseParser.parse(content);
        } catch (Exception e) {
            log.error("Failed to generate response from Ollama API (model: {}): {}", model, e.getMessage(), e);
            return AiChatResponse.builder()
                    .text(ChatConstant.DEFAULT_AI_OFFLINE_MESSAGE)
                    .recommendedProductIds(new ArrayList<>())
                    .recommendedCategoryIds(new ArrayList<>())
                    .quickReplies(new ArrayList<>())
                    .build();
        }
    }

    private Map<String, Object> buildOllamaPayload(String systemPrompt, List<AiMessage> history, String userMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("stream", false);

        List<Map<String, String>> messages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }

        if (history != null) {
            for (AiMessage msg : history) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }

        messages.add(Map.of("role", "user", "content", userMessage));

        payload.put("messages", messages);
        payload.put("options", Map.of("temperature", temperature));

        return payload;
    }

    private String extractOllamaMessageContent(String responseString) {
        try {
            JsonNode root = objectMapper.readTree(responseString);
            return root.path("message").path("content").asText("");
        } catch (Exception e) {
            log.warn("Error parsing Ollama JSON response: {}", e.getMessage());
        }
        return "";
    }
}
