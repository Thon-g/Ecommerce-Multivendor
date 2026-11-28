package com.abs.app.application.chat.command;

import com.abs.app.common.constant.ChatConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.repository.ChatMessageRepository;
import com.abs.app.domain.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteChatSessionCommandHandler {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void handle(DeleteChatSessionCommand command) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(command.getSessionId(), command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ChatConstant.CHAT_SESSION_NOT_FOUND));

        chatMessageRepository.deleteBySessionId(session.getId());
        chatSessionRepository.delete(session);
    }
}
