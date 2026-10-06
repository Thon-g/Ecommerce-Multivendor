package com.abs.app.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public DefaultRedisScript<Long> deductStockScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(
                "local stock = tonumber(redis.call('GET', KEYS[1])) " +
                "if stock == nil then return -1 end " +
                "local qty = tonumber(ARGV[1]) " +
                "if stock >= qty then " +
                "  redis.call('DECRBY', KEYS[1], qty) " +
                "  return 1 " +
                "end " +
                "return 0"
        );
        script.setResultType(Long.class);
        return script;
    }
}
