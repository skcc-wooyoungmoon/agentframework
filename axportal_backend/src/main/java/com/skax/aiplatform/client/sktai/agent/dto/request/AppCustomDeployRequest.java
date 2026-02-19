package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 커스텀 배포 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 커스텀 배포 요청 정보")
public class AppCustomDeployRequest {
    
    @JsonProperty("name")
    @Schema(description = "앱 이름", required = true)
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "앱 설명", required = true)
    private String description;
    
    @JsonProperty("version_description")
    @Schema(description = "버전 설명")
    private String versionDescription;
    
    @JsonProperty("target_type")
    @Schema(description = "타겟 타입", defaultValue = "external_graph")
    private String targetType;
    
    @JsonProperty("model_list")
    @Schema(description = "모델 목록")
    private List<String> modelList;
    
    @JsonProperty("image_url")
    @Schema(description = "이미지 URL", defaultValue = "")
    private String imageUrl;
    
    @JsonProperty("use_external_registry")
    @Schema(description = "외부 레지스트리 사용 여부", defaultValue = "true")
    private Boolean useExternalRegistry;
    
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청", defaultValue = "1")
    private Integer cpuRequest;
    
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한", defaultValue = "1")
    private Integer cpuLimit;
    
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청 (GB)", defaultValue = "2")
    private Integer memRequest;
    
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한 (GB)", defaultValue = "2")
    private Integer memLimit;
    
    @JsonProperty("min_replicas")
    @Schema(description = "최소 복제본 수", defaultValue = "1")
    private Integer minReplicas;
    
    @JsonProperty("max_replicas")
    @Schema(description = "최대 복제본 수", defaultValue = "1")
    private Integer maxReplicas;
    
    @JsonProperty("workers_per_core")
    @Schema(description = "코어당 워커 수", defaultValue = "3")
    private Integer workersPerCore;
    
    @JsonProperty("safety_filter_options")
    @Schema(description = "안전 필터 옵션 (문자열 또는 객체)")
    private Object safetyFilterOptions;

    @JsonProperty("policy")
    @Schema(description = "정책 목록", example = "[{\"scopes\": [\"GET\", \"POST\", \"PUT\", \"DELETE\"], \"policies\": [{\"type\": \"regex\", \"logic\": \"POSITIVE\", \"target_claim\": \"current_group\", \"pattern\": \"^/D2$\"}], \"logic\": \"POSITIVE\", \"decision_strategy\": \"AFFIRMATIVE\", \"cascade\": false}]")
    private List<PolicyRequest> policy;
}
