package com.abs.app.infrastructure.persistence.adapter;

import com.abs.app.domain.entity.ChatMessage;
import com.abs.app.domain.repository.ChatMessageRepository;
import com.abs.app.infrastructure.persistence.jpa.ChatMessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageJpaRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId) {
        return chatMessageJpaRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Override
    public Page<ChatMessage> findBySessionIdOrderByCreatedAtDesc(String sessionId, Pageable pageable) {
        return chatMessageJpaRepository.findBySessionIdOrderByCreatedAtDesc(sessionId, pageable);
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        chatMessageJpaRepository.deleteBySessionId(sessionId);
    }
}
