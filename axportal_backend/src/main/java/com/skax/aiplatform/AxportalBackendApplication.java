package com.skax.aiplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * AxportalBackend 메인 애플리케이션 클래스
 * 
 * <p>
 * Spring Boot 기반의 AI Portal RESTful API 서버입니다.
 * JWT 인증, 다중 데이터베이스 지원, OpenFeign 클라이언트 등을 포함합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
@EnableFeignClients(basePackages = { "com.skax.aiplatform.client" })
@ComponentScan(basePackages = { "com.skax.aiplatform" })
@EnableScheduling
public class AxportalBackendApplication {

    /**
     * 애플리케이션 시작점
     * 
     * @param args 커맨드 라인 인수
     */
    public static void main(String[] args) {
        SpringApplication.run(AxportalBackendApplication.class, args);
        log.info("✅ AX Portal API Application started successfully.");
    }
}