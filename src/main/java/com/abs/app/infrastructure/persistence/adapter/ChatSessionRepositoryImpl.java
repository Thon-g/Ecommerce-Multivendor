package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.ChatSession;
import com.abs.app.domain.repository.ChatSessionRepository;
import com.abs.app.infrastructure.persistence.jpa.ChatSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final ChatSessionJpaRepository chatSessionJpaRepository;

    @Override
    public ChatSession save(ChatSession chatSession) {
        return chatSessionJpaRepository.save(chatSession);
    }

    @Override
    public Optional<ChatSession> findById(String id) {
        return chatSessionJpaRepository.findById(id);
    }

    @Override
    public Optional<ChatSession> findByIdAndUserId(String id, String userId) {
        return chatSessionJpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId) {
        return chatSessionJpaRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Override
    public void delete(ChatSession chatSession) {
        chatSessionJpaRepository.delete(chatSession);
    }
}
