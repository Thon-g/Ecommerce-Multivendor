package com.abs.app.application.chat.query;

import com.abs.app.application.chat.dto.ChatSessionSummaryDto;
import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.repository.ChatSessionRepository;
import com.abs.app.infrastructure.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserChatSessionsQueryHandler {

    private final ChatSessionRepository chatSessionRepository;

    @Transactional(readOnly = true)
    public List<ChatSessionSummaryDto> handle(GetUserChatSessionsQuery query) {
        if (query.getUserId() == null || query.getUserId().isBlank()) {
            return Collections.emptyList();
        }

        List<ChatSession> sessions = chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(query.getUserId());
        return sessions.stream()
                .map(ChatMapper::toChatSessionSummaryDto)
                .collect(Collectors.toList());
    }
}
