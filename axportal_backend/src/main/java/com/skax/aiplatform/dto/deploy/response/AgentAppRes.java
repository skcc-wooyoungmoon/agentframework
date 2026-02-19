package com.skax.aiplatform.dto.deploy.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Agent 배포 응답 DTO
 * 
 * <p>AppResponse와 일치하는 구조로 외부 API 응답을 내부 응답으로 변환합니다.</p>
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
public class AgentAppRes {
    
    /**
     * 애플리케이션 고유 식별자
     */
    private String id;
    
    /**
     * 애플리케이션 이름
     */
    private String name;

    /**
     * 그래프 이름
     */
    private String builderName;
    
    /**
     * 애플리케이션 설명
     */
    private String description;
    
    /**
     * 타겟 ID
     */
    private String targetId;

    /**
     * 생성 시간
     */
    private String createdAt;
    
    /**
     * 마지막 수정 시간
     */
    private String updatedAt;
    
    /**
     * 생성자 ID
     */
    private String createdBy;
    
    /**
     * 마지막 수정자 ID
     */
    private String updatedBy;
    
    /**
     * 배포 정보 목록
     */
    private List<DeploymentInfo> deployments;
    
    /**
     * 배포 버전
     */
    private Integer deploymentVersion;
    
    /**
     * 배포 상태
     */
    private String deploymentStatus;
    
    /**
     * 서빙 타입
     */
    private String servingType;
    
    /**
     * 입력 키 목록
     */
    private List<InputKey> inputKeys;
    
    /**
     * 출력 키 목록
     */
    private List<OutputKey> outputKeys;
    
    /**
     * 출력 타입
     */
    private String outputType;

    /**
     * 마이그레이션 여부
     */
    private Boolean isMigration;


    /**
     * 공개범위
     */
    private String publicStatus;
    
    /**
     * 페이로드 정보
     */
    private Object payload;
    
    /**
     * 배포 정보 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "배포 정보")
    public static class DeploymentInfo {
        
        /**
         * 배포 설명
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
         * 배포 시간
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
    }
    
    /**
     * 입력 키 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "입력/출력 키 정보")
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

        /**
         * 키 설명
         */
        private String description;

        /**
         * 고정 값
         */
        private String fixedValue;
    }

    /**
     * 출력 키 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "입력/출력 키 정보")
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