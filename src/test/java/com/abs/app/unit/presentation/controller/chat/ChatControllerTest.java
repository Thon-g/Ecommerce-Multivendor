package com.abs.app.unit.presentation.controller.chat;

import com.abs.app.application.chat.command.DeleteChatSessionCommand;
import com.abs.app.application.chat.command.DeleteChatSessionCommandHandler;
import com.abs.app.application.chat.command.MigrateGuestChatCommand;
import com.abs.app.application.chat.command.MigrateGuestChatCommandHandler;
import com.abs.app.application.chat.dto.*;
import com.abs.app.application.chat.query.*;
import com.abs.app.common.constant.ChatConstant;
import com.abs.app.common.response.ApiResponse;
import com.abs.app.common.response.PageResponse;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.ChatSessionStatus;
import com.abs.app.domain.entity.enums.UserStatus;
import com.abs.app.infrastructure.security.CustomUserPrincipal;
import com.abs.app.presentation.controller.chat.ChatController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private AskChatbotQueryHandler askChatbotQueryHandler;

    @Mock
    private GetUserChatSessionsQueryHandler getUserChatSessionsQueryHandler;

    @Mock
    private GetChatHistoryQueryHandler getChatHistoryQueryHandler;

    @Mock
    private DeleteChatSessionCommandHandler deleteChatSessionCommandHandler;

    @Mock
    private MigrateGuestChatCommandHandler migrateGuestChatCommandHandler;

    @InjectMocks
    private ChatController chatController;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /chat handles unauthenticated guest without userId")
    void shouldHandleGuestChatRequest() {
        SecurityContextHolder.clearContext();
        ChatRequestDto request = ChatRequestDto.builder()
                .message("Tư vấn áo thun")
                .guestSessionId("guest-123")
                .build();

        ChatResponseDto expected = ChatResponseDto.builder()
                .conversationId("guest-123")
                .reply("Dưới đây là các mẫu áo thun.")
                .build();
        when(askChatbotQueryHandler.handle(any(AskChatbotQuery.class))).thenReturn(expected);

        ResponseEntity<ApiResponse<ChatResponseDto>> response = chatController.askChatbot(request);

        assertTrue(response.getBody().isSuccess());
        assertEquals(ChatConstant.CHAT_SUCCESS, response.getBody().getMessage());
        assertEquals("guest-123", response.getBody().getData().getConversationId());

        ArgumentCaptor<AskChatbotQuery> captor = ArgumentCaptor.forClass(AskChatbotQuery.class);
        verify(askChatbotQueryHandler).handle(captor.capture());
        assertNull(captor.getValue().getUserId());
        assertEquals("guest-123", captor.getValue().getGuestSessionId());
    }

    @Test
    @DisplayName("POST /chat extracts userId when authenticated via CustomUserPrincipal")
    void shouldHandleAuthenticatedChatRequest() {
        setAuthenticatedUser("user-001");

        ChatRequestDto request = ChatRequestDto.builder()
                .message("Tư vấn laptop")
                .conversationId("session-1")
                .build();

        ChatResponseDto expected = ChatResponseDto.builder()
                .conversationId("session-1")
                .reply("Có nhiều mẫu laptop phù hợp.")
                .build();
        when(askChatbotQueryHandler.handle(any(AskChatbotQuery.class))).thenReturn(expected);

        ResponseEntity<ApiResponse<ChatResponseDto>> response = chatController.askChatbot(request);

        assertTrue(response.getBody().isSuccess());
        ArgumentCaptor<AskChatbotQuery> captor = ArgumentCaptor.forClass(AskChatbotQuery.class);
        verify(askChatbotQueryHandler).handle(captor.capture());
        assertEquals("user-001", captor.getValue().getUserId());
        assertEquals("session-1", captor.getValue().getConversationId());
    }

    @Test
    @DisplayName("GET /chat/sessions returns sessions for current authenticated user")
    void shouldGetUserSessions() {
        setAuthenticatedUser("user-001");
        List<ChatSessionSummaryDto> summaries = List.of(
                ChatSessionSummaryDto.builder().id("s1").title("Áo polo").status(ChatSessionStatus.ACTIVE).build()
        );
        when(getUserChatSessionsQueryHandler.handle(any(GetUserChatSessionsQuery.class))).thenReturn(summaries);

        ResponseEntity<ApiResponse<List<ChatSessionSummaryDto>>> response = chatController.getSessions();

        assertTrue(response.getBody().isSuccess());
        assertEquals(ChatConstant.CHAT_SESSIONS_FETCHED_SUCCESS, response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    @DisplayName("GET /chat/sessions/{id}/messages delegates pagination parameters properly")
    void shouldGetSessionMessages() {
        setAuthenticatedUser("user-001");
        PageResponse<ChatMessageDto> pageResponse = new PageResponse<>(List.of(), 0, 1, 20);
        when(getChatHistoryQueryHandler.handle(any(GetChatHistoryQuery.class))).thenReturn(pageResponse);

        ResponseEntity<ApiResponse<PageResponse<ChatMessageDto>>> response = chatController.getSessionMessages("s1", 1, 20);

        assertTrue(response.getBody().isSuccess());
        assertEquals(ChatConstant.CHAT_HISTORY_FETCHED_SUCCESS, response.getBody().getMessage());
    }

    @Test
    @DisplayName("DELETE /chat/sessions/{id} deletes session for current user")
    void shouldDeleteSession() {
        setAuthenticatedUser("user-001");
        doNothing().when(deleteChatSessionCommandHandler).handle(any(DeleteChatSessionCommand.class));

        ResponseEntity<ApiResponse<Void>> response = chatController.deleteSession("s1");

        assertTrue(response.getBody().isSuccess());
        assertEquals(ChatConstant.CHAT_SESSION_DELETED_SUCCESS, response.getBody().getMessage());
    }

    @Test
    @DisplayName("POST /chat/migrate returns success when guest messages migrated")
    void shouldMigrateGuestSession() {
        setAuthenticatedUser("user-001");
        ChatSessionSummaryDto migrated = ChatSessionSummaryDto.builder().id("new-s1").title("Chat").build();
        when(migrateGuestChatCommandHandler.handle(any(MigrateGuestChatCommand.class))).thenReturn(migrated);

        ResponseEntity<ApiResponse<ChatSessionSummaryDto>> response = chatController.migrateGuestSession(
                new GuestMigrationRequestDto("guest-123"));

        assertTrue(response.getBody().isSuccess());
        assertEquals(ChatConstant.CHAT_MIGRATION_SUCCESS, response.getBody().getMessage());
        assertEquals("new-s1", response.getBody().getData().getId());
    }

    @Test
    @DisplayName("POST /chat/migrate returns not found message when guest session expired or empty")
    void shouldHandleEmptyMigration() {
        setAuthenticatedUser("user-001");
        when(migrateGuestChatCommandHandler.handle(any(MigrateGuestChatCommand.class))).thenReturn(null);

        ResponseEntity<ApiResponse<ChatSessionSummaryDto>> response = chatController.migrateGuestSession(
                new GuestMigrationRequestDto("guest-empty"));

        assertTrue(response.getBody().isSuccess());
        assertEquals(ChatConstant.GUEST_SESSION_NOT_FOUND, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    private void setAuthenticatedUser(String userId) {
        User user = new User();
        user.setUserId(userId);
        user.setEmail("user@test.com");
        user.setPassword("secret");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of());
        CustomUserPrincipal principal = new CustomUserPrincipal(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
