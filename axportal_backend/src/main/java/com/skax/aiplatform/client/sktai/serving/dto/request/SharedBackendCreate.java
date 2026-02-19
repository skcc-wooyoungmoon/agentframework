package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Shared Backend 생성 요청 DTO
 * 
 * <p>SKTAI Serving 시스템에서 새로운 공유 백엔드를 생성하기 위한 요청 데이터 구조입니다.
 * 공유 백엔드는 여러 서빙 인스턴스가 공통으로 사용할 수 있는 백엔드 리소스를 제공합니다.</p>
 * 
 * <h3>공유 백엔드의 특징:</h3>
 * <ul>
 *   <li><strong>리소스 효율성</strong>: 여러 서빙이 하나의 백엔드를 공유하여 리소스 절약</li>
 *   <li><strong>중앙 집중식 관리</strong>: 백엔드 설정을 중앙에서 관리</li>
 *   <li><strong>확장성</strong>: 백엔드 용량을 독립적으로 스케일링 가능</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SharedBackendResponse 공유 백엔드 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Shared Backend 생성 요청 정보",
    example = """
        {
          "model_id": "gpt-4-turbo-128k",
          "shared_backend_name": "shared-gpt4-backend",
          "description": "GPT-4 모델을 위한 공유 백엔드 인프라",
          "cpu_request": 8,
          "cpu_limit": 16,
          "gpu_request": 4,
          "gpu_limit": 4,
          "mem_request": 32768,
          "mem_limit": 65536,
          "min_replicas": 2,
          "max_replicas": 10
        }
        """
)
public class SharedBackendCreate {
    
    /**
     * 모델 식별자
     * 
     * <p>공유 백엔드에서 사용할 모델의 고유 식별자입니다.
     * 백엔드가 처리할 모델을 지정합니다.</p>
     * 
     * @apiNote 모델은 사전에 등록되어 있어야 하며, 백엔드와 호환되는 모델이어야 합니다.
     */
    @JsonProperty("model_id")
    @Schema(
        description = "백엔드에서 사용할 모델 ID", 
        example = "gpt-4-turbo-128k",
        required = true,
        minLength = 5,
        maxLength = 100
    )
    private String modelId;
    
