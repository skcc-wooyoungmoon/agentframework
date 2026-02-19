package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Serving 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 모델 서빙 관련 작업의 응답 데이터를 담는 구조입니다.
 * 서빙 생성, 수정, 조회 등의 작업 결과를 포함합니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "SKTAI Serving 응답 정보")
public class ServingResponse  {

    
    @JsonProperty("serving_id")
    @Schema(description = "서빙 ID")
    private String servingId;

    @JsonProperty("name")
    @Schema(description = "서빙 이름")
    private String name;

    @JsonProperty("description")
    @Schema(description = "서빙 설명")
    private String description;

    @JsonProperty("kserve_yaml")
    @Schema(description = "KServe YAML")
    private String kserveYaml;

    @JsonProperty("isvc_name")
    @Schema(description = "InferenceService 이름")
    private String isvcName;

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @JsonProperty("namespace")
    @Schema(description = "네임스페이스")
    private String namespace;

    @JsonProperty("status")
    @Schema(description = "서빙 상태")
    private String status;

    @JsonProperty("model_id")
    @Schema(description = "모델 ID")
    private String modelId;

    @JsonProperty("version_id")
    @Schema(description = "버전 ID")
    private String versionId;

    @JsonProperty("serving_params")
    @Schema(description = "서빙 파라미터")
    private String servingParams; 

    @JsonProperty("error_message")
    @Schema(description = "오류 메시지")
    private String errorMessage;

    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량")
    private Number cpuRequest;

    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량")
    private Number cpuLimit;

    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청량")
    private Number gpuRequest;

    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한량")
    private Number gpuLimit;

    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량")
    private Number memRequest;

    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량")
    private Number memLimit;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;

    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "최종 수정 시간")
    private LocalDateTime updatedAt;

    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부")
    private Boolean isDeleted;

    @JsonProperty("safety_filter_input")
    @Schema(description = "입력 안전 필터")
    private Boolean safetyFilterInput;

    @JsonProperty("safety_filter_output")
    @Schema(description = "출력 안전 필터")
    private Boolean safetyFilterOutput;

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

    @JsonProperty("model_name")
    @Schema(description = "모델 이름")
    private String modelName;

    @JsonProperty("display_name")
    @Schema(description = "표시 이름")
    private String displayName;

    @JsonProperty("model_description")
    @Schema(description = "모델 설명")
    private String modelDescription;

    @JsonProperty("type")
    @Schema(description = "모델 타입")
    private String type;

    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입")
    private String servingType;

    @JsonProperty("is_private")
    @Schema(description = "비공개 여부")
    private Boolean isPrivate;

    @JsonProperty("is_valid")
    @Schema(description = "유효 여부")
    private Boolean isValid;

    @JsonProperty("inference_param")
    @Schema(description = "추론 파라미터")
    private Map<String, Object> inferenceParam;

    @JsonProperty("quantization")
    @Schema(description = "양자화 정보")
    private Map<String, Object> quantization;

    @JsonProperty("provider_name")
    @Schema(description = "제공자 이름")
    private String providerName;

    @JsonProperty("model_version")
    @Schema(description = "모델 버전")
    private String modelVersion;

    @JsonProperty("path")
    @Schema(description = "경로")
    private String path;

    @JsonProperty("version_path")
    @Schema(description = "버전 경로")
    private String versionPath;

    @JsonProperty("fine_tuning_id")
    @Schema(description = "파인튜닝 ID")
    private String fineTuningId;

    @JsonProperty("version_is_valid")
    @Schema(description = "버전 유효 여부")
    private Boolean versionIsValid;

    @JsonProperty("version_is_deleted")
    @Schema(description = "버전 삭제 여부")
    private Boolean versionIsDeleted;

    @JsonProperty("gpu_type")
    @Schema(description = "GPU 타입")
    private String gpuType;

    @JsonProperty("is_custom")
    @Schema(description = "커스텀 여부")
    private Boolean isCustom;

    @JsonProperty("serving_mode")
    @Schema(description = "서빙 모드")
    private String servingMode;

    @JsonProperty("serving_operator")
    @Schema(description = "서빙 오퍼레이터")
    private String servingOperator;

    @JsonProperty("endpoint")
    @Schema(description = "엔드포인트")
    private String endpoint;

    @JsonProperty("external_endpoint")
    @Schema(description = "외부 엔드포인트")
    private String externalEndpoint;

    @JsonProperty("runtime")
    @Schema(description = "런타임")
    private String runtime;

    @JsonProperty("runtime_image")
    @Schema(description = "런타임 이미지")
    private String runtimeImage;

    @JsonProperty("envs")
    @Schema(description = "환경 변수")
    private Map<String, Object> envs;
}
