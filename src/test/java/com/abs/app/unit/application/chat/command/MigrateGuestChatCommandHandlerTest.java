package com.abs.app.unit.application.chat.command;

import com.abs.app.application.chat.command.MigrateGuestChatCommand;
import com.abs.app.application.chat.command.MigrateGuestChatCommandHandler;
import com.abs.app.application.chat.dto.ChatSessionSummaryDto;
import com.abs.app.domain.entity.ChatMessage;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.entity.User;
import com.abs.app.domain.entity.enums.ChatSenderType;
import com.abs.app.domain.entity.enums.ChatSessionStatus;
import com.abs.app.domain.model.GuestChatMessage;
import com.abs.app.domain.repository.ChatMessageRepository;
import com.abs.app.domain.repository.ChatSessionRepository;
import com.abs.app.domain.repository.UserRepository;
import com.abs.app.domain.service.ChatMemoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MigrateGuestChatCommandHandlerTest {

    @Mock
    private ChatMemoryService chatMemoryService;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    private MigrateGuestChatCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new MigrateGuestChatCommandHandler(
                chatMemoryService,
                chatSessionRepository,
                chatMessageRepository,
                userRepository
        );
    }

    @Test
    @DisplayName("Should migrate guest messages to MySQL and clear temporary memory")
    void shouldMigrateGuestMessagesSuccessfully() {
        String userId = "user-abc";
        String guestSessionId = "guest-xyz";

        User user = new User();
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        GuestChatMessage msg1 = GuestChatMessage.builder()
                .senderType(ChatSenderType.USER)
                .content("Tìm giày sneaker nam")
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .build();

        GuestChatMessage msg2 = GuestChatMessage.builder()
                .senderType(ChatSenderType.BOT)
                .content("Dưới đây là một số mẫu giày")
                .metadataJson("{\"productIds\":[\"prod-shoe-1\"]}")
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(chatMemoryService.getAllMessages(guestSessionId)).thenReturn(List.of(msg1, msg2));

        ChatSession savedSession = ChatSession.builder()
                .id("session-migrated-123")
                .user(user)
                .title("Tìm giày sneaker nam")
                .status(ChatSessionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(savedSession);

        MigrateGuestChatCommand command = MigrateGuestChatCommand.builder()
                .userId(userId)
                .guestSessionId(guestSessionId)
                .build();

        ChatSessionSummaryDto summary = handler.handle(command);

        assertNotNull(summary);
        assertEquals("session-migrated-123", summary.getId());
        assertEquals("Tìm giày sneaker nam", summary.getTitle());

        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        verify(chatMemoryService).clearSession(guestSessionId);
    }

    @Test
    @DisplayName("Should return null when guest session has no messages")
    void shouldReturnNullWhenGuestSessionEmpty() {
        String userId = "user-abc";
        String guestSessionId = "guest-empty";

        User user = new User();
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatMemoryService.getAllMessages(guestSessionId)).thenReturn(Collections.emptyList());

        MigrateGuestChatCommand command = MigrateGuestChatCommand.builder()
                .userId(userId)
                .guestSessionId(guestSessionId)
                .build();

        ChatSessionSummaryDto summary = handler.handle(command);

        assertNull(summary);
        verifyNoInteractions(chatSessionRepository);
        verifyNoInteractions(chatMessageRepository);
    }
}
