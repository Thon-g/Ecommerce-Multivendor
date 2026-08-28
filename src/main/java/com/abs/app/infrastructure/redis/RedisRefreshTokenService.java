package com.abs.app.infrastructure.redis;

import com.abs.app.domain.service.RefreshTokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisRefreshTokenService implements RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    public RedisRefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildKey(String userId) {
        return "refresh-token:" + userId;
    }

    @Override
    public void save(String userId, String refreshToken, long expirationMinutes) {
        redisTemplate.opsForValue().set(buildKey(userId), refreshToken, expirationMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String get(String userId) {
        return redisTemplate.opsForValue().get(buildKey(userId));
    }

    @Override
    public void invalidate(String userId) {
        redisTemplate.delete(buildKey(userId));
    }

    @Override
    public boolean isValid(String userId, String refreshToken) {
        String saved = get(userId);
        return refreshToken != null && refreshToken.equals(saved);
    }
}
