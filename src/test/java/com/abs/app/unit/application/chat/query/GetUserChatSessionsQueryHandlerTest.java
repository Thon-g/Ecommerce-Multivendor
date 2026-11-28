package com.abs.app.unit.application.chat.query;

import com.abs.app.application.chat.dto.ChatSessionSummaryDto;
import com.abs.app.application.chat.query.GetUserChatSessionsQuery;
import com.abs.app.application.chat.query.GetUserChatSessionsQueryHandler;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.entity.enums.ChatSessionStatus;
import com.abs.app.domain.repository.ChatSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserChatSessionsQueryHandlerTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @InjectMocks
    private GetUserChatSessionsQueryHandler handler;

    @Test
    @DisplayName("Should return user sessions ordered by updatedAt desc")
    void shouldReturnUserSessions() {
        ChatSession session = ChatSession.builder()
                .id("s1")
                .title("Tư vấn giày")
                .status(ChatSessionStatus.ACTIVE)
                .build();
        when(chatSessionRepository.findByUserIdOrderByUpdatedAtDesc("u1")).thenReturn(List.of(session));

        List<ChatSessionSummaryDto> result = handler.handle(new GetUserChatSessionsQuery("u1"));

        assertEquals(1, result.size());
        assertEquals("s1", result.get(0).getId());
        assertEquals("Tư vấn giày", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Should return empty list when userId is blank")
    void shouldReturnEmptyWhenUserIdBlank() {
        List<ChatSessionSummaryDto> result = handler.handle(new GetUserChatSessionsQuery("  "));
        assertTrue(result.isEmpty());
        verifyNoInteractions(chatSessionRepository);
    }
}
