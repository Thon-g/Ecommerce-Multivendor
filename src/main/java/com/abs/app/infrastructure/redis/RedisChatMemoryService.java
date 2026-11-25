package com.abs.app.infrastructure.redis;

import com.abs.app.application.dto.chat.ChatMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisChatMemoryService {

    private static final String GUEST_CHAT_KEY_PREFIX = "chat:guest:";
    private static final long GUEST_SESSION_TTL_HOURS = 24L;
    private static final long MAX_STORED_MESSAGES = 20L;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemoryService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveMessage(String guestSessionId, ChatMessageDto messageDto) {
        if (guestSessionId == null || guestSessionId.isBlank() || messageDto == null) {
            return;
        }

        String key = buildKey(guestSessionId);
        try {
            String json = objectMapper.writeValueAsString(messageDto);
            redisTemplate.opsForList().rightPush(key, json);
            redisTemplate.expire(key, GUEST_SESSION_TTL_HOURS, TimeUnit.HOURS);
            redisTemplate.opsForList().trim(key, -MAX_STORED_MESSAGES, -1);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ChatMessageDto for guestSessionId {}: {}", guestSessionId, e.getMessage());
        } catch (Exception e) {
            log.error("Redis error saving guest message for {}: {}", guestSessionId, e.getMessage());
        }
    }

    public List<ChatMessageDto> getRecentMessages(String guestSessionId, int limit) {
        if (guestSessionId == null || guestSessionId.isBlank()) {
            return Collections.emptyList();
        }

        String key = buildKey(guestSessionId);
        try {
            int fetchLimit = limit > 0 ? limit : 6;
            List<String> rawMessages = redisTemplate.opsForList().range(key, -fetchLimit, -1);
            return deserializeList(rawMessages);
        } catch (Exception e) {
            log.error("Redis error fetching recent messages for {}: {}", guestSessionId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ChatMessageDto> getAllMessages(String guestSessionId) {
        if (guestSessionId == null || guestSessionId.isBlank()) {
            return Collections.emptyList();
        }

        String key = buildKey(guestSessionId);
        try {
            List<String> rawMessages = redisTemplate.opsForList().range(key, 0, -1);
            return deserializeList(rawMessages);
        } catch (Exception e) {
            log.error("Redis error fetching all messages for {}: {}", guestSessionId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public void clearSession(String guestSessionId) {
        if (guestSessionId == null || guestSessionId.isBlank()) {
            return;
        }
        try {
            redisTemplate.delete(buildKey(guestSessionId));
        } catch (Exception e) {
            log.error("Redis error clearing session for {}: {}", guestSessionId, e.getMessage());
        }
    }

    private List<ChatMessageDto> deserializeList(List<String> rawList) {
        if (rawList == null || rawList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChatMessageDto> result = new ArrayList<>();
        for (String raw : rawList) {
            try {
                ChatMessageDto dto = objectMapper.readValue(raw, ChatMessageDto.class);
                result.add(dto);
            } catch (JsonProcessingException e) {
                log.warn("Failed to deserialize ChatMessageDto from Redis: {}", e.getMessage());
            }
        }
        return result;
    }

    private String buildKey(String guestSessionId) {
        return GUEST_CHAT_KEY_PREFIX + guestSessionId.trim();
    }
}
