package com.abs.app.domain.repository;

import com.abs.app.domain.entity.ChatSession;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository {
    ChatSession save(ChatSession chatSession);

    Optional<ChatSession> findById(String id);

    Optional<ChatSession> findByIdAndUserId(String id, String userId);

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);

    void delete(ChatSession chatSession);
}
