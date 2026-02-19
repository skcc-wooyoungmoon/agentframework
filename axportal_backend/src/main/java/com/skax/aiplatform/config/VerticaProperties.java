package com.skax.aiplatform.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Vertica 데이터베이스 설정 Properties
 *
 * <p>application.yml의 vertica 속성을 바인딩하는 설정 클래스입니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.1.0
 * @since 2025-11-18
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "vertica")
public class VerticaProperties {
    private String host;
    private String port;
    private String database;
    private String sessionLabel;
    private Datasource datasource;
    private String dwSecretJsonPath;

    @Getter
    @Setter
    public static class Datasource {
        private String driverClassName;
        private String jdbcUrl;
        private String username;
        private String password;
        private HikariConfig hikari;
    }

    @Getter
    @Setter
    public static class HikariConfig {
        private Integer maximumPoolSize;
        private Integer minimumIdle;
        private Long connectionTimeout;
        private Long idleTimeout;
        private Long maxLifetime;
        private Boolean readOnly;
    }
}
