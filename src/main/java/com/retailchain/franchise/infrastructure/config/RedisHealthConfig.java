package com.retailchain.franchise.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

@Slf4j
@Configuration
@Profile("railway")
public class RedisHealthConfig {
    
    @Bean
    HealthIndicator redisHealthIndicator(ReactiveRedisConnectionFactory connectionFactory) {
        return () -> {
            try {
                connectionFactory.getReactiveConnection()
                    .ping()
                    .block(java.time.Duration.ofSeconds(1));
                return Health.up()
                    .withDetail("redis", "Available")
                    .build();
            } catch (Exception e) {
                log.debug("Redis not available in Railway free tier - this is expected");
                return Health.up()
                    .withDetail("redis", "Not available (Railway free tier)")
                    .withDetail("impact", "Cache disabled, all features working")
                    .build();
            }
        };
    }
}