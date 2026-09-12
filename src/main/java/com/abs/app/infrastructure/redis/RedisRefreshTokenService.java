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

    private String buildKey(String userId, String familyId) {
        return "rt-family:" + userId + ":" + familyId;
    }

    private String buildPattern(String userId) {
        return "rt-family:" + userId + ":*";
    }

    @Override
    public void save(String userId, String familyId, String tokenId, long expirationMinutes) {
        redisTemplate.opsForValue().set(buildKey(userId, familyId), tokenId, expirationMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String get(String userId, String familyId) {
        return redisTemplate.opsForValue().get(buildKey(userId, familyId));
    }

    @Override
    public void invalidateFamily(String userId, String familyId) {
        redisTemplate.delete(buildKey(userId, familyId));
    }

    @Override
    public void invalidateAll(String userId) {
        var keys = redisTemplate.keys(buildPattern(userId));
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public boolean rotate(String userId, String familyId, String oldTokenId, String newTokenId, long expirationMinutes) {
        String key = buildKey(userId, familyId);
        String currentTokenId = redisTemplate.opsForValue().get(key);

        if (currentTokenId == null) {
            // Key expired or deleted
            return false;
        }

        if (!currentTokenId.equals(oldTokenId)) {
            // REUSE DETECTED!
            // The token sent by the user does not match the latest valid token in this family.
            // Invalidate the entire family to protect the user.
            redisTemplate.delete(key);
            return false;
        }

        // Token matches, proceed with rotation
        redisTemplate.opsForValue().set(key, newTokenId, expirationMinutes, TimeUnit.MINUTES);
        return true;
    }
}
