package org.example.accessbasededonnees.util;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
public class RedisConnection {
    private final RedisTemplate<String, String> revokedTokenRedisTemplate;
    private final RedisTemplate<String, String> activeTokenRedisTemplate;

    public RedisConnection(
            @Qualifier("activeTokenRedisTemplate")RedisTemplate<String, String> activeTokenRedisTemplate,
            @Qualifier("revokeTokenRedisTemplate")RedisTemplate<String, String> revokedTokenRedisTemplate){
        this.activeTokenRedisTemplate = activeTokenRedisTemplate;
        this.revokedTokenRedisTemplate = revokedTokenRedisTemplate;
    }

    public RedisTemplate<String, String> getActiveTokensRedis() {
        return activeTokenRedisTemplate;
    }

    public RedisTemplate<String, String> getExpiredTokensRedis() {
        return revokedTokenRedisTemplate;
    }



}
