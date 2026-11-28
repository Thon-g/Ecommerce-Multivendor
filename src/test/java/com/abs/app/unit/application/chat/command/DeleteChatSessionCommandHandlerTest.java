package com.abs.app.unit.application.chat.command;

import com.abs.app.application.chat.command.DeleteChatSessionCommand;
import com.abs.app.application.chat.command.DeleteChatSessionCommandHandler;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.repository.ChatMessageRepository;
import com.abs.app.domain.repository.ChatSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteChatSessionCommandHandlerTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private DeleteChatSessionCommandHandler handler;

    @Test
    @DisplayName("Should delete messages and session when session belongs to user")
    void shouldDeleteSessionAndMessages() {
        ChatSession session = ChatSession.builder().id("s1").build();
        when(chatSessionRepository.findByIdAndUserId("s1", "u1")).thenReturn(Optional.of(session));

        handler.handle(new DeleteChatSessionCommand("s1", "u1"));

        verify(chatMessageRepository).deleteBySessionId("s1");
        verify(chatSessionRepository).delete(session);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when session not found")
    void shouldThrowWhenNotFound() {
        when(chatSessionRepository.findByIdAndUserId("s1", "u1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> handler.handle(new DeleteChatSessionCommand("s1", "u1")));
        verifyNoInteractions(chatMessageRepository);
    }
}
