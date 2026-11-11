package com.abs.app.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockCacheService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> deductStockScript;

    private static final String STOCK_KEY_PREFIX = "product_sku:";
    private static final String STOCK_KEY_SUFFIX = ":stock";

    public long deductStock(Long skuId, int quantity, int dbStock) {
        String key = buildKey(skuId);

        try {
            if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.opsForValue().set(key, String.valueOf(dbStock));
                log.info("Lazy loaded stock for SKU {} into Redis: {}", skuId, dbStock);
            }

            Long result = redisTemplate.execute(
                    deductStockScript,
                    Collections.singletonList(key),
                    String.valueOf(quantity)
            );

            if (result == null) {
                log.warn("Lua script returned null for SKU {}. Falling back to DB.", skuId);
                return -1;
            }

            return result;

        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failed for SKU {}. Falling back to DB.", skuId, e);
            return -1;
        }
    }

    public void restoreStock(Long skuId, int quantity) {
        String key = buildKey(skuId);
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.opsForValue().increment(key, quantity);
                log.info("Restored {} stock for SKU {} on Redis.", quantity, skuId);
            }
        } catch (RedisConnectionFailureException e) {
            log.error("Failed to restore stock on Redis for SKU {}. Data may be inconsistent.", skuId, e);
        }
    }

    public void syncStock(Long skuId, int stock) {
        String key = buildKey(skuId);
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(stock));
            log.info("Synced stock for SKU {} on Redis: {}", skuId, stock);
        } catch (RedisConnectionFailureException e) {
            log.error("Failed to sync stock on Redis for SKU {}.", skuId, e);
        }
    }

    private String buildKey(Long skuId) {
        return STOCK_KEY_PREFIX + skuId + STOCK_KEY_SUFFIX;
    }
}
