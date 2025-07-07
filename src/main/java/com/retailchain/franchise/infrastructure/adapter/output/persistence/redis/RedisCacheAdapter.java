package com.retailchain.franchise.infrastructure.adapter.output.persistence.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailchain.franchise.domain.port.output.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Slf4j
@Component
public class RedisCacheAdapter implements CacheRepository {
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // Constructor con @Qualifier
    public RedisCacheAdapter(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate, 
                            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public <T> Mono<T> get(String key, Class<T> type) {
        return redisTemplate.opsForValue().get(key)
                .flatMap(value -> {
                    try {
                        T result = objectMapper.readValue(value, type);
                        return Mono.just(result);
                    } catch (Exception e) {
                        log.error("Error deserializing cached value for key: {}", key, e);
                        return Mono.empty();
                    }
                })
                .doOnNext(value -> log.debug("Cache hit for key: {}", key))
                .doOnSubscribe(s -> log.debug("Getting value from cache for key: {}", key));
    }
    
    @Override
    public <T> Mono<Void> set(String key, T value, Duration ttl) {
        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.writeValueAsString(value);
                    } catch (Exception e) {
                        throw new RuntimeException("Error serializing value for cache", e);
                    }
                })
                .flatMap(json -> redisTemplate.opsForValue().set(key, json, ttl))
                .then()
                .doOnSuccess(v -> log.debug("Cached value for key: {} with TTL: {}", key, ttl));
    }
    
    @Override
    public Mono<Void> delete(String key) {
        return redisTemplate.delete(key)
                .then()
                .doOnSuccess(v -> log.debug("Deleted cache entry for key: {}", key));
    }
}