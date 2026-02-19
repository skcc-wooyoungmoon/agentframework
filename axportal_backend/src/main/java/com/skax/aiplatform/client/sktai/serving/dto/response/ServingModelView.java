package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.serving.dto.common.InferenceParam;
import com.skax.aiplatform.client.sktai.serving.dto.common.Quantization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 서빙 모델 뷰 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 모델 서빙 정보를 조회할 때 반환되는 상세 정보입니다.
 * 서빙 정보와 연관된 모델 정보를 함께 포함하는 확장된 뷰입니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>서빙 기본 정보</strong>: ID, 이름, 설명, 상태</li>
 *   <li><strong>모델 정보</strong>: 모델 ID, 버전, 이름, 타입</li>
 *   <li><strong>리소스 정보</strong>: CPU, GPU, 메모리 할당</li>
 *   <li><strong>스케일링 정보</strong>: 최소/최대 복제본, 오토스케일링 설정</li>
 *   <li><strong>보안 설정</strong>: 안전 필터, 데이터 마스킹</li>
 *   <li><strong>메타데이터</strong>: 생성/수정 시간, 생성자</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see ServingResponse 기본 서빙 응답
 * @see ServingStatus 서빙 상태 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 서빙 모델 뷰 상세 정보",
    example = """
        {
          "serving_id": "srv-123e4567-e89b-12d3-a456-426614174000",
          "name": "gpt4-production",
          "description": "GPT-4 프로덕션 서빙",
          "kserve_yaml": "apiVersion: serving.kserve.io/v1beta1...",
          "isvc_name": "gpt4-prod-isvc",
          "project_id": "proj-123e4567-e89b-12d3-a456-426614174000",
          "namespace": "serving-prod",
          "status": "Ready",
          "model_id": "model-123e4567-e89b-12d3-a456-426614174000",
          "version_id": "version-123e4567-e89b-12d3-a456-426614174000",
          "model_name": "GPT-4",
          "display_name": "GPT-4 Large Language Model",
          "model_description": "OpenAI GPT-4 large language model",
          "type": "llm",
          "serving_type": "inference",
          "is_private": false,
          "is_valid": true,
          "provider_name": "OpenAI",
          "model_version": 1,
          "path": "/models/gpt4",
          "version_path": "/models/gpt4/v1",
          "cpu_request": 4,
          "cpu_limit": 8,
          "gpu_request": 2,
          "gpu_limit": 2,
          "mem_request": 8192,
          "mem_limit": 16384,
          "gpu_type": "A100",
          "created_by": "admin@example.com",
          "updated_by": "admin@example.com",
          "created_at": "2025-10-16T10:30:00Z",
          "updated_at": "2025-10-16T11:30:00Z",
          "is_deleted": false,
          "safety_filter_input": true,
          "safety_filter_output": true,
          "data_masking_input": false,
          "data_masking_output": false,
          "min_replicas": 2,
          "max_replicas": 10,
          "autoscaling_class": "hpa",
          "autoscaling_metric": "cpu",
          "target": 70
        }
        """
)
public class ServingModelView {
    
    /**
     * 서빙 ID
     */
    @JsonProperty("serving_id")
    @Schema(description = "서빙 고유 식별자", example = "srv-123e4567-e89b-12d3-a456-426614174000", format = "uuid", required = true)
    private String servingId;
    
    /**
     * 서빙 이름
     */
    @JsonProperty("name")
    @Schema(description = "서빙 이름", example = "gpt4-production", required = true)
    private String name;
    
    /**
     * 서빙 설명
     */
    @JsonProperty("description")
    @Schema(description = "서빙 설명", example = "GPT-4 프로덕션 서빙", required = true)
    private String description;
    
    /**
     * KServe YAML 설정
     */
    @JsonProperty("kserve_yaml")
    @Schema(description = "KServe 배포 YAML 설정", required = true)
    private String kserveYaml;
    
