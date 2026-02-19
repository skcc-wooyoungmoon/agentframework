package com.skax.aiplatform.client.sktai.agent.dto.response;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 생성 응답 DTO
 * 
 * <p>Agent 애플리케이션 생성 성공 시 반환되는 응답 데이터 구조입니다.
 * 생성된 애플리케이션의 상세 정보를 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "SKTAI Agent App 생성 성공 응답")
public class AppCreateResponse {
    
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
    @Schema(description = "생성된 애플리케이션 정보")
    private AppCreateData data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "생성된 애플리케이션 상세 정보")
    public static class AppCreateData {
        @JsonProperty("agent_serving_id")
        @Schema(description = "Agent Serving ID")
        private String agentServingId;
        
        @JsonProperty("deployment_name")
        @Schema(description = "배포 이름")
        private String deploymentName;
        
        @JsonProperty("isvc_name")
        @Schema(description = "InferenceService 이름")
        private String isvcName;
        
        @JsonProperty("description")
        @Schema(description = "설명")
        private String description;
        
        @JsonProperty("kserve_yaml")
        @Schema(description = "KServe YAML 설정")
        private String kserveYaml;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;
        
        @JsonProperty("namespace")
        @Schema(description = "네임스페이스")
        private String namespace;
        
        @JsonProperty("app_id")
        @Schema(description = "앱 ID")
        private String appId;
        
        @JsonProperty("app_version")
        @Schema(description = "앱 버전")
        private Integer appVersion;
        
        @JsonProperty("status")
        @Schema(description = "상태")
        private String status;
        
        @JsonProperty("cpu_request")
        @Schema(description = "CPU 요청량")
        private Integer cpuRequest;
        
        @JsonProperty("cpu_limit")
        @Schema(description = "CPU 제한량")
        private Integer cpuLimit;
        
        @JsonProperty("gpu_request")
        @Schema(description = "GPU 요청량")
        private Integer gpuRequest;
        
        @JsonProperty("gpu_limit")
        @Schema(description = "GPU 제한량")
        private Integer gpuLimit;
        
        @JsonProperty("mem_request")
        @Schema(description = "메모리 요청량")
        private Integer memRequest;
        
        @JsonProperty("mem_limit")
        @Schema(description = "메모리 제한량")
        private Integer memLimit;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;
        
        @JsonProperty("updated_by")
        @Schema(description = "수정자")
        private String updatedBy;
        
        @JsonProperty("is_deleted")
        @Schema(description = "삭제 여부")
        private Boolean isDeleted;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;
        
        @JsonProperty("safety_filter_input")
        @Schema(description = "입력 안전 필터")
        private Boolean safetyFilterInput;
        
        @JsonProperty("safety_filter_output")
        @Schema(description = "출력 안전 필터")
        private Boolean safetyFilterOutput;
        
        @JsonProperty("model_list")
        @Schema(description = "모델 목록")
        private List<String> modelList;
        
        @JsonProperty("endpoint")
        @Schema(description = "엔드포인트")
        private String endpoint;
        
        @JsonProperty("agent_params")
        @Schema(description = "Agent 파라미터")
        private Map<String, Object> agentParams;
        
        @JsonProperty("serving_type")
        @Schema(description = "서빙 타입")
        private String servingType;
        
        @JsonProperty("agent_app_image")
        @Schema(description = "Agent 앱 이미지")
        private String agentAppImage;
        
        @JsonProperty("agent_app_image_registry")
        @Schema(description = "Agent 앱 이미지 레지스트리")
        private String agentAppImageRegistry;
        
        @JsonProperty("deployment_id")
        @Schema(description = "배포 ID")
        private String deploymentId;
        
        @JsonProperty("error_message")
        @Schema(description = "에러 메시지")
        private String errorMessage;
        
        @JsonProperty("gpu_type")
        @Schema(description = "GPU 타입")
        private String gpuType;
        
        @JsonProperty("safety_filter_input_groups")
        @Schema(description = "입력 안전 필터 그룹")
        private List<String> safetyFilterInputGroups;
        
        @JsonProperty("safety_filter_output_groups")
        @Schema(description = "출력 안전 필터 그룹")
        private List<String> safetyFilterOutputGroups;
        
        @JsonProperty("data_masking_input")
        @Schema(description = "입력 데이터 마스킹")
        private Boolean dataMaskingInput;
        
        @JsonProperty("data_masking_output")
        @Schema(description = "출력 데이터 마스킹")
        private Boolean dataMaskingOutput;
        
        @JsonProperty("min_replicas")
        @Schema(description = "최소 복제본 수")
        private Integer minReplicas;
        
        @JsonProperty("max_replicas")
        @Schema(description = "최대 복제본 수")
        private Integer maxReplicas;
        
        @JsonProperty("autoscaling_class")
        @Schema(description = "오토스케일링 클래스")
        private String autoscalingClass;
        
        @JsonProperty("autoscaling_metric")
        @Schema(description = "오토스케일링 메트릭")
        private String autoscalingMetric;
        
        @JsonProperty("target")
        @Schema(description = "타겟 값")
        private Integer target;
        
        @JsonProperty("external_endpoint")
        @Schema(description = "외부 엔드포인트")
        private String externalEndpoint;
        
        @JsonProperty("app_config_file_path")
        @Schema(description = "앱 설정 파일 경로")
        private String appConfigFilePath;
        
        @JsonProperty("shared_backend_id")
        @Schema(description = "공유 백엔드 ID")
        private String sharedBackendId;
    }
}
