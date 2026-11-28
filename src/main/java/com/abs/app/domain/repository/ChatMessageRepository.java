package com.abs.app.domain.repository;

import com.abs.app.domain.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    Page<ChatMessage> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable);

    void deleteBySessionId(String sessionId);
}
