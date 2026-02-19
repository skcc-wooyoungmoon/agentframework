package com.skax.aiplatform.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Vertica DataSource 설정
 * 
 * <p>Secondary DataSource로 Vertica를 구성합니다.
 * JdbcTemplate만 사용하며, JPA/MyBatis에서는 제외됩니다.</p>
 * 
 * <p>⚠️ 중요: dev, elocal 프로파일에서만 Bean이 생성됩니다.</p>
 * 
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-11-19
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "vertica.datasource",
    name = "jdbc-url"
)
public class VerticaDataSourceConfig {

    private final VerticaProperties verticaProperties;

    /**
     * Vertica DataSource 생성
     * 
     * <p>⚠️ 중요: Spring Boot의 자동 데이터베이스 초기화(data.sql)에서 제외됩니다.
     * DataSourceProperties를 사용하지 않고 수동으로 생성하여 초기화 스크립트 실행을 방지합니다.</p>
     * 
     * @return Vertica DataSource
     */
    @Bean(name = "verticaDataSource")
    @ConfigurationPropertiesBinding
    public DataSource verticaDataSource() {
        log.info("🔧 Vertica DataSource 초기화 시작");
        log.info("   - Host: {}", verticaProperties.getHost());
        log.info("   - Port: {}", verticaProperties.getPort());
        log.info("   - Database: {}", verticaProperties.getDatabase());
        log.info("   - Session Label: {}", verticaProperties.getSessionLabel());

        HikariConfig config = new HikariConfig();
        
        // 기본 연결 정보
        config.setJdbcUrl(verticaProperties.getDatasource().getJdbcUrl());
        config.setUsername(verticaProperties.getDatasource().getUsername());
        config.setPassword(verticaProperties.getDatasource().getPassword());
        config.setDriverClassName(verticaProperties.getDatasource().getDriverClassName());
        
        // HikariCP 설정
        VerticaProperties.HikariConfig hikariProps = verticaProperties.getDatasource().getHikari();
        config.setMaximumPoolSize(hikariProps.getMaximumPoolSize());
        config.setMinimumIdle(hikariProps.getMinimumIdle());
        config.setConnectionTimeout(hikariProps.getConnectionTimeout());
        config.setIdleTimeout(hikariProps.getIdleTimeout());
        config.setMaxLifetime(hikariProps.getMaxLifetime());
        config.setReadOnly(hikariProps.getReadOnly());
        
        // 🔧 데이터베이스 초기화 비활성화 (중요!)
        config.setAutoCommit(true);
        config.setPoolName("VerticaHikariPool");
        
        // 🔧 초기화 스크립트 실행 방지
        config.addDataSourceProperty("initializationFailTimeout", "-1");
        
        log.info("✅ Vertica DataSource 초기화 완료");
        return new HikariDataSource(config);
    }

    /**
     * Vertica JdbcTemplate 생성
     * 
     * @param verticaDataSource Vertica DataSource
     * @return Vertica JdbcTemplate
     */
    @Bean(name = "verticaJdbcTemplate")
    @SuppressWarnings("null")
    public JdbcTemplate verticaJdbcTemplate(@Qualifier("verticaDataSource") DataSource verticaDataSource) {
        log.info("🔧 Vertica JdbcTemplate 초기화");
        log.info("   - DataSource: {}", verticaDataSource.getClass().getSimpleName());
        if (verticaDataSource instanceof HikariDataSource hikari) {
            log.info("   - JDBC URL: {}", hikari.getJdbcUrl());
        }
        log.info("✅ Vertica JdbcTemplate 초기화 완료");
        return new JdbcTemplate(verticaDataSource);
    }
}