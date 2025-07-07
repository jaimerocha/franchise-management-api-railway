package com.retailchain.franchise.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Configuration
@Profile("railway")
public class SchemaInitializer {

    private final DatabaseClient databaseClient;
    
    public SchemaInitializer(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        log.info("🚀 Starting database schema initialization for Railway deployment...");
        log.info("Active profile: railway");
        
        // Wait for MySQL to be ready and create schema
        Mono.delay(Duration.ofSeconds(5))
            .then(createSchemaWithRetry())
            .subscribe(
                result -> log.info("✅ Database schema initialization completed successfully"),
                error -> log.error("❌ Database schema initialization failed after all retries: {}", error.getMessage())
            );
    }
    
    private Mono<Void> createSchemaWithRetry() {
        return testConnection()
            .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                .maxBackoff(Duration.ofSeconds(10))
                .doBeforeRetry(signal -> log.warn("⏳ MySQL not ready yet, retrying... (attempt {})", signal.totalRetries() + 1)))
            .then(createTables())
            .doOnSuccess(v -> log.info("✅ All tables created or verified successfully"))
            .doOnError(e -> log.error("❌ Failed to create tables: {}", e.getMessage()));
    }
    
    private Mono<Void> testConnection() {
        return databaseClient.sql("SELECT 1")
            .fetch()
            .one()
            .doOnSuccess(r -> log.debug("✅ MySQL connection successful"))
            .doOnError(e -> log.debug("❌ MySQL connection failed: {}", e.getMessage()))
            .then();
    }
    
    private Mono<Void> createTables() {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS franchises (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_franchise_name (name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS branches (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                franchise_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (franchise_id) REFERENCES franchises(id) ON DELETE CASCADE,
                INDEX idx_branch_franchise (franchise_id),
                INDEX idx_branch_name (name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS products (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(150) NOT NULL,
                stock INT NOT NULL DEFAULT 0,
                branch_id BIGINT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
                INDEX idx_product_branch (branch_id),
                INDEX idx_product_stock (stock),
                INDEX idx_product_name (name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """
        };
        
        return Flux.fromArray(createStatements)
            .concatMap(sql -> databaseClient.sql(sql)
                .then()
                .doOnSuccess(v -> log.debug("✅ Executed: {}", sql.substring(0, Math.min(50, sql.length())).replace("\n", " ")))
                .onErrorResume(error -> {
                    if (error.getMessage().contains("already exists")) {
                        log.debug("ℹ️ Table already exists, skipping...");
                        return Mono.empty();
                    }
                    return Mono.error(error);
                })
            )
            .then();
    }
}