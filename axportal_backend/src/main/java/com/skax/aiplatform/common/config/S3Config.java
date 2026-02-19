package com.skax.aiplatform.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * S3 설정 정보를 관리하는 Configuration 클래스
 * 
 * <p>
 * s3 설정을 읽어와서 Bean으로 등록합니다.
 * </p>
 * 
 * @author 장지원
 * @since 2025-10-27
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Config {

    /**
     * S3 버킷 이름
     */
    private String bucketName;

    /**
     * AWS 액세스 키
     */
    private String accessKey;

    /**
     * AWS 시크릿 키
     */
    private String secretKey;

    /**
     * AWS 리전
     */
    private String region;

    /**
     * S3 엔드포인트
     */
    private String endpoint;

    /**
     * Path Style Access 사용 여부
     */
    private boolean pathStyleAccess = true;
}
