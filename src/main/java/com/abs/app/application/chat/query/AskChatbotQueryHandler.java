package com.abs.app.application.chat.query;

import com.abs.app.application.chat.dto.*;
import com.abs.app.common.constant.ChatConstant;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.entity.enums.ChatSenderType;
import com.abs.app.domain.entity.enums.ChatSessionStatus;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.model.AiChatResponse;
import com.abs.app.domain.model.AiMessage;
import com.abs.app.domain.model.GuestChatMessage;
import com.abs.app.domain.repository.*;
import com.abs.app.domain.service.AiChatService;
import com.abs.app.domain.service.AiSearchService;
import com.abs.app.domain.service.ChatMemoryService;
import com.abs.app.domain.service.ChatPromptService;
import com.abs.app.infrastructure.mapper.ChatMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AskChatbotQueryHandler {

    private static final int HISTORY_WINDOW_SIZE = 6;
    private static final int RAG_TOP_K = 5;

    private final AiChatService aiChatService;
    private final AiSearchService aiSearchService;
    private final ChatPromptService chatPromptService;
    private final ChatMemoryService chatMemoryService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatResponseDto handle(AskChatbotQuery query) {
        boolean isAuthenticated = query.getUserId() != null && !query.getUserId().isBlank();

        ChatSession userSession = isAuthenticated
                ? resolveUserSession(query.getUserId(), query.getConversationId(), query.getMessage())
                : null;

        String guestSessionId = !isAuthenticated
                ? resolveGuestSessionId(query.getGuestSessionId())
                : null;

        String conversationId = isAuthenticated ? userSession.getId() : guestSessionId;

        List<AiMessage> history = isAuthenticated
                ? loadUserHistory(userSession.getId())
                : loadGuestHistory(guestSessionId);

        String ragContext = aiSearchService.searchRelevantContext(query.getMessage(), RAG_TOP_K);
        String systemPrompt = chatPromptService.buildSystemPrompt(ragContext);

        AiChatResponse aiResponse = callAiProvider(systemPrompt, history, query.getMessage());

        List<ProductCardDto> validatedProducts = validateAndFetchProducts(aiResponse.getRecommendedProductIds());
        List<CategoryCardDto> validatedCategories = validateAndFetchCategories(aiResponse.getRecommendedCategoryIds());

        ChatMessageMetadataDto metadata = buildMetadata(validatedProducts, validatedCategories, aiResponse.getQuickReplies());

        if (isAuthenticated) {
            saveUserTurnToDatabase(userSession, query.getMessage(), aiResponse.getText(), metadata);
        } else {
            saveGuestTurnToRedis(guestSessionId, query.getMessage(), aiResponse.getText(), metadata);
        }

        return ChatResponseDto.builder()
                .conversationId(conversationId)
                .reply(aiResponse.getText())
                .products(validatedProducts)
                .categories(validatedCategories)
                .quickReplies(aiResponse.getQuickReplies())
                .build();
    }

    private String resolveGuestSessionId(String guestSessionId) {
        return (guestSessionId == null || guestSessionId.isBlank())
                ? UUID.randomUUID().toString()
                : guestSessionId;
    }

    private ChatSession resolveUserSession(String userId, String conversationId, String firstMessage) {
        if (conversationId != null && !conversationId.isBlank()) {
            Optional<ChatSession> existing = chatSessionRepository.findByIdAndUserId(conversationId, userId);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        ChatSession newSession = ChatSession.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .title(buildSessionTitle(firstMessage))
                .status(ChatSessionStatus.ACTIVE)
                .build();

        return chatSessionRepository.save(newSession);
    }

    private String buildSessionTitle(String message) {
        if (message == null || message.isBlank()) {
            return "Cuộc trò chuyện mới";
        }
        return message.length() > 50 ? message.substring(0, 47) + "..." : message;
    }

    private List<AiMessage> loadUserHistory(String sessionId) {
        List<ChatMessage> dbMessages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        int fromIdx = Math.max(0, dbMessages.size() - HISTORY_WINDOW_SIZE);
        return dbMessages.subList(fromIdx, dbMessages.size()).stream()
                .map(msg -> new AiMessage(toAiRole(msg.getSenderType()), msg.getContent()))
                .toList();
    }

    private List<AiMessage> loadGuestHistory(String guestSessionId) {
        return chatMemoryService.getRecentMessages(guestSessionId, HISTORY_WINDOW_SIZE).stream()
                .map(msg -> new AiMessage(toAiRole(msg.getSenderType()), msg.getContent()))
                .toList();
    }

    private String toAiRole(ChatSenderType senderType) {
        return senderType == ChatSenderType.USER ? "user" : "assistant";
    }

    private AiChatResponse callAiProvider(String systemPrompt, List<AiMessage> history, String userMessage) {
        try {
            AiChatResponse response = aiChatService.generateResponse(systemPrompt, history, userMessage);
            return response != null ? response : createOfflineFallbackResponse();
        } catch (Exception e) {
            log.error("AI chat provider generation failed: {}", e.getMessage());
            return createOfflineFallbackResponse();
        }
    }

    private AiChatResponse createOfflineFallbackResponse() {
        return AiChatResponse.builder()
                .text(ChatConstant.DEFAULT_AI_OFFLINE_MESSAGE)
                .recommendedProductIds(new ArrayList<>())
                .recommendedCategoryIds(new ArrayList<>())
                .quickReplies(new ArrayList<>())
                .build();
    }

    private List<ProductCardDto> validateAndFetchProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Re-fetch live MySQL records because vector DB snapshots can have stale price or stock
        return productRepository.findAllByIdIn(productIds).stream()
                .filter(this::isProductAvailable)
                .map(ChatMapper::toProductCardDto)
                .toList();
    }

    private boolean isProductAvailable(Product product) {
        if (product.getSeller() == null || !SellerStatus.ACTIVE.equals(product.getSeller().getStatus())) {
            log.info("Filtering out product {}: seller is inactive or null", product.getId());
            return false;
        }

        int totalStock = (product.getSkus() != null)
                ? product.getSkus().stream().mapToInt(sku -> sku.getQuantity() != null ? sku.getQuantity() : 0).sum()
                : 0;
        if (totalStock <= 0) {
            log.info("Filtering out product {}: out of stock", product.getId());
            return false;
        }
        return true;
    }

    private List<CategoryCardDto> validateAndFetchCategories(List<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Category> uniqueMap = new LinkedHashMap<>();
        categoryRepository.findAllByIdIn(categoryIds).forEach(cat -> uniqueMap.put(cat.getId(), cat));
        categoryRepository.findAllByCategoryIdIn(categoryIds).forEach(cat -> uniqueMap.put(cat.getId(), cat));

        return uniqueMap.values().stream()
                .map(ChatMapper::toCategoryCardDto)
                .toList();
    }

    private ChatMessageMetadataDto buildMetadata(
            List<ProductCardDto> products,
            List<CategoryCardDto> categories,
            List<String> quickReplies) {
        return ChatMessageMetadataDto.builder()
                .productIds(products.stream().map(ProductCardDto::getProductId).toList())
                .categoryIds(categories.stream().map(CategoryCardDto::getCategoryId).toList())
                .quickReplies(quickReplies)
                .build();
    }

    private void saveUserTurnToDatabase(ChatSession session, String userText, String botReply, ChatMessageMetadataDto metadataDto) {
        chatMessageRepository.save(ChatMessage.builder()
                .session(session)
                .senderType(ChatSenderType.USER)
                .content(userText)
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .session(session)
                .senderType(ChatSenderType.BOT)
                .content(botReply)
                .metadata(serializeMetadata(session.getId(), metadataDto))
                .build());

        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    private String serializeMetadata(String id, ChatMessageMetadataDto metadataDto) {
        try {
            return objectMapper.writeValueAsString(metadataDto);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize metadata for {}: {}", id, e.getMessage());
            return null;
        }
    }

    private void saveGuestTurnToRedis(String guestSessionId, String userText, String botReply, ChatMessageMetadataDto metadataDto) {
        LocalDateTime now = LocalDateTime.now();

        chatMemoryService.saveMessage(guestSessionId, GuestChatMessage.builder()
                .senderType(ChatSenderType.USER)
                .content(userText)
                .createdAt(now)
                .build());

        chatMemoryService.saveMessage(guestSessionId, GuestChatMessage.builder()
                .senderType(ChatSenderType.BOT)
                .content(botReply)
                .metadataJson(serializeMetadata(guestSessionId, metadataDto))
                .createdAt(now.plusSeconds(1))
                .build());
    }
}
