package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import java.util.List;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

/**
 * SKTAI Agent App 배포 상세 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 배포 상세 응답 정보")
public class AppDeploymentResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "배포 상세 정보")
    private AppDeploymentInfo data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    /**
     * 배포 상세 정보
     */
    @Getter     
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder    
    @Schema(description = "배포 상세 정보")
    public static class AppDeploymentInfo {
    
    @JsonProperty("app_id")
    @Schema(description = "앱 ID")
    private String appId;
    
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
    @Schema(description = "입력 키")
    private List<InputKey> inputKeys;
    
    @JsonProperty("config")
    @Schema(description = "배포 설정")
    private Object config;

    @JsonProperty("output_type")
    @Schema(description = "출력 타입")
    private String outputType;

    @JsonProperty("deployed_dt")
    @Schema(description = "배포 일시")
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
    @Schema(description = "출력 키")
    private List<OutputKey> outputKeys;
    
    @JsonProperty("deployment_config_path")
    @Schema(description = "배포 설정 경로")
    private String deploymentConfigPath;
    
    @JsonProperty("delete_flag")
    @Schema(description = "삭제 여부")
    private Boolean deleteFlag;

    @JsonProperty("endpoint")
    @Schema(description = "엔드포인트")
    private String endpoint;

    /**
     * 입력 키
     */
    @Getter     
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder    
    @Schema(description = "입력 키")
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

    }

    /**
     * 출력 키
     */
    @Getter     
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder    
    @Schema(description = "출력 키")
    public static class OutputKey {
        @JsonProperty("name")
        @Schema(description = "키 이름")
        private String name;

        @JsonProperty("keytable_id")
        @Schema(description = "키테이블 ID")
        private String keytableId;

    }
}
}