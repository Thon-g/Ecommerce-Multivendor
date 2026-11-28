package com.abs.app.application.chat.query;

import com.abs.app.application.chat.dto.ChatMessageDto;
import com.abs.app.common.constant.ChatConstant;
import com.abs.app.common.exception.ResourceNotFoundException;
import com.abs.app.common.response.PageResponse;
import com.abs.app.common.util.PaginationUtil;
import com.abs.app.domain.entity.ChatMessage;
import com.abs.app.domain.repository.ChatMessageRepository;
import com.abs.app.domain.repository.ChatSessionRepository;
import com.abs.app.infrastructure.mapper.ChatMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetChatHistoryQueryHandler {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public PageResponse<ChatMessageDto> handle(GetChatHistoryQuery query) {
        chatSessionRepository.findByIdAndUserId(query.getSessionId(), query.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ChatConstant.CHAT_SESSION_NOT_FOUND));

        Pageable pageable = PaginationUtil.createPageable(query.getPage(), query.getLimit());
        Page<ChatMessage> messagePage = chatMessageRepository.findBySessionIdOrderByCreatedAtDesc(
                query.getSessionId(),
                pageable
        );

        return PaginationUtil.toPageResponse(
                messagePage,
                msg -> ChatMapper.toChatMessageDto(msg, objectMapper),
                query.getPage(),
                query.getLimit()
        );
    }
}
