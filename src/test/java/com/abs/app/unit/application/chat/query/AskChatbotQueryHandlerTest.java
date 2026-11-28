package com.abs.app.unit.application.chat.query;

import com.abs.app.application.chat.dto.ChatResponseDto;
import com.abs.app.application.chat.query.AskChatbotQuery;
import com.abs.app.application.chat.query.AskChatbotQueryHandler;
import com.abs.app.common.constant.ChatConstant;
import com.abs.app.domain.entity.*;
import com.abs.app.domain.entity.enums.ChatSenderType;
import com.abs.app.domain.entity.enums.SellerStatus;
import com.abs.app.domain.model.AiChatResponse;
import com.abs.app.domain.model.AiMessage;
import com.abs.app.domain.model.GuestChatMessage;
import com.abs.app.domain.repository.*;
import com.abs.app.domain.service.AiChatService;
import com.abs.app.domain.service.AiSearchService;
import com.abs.app.domain.service.ChatMemoryService;
import com.abs.app.domain.service.ChatPromptService;
import com.abs.app.infrastructure.ai.ChatbotPromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AskChatbotQueryHandlerTest {

    @Mock
    private AiChatService aiChatService;

    @Mock
    private AiSearchService aiSearchService;

    @Mock
    private ChatMemoryService chatMemoryService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    private ChatPromptService chatPromptService;
    private ObjectMapper objectMapper;
    private AskChatbotQueryHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        chatPromptService = new ChatbotPromptBuilder();
        handler = new AskChatbotQueryHandler(
                aiChatService,
                aiSearchService,
                chatPromptService,
                chatMemoryService,
                productRepository,
                categoryRepository,
                chatSessionRepository,
                chatMessageRepository,
                userRepository,
                objectMapper
        );
    }

    @Test
    @DisplayName("Should handle authenticated user query with Backend Source-of-Truth validation")
    void shouldHandleAuthenticatedUserWithSourceOfTruth() {
        String userId = "user-123";
        User user = new User();
        user.setUserId(userId);
        user.setEmail("user@test.com");

        ChatSession session = ChatSession.builder()
                .id("session-uuid")
                .user(user)
                .title("Tìm áo polo")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);
        when(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(anyString())).thenReturn(Collections.emptyList());
        when(aiSearchService.searchRelevantContext(anyString(), eq(5))).thenReturn("Context chunk");

        AiChatResponse mockAiResponse = AiChatResponse.builder()
                .text("Dưới đây là một số mẫu áo polo chất lượng dành cho bạn:")
                .recommendedProductIds(List.of("prod-1", "prod-2", "prod-3"))
                .recommendedCategoryIds(List.of("cat-men"))
                .quickReplies(List.of("Xem áo màu đen", "Có size L không?"))
                .build();
        when(aiChatService.generateResponse(anyString(), anyList(), anyString())).thenReturn(mockAiResponse);

        Seller activeSeller = new Seller();
        activeSeller.setStatus(SellerStatus.ACTIVE);
        activeSeller.setShopName("Polo House");

        Seller inactiveSeller = new Seller();
        inactiveSeller.setStatus(SellerStatus.SUSPENDED);
        inactiveSeller.setShopName("Suspended Shop");

        Product prod1 = new Product();
        prod1.setId("prod-1");
        prod1.setTitle("Áo Polo Classic");
        prod1.setMrpPrice(300000);
        prod1.setSellingPrice(250000);
        prod1.setDiscountPercent(16);
        prod1.setSeller(activeSeller);
        ProductSku sku1 = new ProductSku();
        sku1.setQuantity(5);
        prod1.setSkus(List.of(sku1));

        Product prod2 = new Product();
        prod2.setId("prod-2");
        prod2.setTitle("Áo Polo Hết Hàng");
        prod2.setMrpPrice(200000);
        prod2.setSellingPrice(180000);
        prod2.setSeller(activeSeller);
        ProductSku sku2 = new ProductSku();
        sku2.setQuantity(0);
        prod2.setSkus(List.of(sku2));

        Product prod3 = new Product();
        prod3.setId("prod-3");
        prod3.setTitle("Áo Polo Shop Bị Khóa");
        prod3.setSeller(inactiveSeller);
        ProductSku sku3 = new ProductSku();
        sku3.setQuantity(10);
        prod3.setSkus(List.of(sku3));

        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(prod1, prod2, prod3));

        Category cat = new Category();
        cat.setId("cat-1");
        cat.setCategoryId("cat-men");
        cat.setName("Thời trang nam");
        cat.setLevel(1);
        when(categoryRepository.findAllByIdIn(any())).thenReturn(List.of(cat));
        when(categoryRepository.findAllByCategoryIdIn(any())).thenReturn(Collections.emptyList());

        AskChatbotQuery query = AskChatbotQuery.builder()
                .userId(userId)
                .message("Tư vấn áo polo thoáng mát")
                .build();

        ChatResponseDto response = handler.handle(query);

        assertNotNull(response);
        assertEquals("Dưới đây là một số mẫu áo polo chất lượng dành cho bạn:", response.getReply());
        assertEquals(1, response.getProducts().size());
        assertEquals("prod-1", response.getProducts().get(0).getProductId());
        assertEquals("Áo Polo Classic", response.getProducts().get(0).getTitle());
        assertEquals(250000L, response.getProducts().get(0).getSellingPrice());

        assertEquals(1, response.getCategories().size());
        assertEquals("cat-men", response.getCategories().get(0).getCategoryId());
        assertEquals(2, response.getQuickReplies().size());

        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should handle guest user and save conversation history into ChatMemoryService")
    void shouldHandleGuestUserWithChatMemoryService() {
        String guestSessionId = "guest-uuid-123";

        when(chatMemoryService.getRecentMessages(guestSessionId, 6)).thenReturn(Collections.emptyList());
        when(aiSearchService.searchRelevantContext(anyString(), eq(5))).thenReturn("Empty context");

        AiChatResponse mockAiResponse = AiChatResponse.builder()
                .text("Chào bạn, tôi có thể giúp gì cho bạn hôm nay?")
                .recommendedProductIds(Collections.emptyList())
                .recommendedCategoryIds(Collections.emptyList())
                .quickReplies(Collections.emptyList())
                .build();
        when(aiChatService.generateResponse(anyString(), anyList(), anyString())).thenReturn(mockAiResponse);

        AskChatbotQuery query = AskChatbotQuery.builder()
                .guestSessionId(guestSessionId)
                .message("Xin chào")
                .build();

        ChatResponseDto response = handler.handle(query);

        assertNotNull(response);
        assertEquals(guestSessionId, response.getConversationId());
        assertEquals("Chào bạn, tôi có thể giúp gì cho bạn hôm nay?", response.getReply());
        assertTrue(response.getProducts().isEmpty());

        verify(chatMemoryService, times(2)).saveMessage(eq(guestSessionId), any(GuestChatMessage.class));
        verifyNoInteractions(chatMessageRepository);
    }

    @Test
    @DisplayName("Should return polite offline message when AI provider fails")
    void shouldReturnOfflineFallbackWhenAiProviderFails() {
        when(chatMemoryService.getRecentMessages("guest-err", 6)).thenReturn(Collections.emptyList());
        when(aiSearchService.searchRelevantContext(anyString(), eq(5))).thenReturn("Context");
        when(aiChatService.generateResponse(anyString(), anyList(), anyString()))
                .thenThrow(new RuntimeException("Provider timeout"));

        AskChatbotQuery query = AskChatbotQuery.builder()
                .guestSessionId("guest-err")
                .message("Tư vấn sản phẩm")
                .build();

        ChatResponseDto response = handler.handle(query);

        assertEquals(ChatConstant.DEFAULT_AI_OFFLINE_MESSAGE, response.getReply());
        assertTrue(response.getProducts().isEmpty());
    }

    @Test
    @DisplayName("Should limit user conversation history sent to LLM to last 6 messages (sliding window)")
    void shouldApplySlidingWindowOfSixMessages() {
        String userId = "user-window";
        User user = new User();
        user.setUserId(userId);
        ChatSession session = ChatSession.builder().id("session-w").user(user).build();

        List<ChatMessage> eightMessages = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            eightMessages.add(ChatMessage.builder()
                    .id((long) i)
                    .session(session)
                    .senderType(i % 2 == 1 ? ChatSenderType.USER : ChatSenderType.BOT)
                    .content("Msg " + i)
                    .build());
        }

        when(chatSessionRepository.findByIdAndUserId("session-w", userId)).thenReturn(Optional.of(session));
        when(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc("session-w")).thenReturn(eightMessages);
        when(aiSearchService.searchRelevantContext(anyString(), eq(5))).thenReturn("Context");
        when(aiChatService.generateResponse(anyString(), anyList(), anyString())).thenReturn(
                AiChatResponse.builder().text("Reply").build());

        handler.handle(AskChatbotQuery.builder()
                .userId(userId)
                .conversationId("session-w")
                .message("Câu hỏi mới")
                .build());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AiMessage>> historyCaptor = ArgumentCaptor.forClass(List.class);
        verify(aiChatService).generateResponse(anyString(), historyCaptor.capture(), eq("Câu hỏi mới"));
        List<AiMessage> capturedHistory = historyCaptor.getValue();
        assertEquals(6, capturedHistory.size());
        assertEquals("Msg 3", capturedHistory.get(0).getContent());
        assertEquals("Msg 8", capturedHistory.get(5).getContent());
    }
}
