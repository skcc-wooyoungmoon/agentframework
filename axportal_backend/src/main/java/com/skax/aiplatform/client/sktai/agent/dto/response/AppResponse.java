package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent App 상세 정보 응답 DTO
 * 
 * <p>Agent 애플리케이션의 상세 정보를 담는 응답 데이터 구조입니다.
 * 애플리케이션의 모든 메타데이터, 배포 정보, 설정 정보를 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 상세 정보 응답")
public class AppResponse {
    
    @JsonProperty("id")
    @Schema(description = "애플리케이션 고유 식별자")
    private String id;
    
    @JsonProperty("name")
    @Schema(description = "애플리케이션 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "애플리케이션 설명")
    private String description;
    
    @JsonProperty("target_id")
    @Schema(description = "타겟 ID")
    private String targetId;
    
    @JsonProperty("target_name")
    @Schema(description = "타겟 이름")
    private String targetName;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "마지막 수정 시간")
    private String updatedAt;
    
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;
    
    @JsonProperty("updated_by")
    @Schema(description = "마지막 수정자")
    private String updatedBy;
    
    @JsonProperty("deployments")
    @Schema(description = "배포 정보 목록")
    private List<DeploymentInfo> deployments;
    
    @JsonProperty("deployment_version")
    @Schema(description = "배포 버전")
    private Integer deploymentVersion;
    
    @JsonProperty("deployment_status")
    @Schema(description = "배포 상태")
    private String deploymentStatus;
    
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입")
    private String servingType;
    
    @JsonProperty("input_keys")
    @Schema(description = "입력 키 목록")
    private List<InputKey> inputKeys;
    
    @JsonProperty("output_keys")
    @Schema(description = "출력 키 목록")
    private List<OutputKey> outputKeys;
    
    @JsonProperty("output_type")
    @Schema(description = "출력 타입")
    private String outputType;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Object payload;
    
    /**
     * 배포 정보 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "배포 정보")
    public static class DeploymentInfo {
        
        @JsonProperty("description")
        @Schema(description = "배포 설명")
        private String description;
                
        @JsonProperty("serving_type")
        @Schema(description = "서빙 타입")
        private String servingType;

        @JsonProperty("image_tag")
        @Schema(description = "이미지 태그")
        private String imageTag;
                
        @JsonProperty("input_keys")
        @Schema(description = "입력 키 목록")
        private List<InputKey> inputKeys;
                
        @JsonProperty("output_type")
        @Schema(description = "출력 타입")
        private String outputType;

        @JsonProperty("deployed_dt")
        @Schema(description = "배포 시간")
        private String deployedDt;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;
        
        @JsonProperty("serving_id")
        @Schema(description = "서빙 ID")
        private String servingId;

        @JsonProperty("target_id")
        @Schema(description = "타겟 ID")
        private String targetId;

        @JsonProperty("id")
        @Schema(description = "배포 ID")
        private String id;

        @JsonProperty("target_type")
        @Schema(description = "타겟 타입")
        private String targetType;
        
        @JsonProperty("version")
        @Schema(description = "버전")
        private Integer version;
        
        @JsonProperty("status")
        @Schema(description = "상태")
        private String status;
        
        @JsonProperty("output_keys")
        @Schema(description = "출력 키 목록")
        private List<OutputKey> outputKeys;

        @JsonProperty("deployment_config_path")
        @Schema(description = "배포 설정 경로")
        private String deploymentConfigPath;
    }
    
    /**
     * 입력/출력 키 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "입력/출력 키 정보")
    public static class InputKey {
        
        @JsonProperty("name")
        @Schema(description = "키 이름")
        private String name;
        
        @JsonProperty("required")
        @Schema(description = "필수 여부")
        private Boolean required;
        
        @JsonProperty("keytable_id")
        @Schema(description = "키테이블 ID")
        private String keytableId;

        @JsonProperty("description")
        @Schema(description = "키 설명")
        private String description;
        
        @JsonProperty("fixed_value")
        @Schema(description = "고정 값")
        private String fixedValue;
    }

        /**
     * 입력/출력 키 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "입력/출력 키 정보")
    public static class OutputKey {
        
        @JsonProperty("name")
        @Schema(description = "키 이름")
        private String name;
        
        @JsonProperty("keytable_id")
        @Schema(description = "키테이블 ID")
        private String keytableId;
    }
}
