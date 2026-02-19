package com.skax.aiplatform.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Primary DataSource 설정
 * 
 * <p>PostgreSQL/Tibero를 Primary DataSource로 명시적으로 정의합니다.
 * Vertica는 Secondary DataSource로 별도 구성됩니다.</p>
 * 
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-11-19
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * Primary DataSource Properties
     * 
     * @return DataSourceProperties
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Primary DataSource 생성
     * 
     * <p>Spring Boot의 기본 datasource 설정을 사용하여 Primary DataSource를 생성합니다.
     * JPA, MyBatis, 트랜잭션 관리 등 모든 기본 데이터베이스 작업에 사용됩니다.</p>
     * 
     * @param properties DataSource 설정 정보
     * @return Primary DataSource
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        log.info("🔧 Primary DataSource 초기화 시작");
        log.info("   - URL: {}", properties.getUrl());
        log.info("   - Username: {}", properties.getUsername());
        
        // DataSourceProperties의 initializeDataSourceBuilder()를 사용하여 안전하게 생성
        DataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
        
        // HikariCP 설정은 @ConfigurationProperties("spring.datasource.hikari")로 자동 적용
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.setPoolName("PrimaryHikariPool");
            log.info("   - Pool Name: PrimaryHikariPool");
        }
        
        log.info("✅ Primary DataSource 초기화 완료");
        return dataSource;
    }
}
