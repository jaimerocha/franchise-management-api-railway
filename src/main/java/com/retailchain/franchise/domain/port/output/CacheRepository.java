package com.retailchain.franchise.domain.port.output;

import reactor.core.publisher.Mono;
import java.time.Duration;

public interface CacheRepository {
    <T> Mono<T> get(String key, Class<T> type);
    <T> Mono<Void> set(String key, T value, Duration ttl);
    Mono<Void> delete(String key);
}