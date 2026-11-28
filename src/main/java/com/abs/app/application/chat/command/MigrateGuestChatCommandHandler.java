package com.abs.app.application.chat.command;

import com.abs.app.application.chat.dto.ChatSessionSummaryDto;
import com.abs.app.common.constant.UserConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
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
import com.abs.app.infrastructure.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrateGuestChatCommandHandler {

    private final ChatMemoryService chatMemoryService;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatSessionSummaryDto handle(MigrateGuestChatCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(UserConstant.USER_NOT_EXIST));

        List<GuestChatMessage> guestMessages = chatMemoryService.getAllMessages(command.getGuestSessionId());
        if (guestMessages == null || guestMessages.isEmpty()) {
            log.info("No guest messages found in Redis for session {}", command.getGuestSessionId());
            return null;
        }

        ChatSession savedSession = chatSessionRepository.save(ChatSession.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .title(extractTitle(guestMessages))
                .status(ChatSessionStatus.ACTIVE)
                .build());

        for (GuestChatMessage guestMsg : guestMessages) {
            chatMessageRepository.save(ChatMessage.builder()
                    .session(savedSession)
                    .senderType(guestMsg.getSenderType())
                    .content(guestMsg.getContent())
                    .metadata(guestMsg.getMetadataJson())
                    .createdAt(guestMsg.getCreatedAt() != null ? guestMsg.getCreatedAt() : LocalDateTime.now())
                    .build());
        }

        chatMemoryService.clearSession(command.getGuestSessionId());
        log.info("Successfully migrated {} messages from guestSession {} to MySQL session {}",
                guestMessages.size(), command.getGuestSessionId(), savedSession.getId());

        return ChatMapper.toChatSessionSummaryDto(savedSession);
    }

    private String extractTitle(List<GuestChatMessage> messages) {
        String title = messages.stream()
                .filter(m -> m.getSenderType() == ChatSenderType.USER)
                .findFirst()
                .map(GuestChatMessage::getContent)
                .orElse("Cuộc trò chuyện được chuyển từ khách");
        return title.length() > 50 ? title.substring(0, 47) + "..." : title;
    }
}