    /**
     * 공유 백엔드 이름
     * 
     * <p>공유 백엔드의 고유한 이름입니다.
     * 다른 서빙에서 이 백엔드를 참조할 때 사용됩니다.</p>
     * 
     * @implNote 이름은 URL 경로에 사용되므로 영문자, 숫자, 하이픈만 허용됩니다.
     */
    @JsonProperty("shared_backend_name")
    @Schema(
        description = "공유 백엔드 고유 이름 (영문자, 숫자, 하이픈만 허용)", 
        example = "shared-gpt4-backend",
        required = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9-]*$",
        minLength = 3,
        maxLength = 100
    )
    private String sharedBackendName;
    
    /**
     * 공유 백엔드 설명
     * 
     * <p>공유 백엔드의 목적과 용도를 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "공유 백엔드 설명 (목적과 용도)", 
        example = "GPT-4 모델을 위한 공유 백엔드 인프라",
        maxLength = 500
    )
    private String description;
    
    /**
     * 커스텀 공유 백엔드 여부
     * 
     * <p>커스텀 공유 백엔드 설정을 사용할지 여부입니다.</p>
     */
    @JsonProperty("is_custom")
    @Schema(
        description = "커스텀 공유 백엔드 설정 사용 여부", 
        example = "false"
    )
    private Boolean isCustom;
    
    /**
     * 공유 백엔드 파라미터
     * 
     * <p>공유 백엔드에 사용할 상세 파라미터 설정입니다.
     * 모델 로딩 옵션, 캐싱 설정, 연결 풀 크기 등을 포함합니다.</p>
     */
    @JsonProperty("shared_backend_params")
    @Schema(
        description = "공유 백엔드 파라미터 설정 (모델 로딩, 캐싱 설정 등)"
    )
    private Object sharedBackendParams;
    
    /**
     * CPU 요청량
     * 
     * <p>공유 백엔드 인스턴스가 요청할 CPU 리소스 양입니다.
     * 단위는 CPU 코어 수입니다.</p>
     */
    @JsonProperty("cpu_request")
    @Schema(
        description = "CPU 요청량 (코어 수)", 
        example = "8",
        minimum = "0"
    )
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     * 
     * <p>공유 백엔드 인스턴스가 사용할 수 있는 최대 CPU 리소스 양입니다.</p>
     */
    @JsonProperty("cpu_limit")
    @Schema(
        description = "CPU 제한량 (코어 수)", 
        example = "16",
        minimum = "0"
    )
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     * 
     * <p>공유 백엔드 인스턴스가 요청할 GPU 리소스 양입니다.</p>
     */
    @JsonProperty("gpu_request")
    @Schema(
        description = "GPU 요청량", 
        example = "4",
        minimum = "0"
    )
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     * 
     * <p>공유 백엔드 인스턴스가 사용할 수 있는 최대 GPU 리소스 양입니다.</p>
     */
    @JsonProperty("gpu_limit")
    @Schema(
        description = "GPU 제한량", 
        example = "4",
        minimum = "0"
    )
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     * 
     * <p>공유 백엔드 인스턴스가 요청할 메모리 리소스 양입니다.
     * 단위는 MB입니다.</p>
     */
    @JsonProperty("mem_request")
    @Schema(
        description = "메모리 요청량 (MB)", 
        example = "32768",
        minimum = "0"
    )
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     * 
     * <p>공유 백엔드 인스턴스가 사용할 수 있는 최대 메모리 리소스 양입니다.</p>
     */
    @JsonProperty("mem_limit")
    @Schema(
        description = "메모리 제한량 (MB)", 
        example = "65536",
        minimum = "0"
    )
    private Integer memLimit;
    
    /**
     * GPU 타입
     * 
     * <p>사용할 GPU의 타입을 지정합니다.</p>
     */
    @JsonProperty("gpu_type")
    @Schema(
        description = "GPU 타입", 
        example = "nvidia-tesla-v100"
    )
    private String gpuType;
    
    /**
     * 최소 레플리카 수
     * 
     * <p>오토스케일링 시 유지할 최소 인스턴스 수입니다.
     * 공유 백엔드는 고가용성을 위해 최소 2개 이상 권장됩니다.</p>
     */
    @JsonProperty("min_replicas")
    @Schema(
        description = "최소 레플리카 수 (오토스케일링, 고가용성을 위해 2개 이상 권장)", 
        example = "2",
        minimum = "1"
    )
    private Integer minReplicas;
    
    /**
     * 최대 레플리카 수
     * 
     * <p>오토스케일링 시 생성할 수 있는 최대 인스턴스 수입니다.</p>
     */
    @JsonProperty("max_replicas")
    @Schema(
        description = "최대 레플리카 수 (오토스케일링)", 
        example = "10",
        minimum = "1"
    )
    private Integer maxReplicas;
    
    /**
     * 오토스케일링 클래스
     * 
     * <p>사용할 오토스케일링 정책의 클래스입니다.</p>
     */
    @JsonProperty("autoscaling_class")
    @Schema(
        description = "오토스케일링 클래스", 
        example = "kpa.autoscaling.knative.dev"
    )
    private String autoscalingClass;
    
    /**
     * 오토스케일링 메트릭
     * 
     * <p>오토스케일링의 기준이 되는 메트릭입니다.
     * 공유 백엔드는 보통 처리량이나 큐 길이를 기준으로 스케일링합니다.</p>
     */
    @JsonProperty("autoscaling_metric")
    @Schema(
        description = "오토스케일링 메트릭 (처리량, 큐 길이 등)", 
        example = "rps"
    )
    private String autoscalingMetric;
    
    /**
     * 스케일링 타겟
     * 
     * <p>오토스케일링의 목표 값입니다.</p>
     */
    @JsonProperty("target")
    @Schema(
        description = "스케일링 타겟 값", 
        example = "100",
        minimum = "1"
    )
    private Integer target;
}
