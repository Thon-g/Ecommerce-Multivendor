package com.abs.app.unit.application.chat.query;

import com.abs.app.application.chat.dto.ChatMessageDto;
import com.abs.app.application.chat.query.GetChatHistoryQuery;
import com.abs.app.application.chat.query.GetChatHistoryQueryHandler;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.ChatMessage;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.entity.enums.ChatSenderType;
import com.abs.app.domain.repository.ChatMessageRepository;
import com.abs.app.domain.repository.ChatSessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetChatHistoryQueryHandlerTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    private GetChatHistoryQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetChatHistoryQueryHandler(chatSessionRepository, chatMessageRepository, new ObjectMapper());
    }

    @Test
    @DisplayName("Should return paged messages when user owns the session")
    void shouldReturnPagedHistory() {
        ChatSession session = ChatSession.builder().id("s1").build();
        when(chatSessionRepository.findByIdAndUserId("s1", "u1")).thenReturn(Optional.of(session));

        ChatMessage msg = ChatMessage.builder()
                .id(1L)
                .session(session)
                .senderType(ChatSenderType.BOT)
                .content("Xin chào")
                .metadata("{\"productIds\":[\"p1\"],\"categoryIds\":[],\"quickReplies\":[]}")
                .build();

        Page<ChatMessage> page = new PageImpl<>(List.of(msg));
        when(chatMessageRepository.findBySessionIdOrderByCreatedAtDesc(eq("s1"), any(Pageable.class))).thenReturn(page);

        PageResponse<ChatMessageDto> result = handler.handle(new GetChatHistoryQuery("s1", "u1", 1, 20));

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getItems().size());
        assertEquals("Xin chào", result.getItems().get(0).getContent());
        assertEquals(List.of("p1"), result.getItems().get(0).getMetadata().getProductIds());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when session does not exist or belong to user")
    void shouldThrowWhenSessionNotFound() {
        when(chatSessionRepository.findByIdAndUserId("s1", "u1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> handler.handle(new GetChatHistoryQuery("s1", "u1", 1, 20)));
    }
}