    /**
     * InferenceService 이름
     */
    @JsonProperty("isvc_name")
    @Schema(description = "KServe InferenceService 이름", example = "gpt4-prod-isvc", required = true)
    private String isvcName;
    
    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "proj-123e4567-e89b-12d3-a456-426614174000", required = true)
    private String projectId;
    
    /**
     * 네임스페이스
     */
    @JsonProperty("namespace")
    @Schema(description = "Kubernetes 네임스페이스", example = "serving-prod", required = true)
    private String namespace;
    
    /**
     * 서빙 상태
     */
    @JsonProperty("status")
    @Schema(description = "서빙 상태", example = "Ready", required = true)
    private String status;
    
    /**
     * 모델 ID
     */
    @JsonProperty("model_id")
    @Schema(description = "모델 ID", example = "model-123e4567-e89b-12d3-a456-426614174000", format = "uuid", required = true)
    private String modelId;
    
    /**
     * 모델 버전 ID
     */
    @JsonProperty("version_id")
    @Schema(description = "모델 버전 ID", example = "version-123e4567-e89b-12d3-a456-426614174000", format = "uuid", required = true)
    private String versionId;
    
    /**
     * 서빙 파라미터
     */
    @JsonProperty("serving_params")
    @Schema(description = "서빙 파라미터 설정", required = true)
    private String servingParams;
    
    /**
     * 에러 메시지
     */
    @JsonProperty("error_message")
    @Schema(description = "에러 메시지 (오류 발생 시)", required = true)
    private String errorMessage;
    
    /**
     * CPU 요청량
     */
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량", example = "4", required = true)
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     */
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량", example = "8", required = true)
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     */
    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청량", example = "2", required = true)
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     */
    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한량", example = "2", required = true)
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     */
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량 (MB)", example = "8192", required = true)
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     */
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량 (MB)", example = "16384", required = true)
    private Integer memLimit;
    
    /**
     * 생성자
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자", example = "admin@example.com", required = true)
    private String createdBy;
    
    /**
     * 수정자
     */
    @JsonProperty("updated_by")
    @Schema(description = "수정자", example = "admin@example.com", required = true)
    private String updatedBy;
    
    /**
     * 생성 시간
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시간", example = "2025-10-16T10:30:00Z", format = "date-time", required = true)
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간", example = "2025-10-16T11:30:00Z", format = "date-time", required = true)
    private LocalDateTime updatedAt;
    
    /**
     * 삭제 여부
     */
    @JsonProperty("is_deleted")
    @Schema(description = "삭제 여부", example = "false", required = true)
    private Boolean isDeleted;
    
    /**
     * 입력 안전 필터
     */
    @JsonProperty("safety_filter_input")
    @Schema(description = "입력 안전 필터 활성화 여부", example = "true", required = true)
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터
     */
    @JsonProperty("safety_filter_output")
    @Schema(description = "출력 안전 필터 활성화 여부", example = "true", required = true)
    private Boolean safetyFilterOutput;
    
    /**
     * 입력 데이터 마스킹
     */
    @JsonProperty("data_masking_input")
    @Schema(description = "입력 데이터 마스킹 활성화 여부", example = "false", required = true)
    private Boolean dataMaskingInput;
    
    /**
     * 출력 데이터 마스킹
     */
    @JsonProperty("data_masking_output")
    @Schema(description = "출력 데이터 마스킹 활성화 여부", example = "false", required = true)
    private Boolean dataMaskingOutput;
    
    /**
     * 최소 복제본 수
     */
    @JsonProperty("min_replicas")
    @Schema(description = "최소 복제본 수", example = "2", required = true)
    private Integer minReplicas;
    
    /**
     * 최대 복제본 수
     */
    @JsonProperty("max_replicas")
    @Schema(description = "최대 복제본 수", example = "10", required = true)
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     */
    @JsonProperty("autoscaling_class")
    @Schema(description = "오토스케일링 클래스", example = "hpa", required = true)
    private String autoscalingClass;
    
    /**
     * 오토스케일링 메트릭
     */
    @JsonProperty("autoscaling_metric")
    @Schema(description = "오토스케일링 메트릭", example = "cpu", required = true)
    private String autoscalingMetric;
    
    /**
     * 스케일링 목표값
     */
    @JsonProperty("target")
    @Schema(description = "스케일링 목표값", example = "70", required = true)
    private Integer target;
    
    /**
     * 모델 이름
     */
    @JsonProperty("model_name")
    @Schema(description = "모델 이름", example = "GPT-4", required = true)
    private String modelName;
    
    /**
     * 모델 표시 이름
     */
    @JsonProperty("display_name")
    @Schema(description = "모델 표시 이름", example = "GPT-4 Large Language Model", required = true)
    private String displayName;
    
    /**
     * 모델 설명
     */
    @JsonProperty("model_description")
    @Schema(description = "모델 설명", example = "OpenAI GPT-4 large language model", required = true)
    private String modelDescription;
    
    /**
     * 모델 타입
     */
    @JsonProperty("type")
    @Schema(description = "모델 타입", example = "llm", required = true)
    private String type;
    
    /**
     * 서빙 타입
     */
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입", example = "inference", required = true)
    private String servingType;
    
    /**
     * 프라이빗 모델 여부
     */
    @JsonProperty("is_private")
    @Schema(description = "프라이빗 모델 여부", example = "false", required = true)
    private Boolean isPrivate;
    
    /**
     * 모델 유효성
     */
    @JsonProperty("is_valid")
    @Schema(description = "모델 유효성", example = "true", required = true)
    private Boolean isValid;
    
    /**
     * 추론 파라미터
     */
    @JsonProperty("inference_param")
    @Schema(description = "추론 파라미터", required = true)
    private InferenceParam inferenceParam;
    
    /**
     * 양자화 설정
     */
    @JsonProperty("quantization")
    @Schema(description = "양자화 설정", required = true)
    private Quantization quantization;
    
    /**
     * 제공자 이름
     */
    @JsonProperty("provider_name")
    @Schema(description = "모델 제공자 이름", example = "OpenAI", required = true)
    private String providerName;
    
    /**
     * 모델 버전
     */
    @JsonProperty("model_version")
    @Schema(description = "모델 버전", example = "1", required = true)
    private Integer modelVersion;
    
    /**
     * 모델 경로
     */
    @JsonProperty("path")
    @Schema(description = "모델 경로", example = "/models/gpt4", required = true)
    private String path;
    
    /**
     * 버전 경로
     */
    @JsonProperty("version_path")
    @Schema(description = "모델 버전 경로", example = "/models/gpt4/v1", required = true)
    private String versionPath;
    
    /**
     * 파인튜닝 ID
     */
    @JsonProperty("fine_tuning_id")
    @Schema(description = "파인튜닝 ID", format = "uuid", required = true)
    private String fineTuningId;
    
    /**
     * 버전 유효성
     */
    @JsonProperty("version_is_valid")
    @Schema(description = "버전 유효성", example = "true", required = true)
    private Boolean versionIsValid;
    
    /**
     * 버전 삭제 여부
     */
    @JsonProperty("version_is_deleted")
    @Schema(description = "버전 삭제 여부", example = "false", required = true)
    private Boolean versionIsDeleted;
    
    /**
     * GPU 타입
     */
    @JsonProperty("gpu_type")
    @Schema(description = "GPU 타입", example = "A100", required = true)
    private String gpuType;
}