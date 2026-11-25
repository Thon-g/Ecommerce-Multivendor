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

import java.util.*;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.ai.chat.provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiChatServiceImpl implements AiChatService {

    private final String apiKey;
    private final String model;
    private final Double temperature;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiChatServiceImpl(
            @Value("${app.ai.chat.gemini.api-key:}") String apiKey,
            @Value("${app.ai.chat.gemini.model:gemini-2.5-flash}") String model,
            @Value("${app.ai.chat.gemini.temperature:0.7}") Double temperature,
            @Value("${app.ai.chat.gemini.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
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
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API Key is not configured.");
            return AiChatResponse.builder()
                    .text(ChatConstant.DEFAULT_AI_OFFLINE_MESSAGE)
                    .recommendedProductIds(new ArrayList<>())
                    .recommendedCategoryIds(new ArrayList<>())
                    .quickReplies(new ArrayList<>())
                    .build();
        }

        try {
            Map<String, Object> requestBody = buildGeminiPayload(systemPrompt, conversationHistory, userMessage);

            String responseString = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/" + model + ":generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            String candidateText = extractCandidateText(responseString);
            return AiResponseParser.parse(candidateText);
        } catch (Exception e) {
            log.error("Failed to generate response from Gemini API (model: {}): {}", model, e.getMessage(), e);
            return AiChatResponse.builder()
                    .text(ChatConstant.DEFAULT_AI_OFFLINE_MESSAGE)
                    .recommendedProductIds(new ArrayList<>())
                    .recommendedCategoryIds(new ArrayList<>())
                    .quickReplies(new ArrayList<>())
                    .build();
        }
    }

    private Map<String, Object> buildGeminiPayload(String systemPrompt, List<AiMessage> history, String userMessage) {
        Map<String, Object> payload = new HashMap<>();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", List.of(Map.of("text", systemPrompt)));
            payload.put("systemInstruction", systemInstruction);
        }

        List<Map<String, Object>> contents = new ArrayList<>();

        if (history != null) {
            for (AiMessage msg : history) {
                String role = "user".equalsIgnoreCase(msg.getRole()) ? "user" : "model";
                contents.add(Map.of(
                        "role", role,
                        "parts", List.of(Map.of("text", msg.getContent()))
                ));
            }
        }

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        payload.put("contents", contents);
        payload.put("generationConfig", Map.of("temperature", temperature));

        return payload;
    }

    private String extractCandidateText(String responseString) {
        try {
            JsonNode root = objectMapper.readTree(responseString);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText("");
                }
            }
        } catch (Exception e) {
            log.warn("Error parsing Gemini JSON response: {}", e.getMessage());
        }
        return "";
    }
}
