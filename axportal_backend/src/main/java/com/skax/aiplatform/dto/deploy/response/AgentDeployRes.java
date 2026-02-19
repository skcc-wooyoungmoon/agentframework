package com.skax.aiplatform.dto.deploy.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 배포 응답 DTO
 * 
 * <p>AppDeploymentResponse와 일치하는 구조로 외부 API 응답을 내부 응답으로 변환합니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-30
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent 배포 응답")
public class AgentDeployRes {
    
    /**
     * 앱 ID
     */
    private String appId;

    /**
     * 설명
     */
    private String description;
    
    /**
     * 서빙 타입
     */
    private String servingType;
    
    /**
     * 이미지 태그
     */
    private String imageTag;
    
    /**
     * 입력 키 목록
     */
    private List<InputKey> inputKeys;
    
    /**
     * 출력 타입
     */
    private String outputType;
    
    /**
     * 배포 일시
     */
    private String deployedDt;
    
    /**
     * 생성자
     */
    private String createdBy;
    
    /**
     * 서빙 ID
     */
    private String servingId;
    
    /**
     * 타겟 ID
     */
    private String targetId;
    
    /**
     * 배포 ID
     */
    private String id;
    
    /**
     * 타겟 타입
     */
    private String targetType;
    
    /**
     * 버전
     */
    private Integer version;
    
    /**
     * 상태
     */
    private String status;
    
    /**
     * 출력 키 목록
     */
    private List<OutputKey> outputKeys;
    
    /**
     * 배포 설정 경로
     */
    private String deploymentConfigPath;
    
    /**
     * 삭제 여부
     */
    private Boolean deleteFlag;
    
    /**
     * 엔드포인트
     */
    private String endpoint;
    
    /**
     * 운영 이행 활성 여부 (GpoMigMas에 등록된 배포인지)
     */
    private Boolean isMigration;
    
    /**
     * 입력 키 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InputKey {
        /**
         * 키 이름
         */
        private String name;
        
        /**
         * 필수 여부
         */
        private Boolean required;
        
        /**
         * 키테이블 ID
         */
        private String keytableId;
    }
    
    /**
     * 출력 키 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OutputKey {
        /**
         * 키 이름
         */
        private String name;
        
        /**
         * 키테이블 ID
         */
        private String keytableId;
    }
}