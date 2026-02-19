package com.skax.aiplatform.client.sktai.serving.dto.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Serving 수정 요청 DTO
 * 
 * <p>SKTAI Serving 시스템에서 기존 모델 서빙의 설정을 수정하기 위한 요청 데이터 구조입니다.
 * 리소스 할당, 오토스케일링 설정, 보안 필터 등을 업데이트할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 설명, 커스텀 설정</li>
 *   <li><strong>리소스 설정</strong>: CPU, GPU, 메모리 요청/제한</li>
 *   <li><strong>오토스케일링</strong>: 최소/최대 레플리카, 스케일링 정책</li>
 *   <li><strong>보안 필터</strong>: Safety Filter, Data Masking</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ServingResponse 서빙 수정 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Serving 수정 요청 정보",
    example = """
        {
          "description": "업데이트된 GPT-4 모델 서빙 인스턴스",
          "cpu_request": 4,
          "cpu_limit": 8,
          "gpu_request": 2,
          "gpu_limit": 2,
          "mem_request": 16384,
          "mem_limit": 32768,
          "min_replicas": 2,
          "max_replicas": 10,
          "safety_filter_input": true,
          "safety_filter_output": true,
          "envs": {}
        }
        """
)
public class ServingUpdate {
    
    /**
     * 서빙 설명
     * 
     * <p>서빙의 목적과 용도를 설명하는 텍스트입니다.
     * 기존 설명을 새로운 내용으로 업데이트할 때 사용합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "서빙 설명 (목적과 용도)", 
        example = "업데이트된 GPT-4 모델을 활용한 텍스트 생성 서빙 인스턴스",
        maxLength = 500
    )
    private String description;
    
    /**
     * 커스텀 서빙 여부
     * 
     * <p>커스텀 서빙 설정을 사용할지 여부입니다.
     * 기존 설정을 변경할 때 사용합니다.</p>
     */
    @JsonProperty("is_custom")
    @Schema(
        description = "커스텀 서빙 설정 사용 여부", 
        example = "false"
    )
    private Boolean isCustom;
    
    /**
     * 서빙 파라미터
     * 
     * <p>모델 서빙에 필요한 상세 파라미터들입니다.
     * 양자화, GPU 메모리 사용률, 텐서 병렬화 등의 옵션을 수정할 수 있습니다.</p>
     */
    @JsonProperty("serving_params")
    @Schema(
        description = "서빙 파라미터 설정 (양자화, GPU 설정 등)"
    )
    private ServingParams servingParams;
    
    /**
     * CPU 요청량
     * 
     * <p>서빙 인스턴스가 요청할 CPU 리소스 양입니다.
     * 단위는 CPU 코어 수입니다.</p>
     */
    @JsonProperty("cpu_request")
    @Schema(
        description = "CPU 요청량 (코어 수)", 
        example = "4",
        minimum = "0"
    )
    private Integer cpuRequest;
    
    /**
     * CPU 제한량
     * 
     * <p>서빙 인스턴스가 사용할 수 있는 최대 CPU 리소스 양입니다.</p>
     */
    @JsonProperty("cpu_limit")
    @Schema(
        description = "CPU 제한량 (코어 수)", 
        example = "8",
        minimum = "0"
    )
    private Integer cpuLimit;
    
    /**
     * GPU 요청량
     * 
     * <p>서빙 인스턴스가 요청할 GPU 리소스 양입니다.</p>
     */
    @JsonProperty("gpu_request")
    @Schema(
        description = "GPU 요청량", 
        example = "2",
        minimum = "0"
    )
    private Integer gpuRequest;
    
    /**
     * GPU 제한량
     * 
     * <p>서빙 인스턴스가 사용할 수 있는 최대 GPU 리소스 양입니다.</p>
     */
    @JsonProperty("gpu_limit")
    @Schema(
        description = "GPU 제한량", 
        example = "2",
        minimum = "0"
    )
    private Integer gpuLimit;
    
    /**
     * 메모리 요청량
     * 
     * <p>서빙 인스턴스가 요청할 메모리 리소스 양입니다.
     * 단위는 MB입니다.</p>
     */
    @JsonProperty("mem_request")
    @Schema(
        description = "메모리 요청량 (MB)", 
        example = "16384",
        minimum = "0"
    )
    private Integer memRequest;
    
    /**
     * 메모리 제한량
     * 
     * <p>서빙 인스턴스가 사용할 수 있는 최대 메모리 리소스 양입니다.</p>
     */
    @JsonProperty("mem_limit")
    @Schema(
        description = "메모리 제한량 (MB)", 
        example = "32768",
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
     * 입력 안전 필터 적용 여부
     * 
     * <p>입력 데이터에 대한 안전 필터링을 적용할지 여부입니다.</p>
     */
    @JsonProperty("safety_filter_input")
    @Schema(
        description = "입력 안전 필터 적용 여부", 
        example = "true"
    )
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터 적용 여부
     * 
     * <p>출력 데이터에 대한 안전 필터링을 적용할지 여부입니다.</p>
     */
    @JsonProperty("safety_filter_output")
    @Schema(
        description = "출력 안전 필터 적용 여부", 
        example = "true"
    )
    private Boolean safetyFilterOutput;
    
    /**
     * 입력 데이터 마스킹 적용 여부
     * 
     * <p>입력 데이터에 대한 개인정보 마스킹을 적용할지 여부입니다.</p>
     */
    @JsonProperty("data_masking_input")
    @Schema(
        description = "입력 데이터 마스킹 적용 여부", 
        example = "false"
    )
    private Boolean dataMaskingInput;
    
    /**
     * 출력 데이터 마스킹 적용 여부
     * 
     * <p>출력 데이터에 대한 개인정보 마스킹을 적용할지 여부입니다.</p>
     */
    @JsonProperty("data_masking_output")
    @Schema(
        description = "출력 데이터 마스킹 적용 여부", 
        example = "false"
    )
    private Boolean dataMaskingOutput;
    
    /**
     * 최소 레플리카 수
     * 
     * <p>오토스케일링 시 유지할 최소 인스턴스 수입니다.</p>
     */
    @JsonProperty("min_replicas")
    @Schema(
        description = "최소 레플리카 수 (오토스케일링)", 
        example = "2",
        minimum = "0"
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
     * <p>오토스케일링의 기준이 되는 메트릭입니다.</p>
     */
    @JsonProperty("autoscaling_metric")
    @Schema(
        description = "오토스케일링 메트릭", 
        example = "concurrency"
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
        example = "20",
        minimum = "1"
    )
    private Integer target;

    /**
     * 환경 변수
     * 
     * <p>서빙에 설정할 환경 변수 맵입니다.</p>
     */
    @JsonProperty("envs")
    @Schema(
        description = "서빙에 설정할 환경 변수 맵",
        example = "{}"
    )
    private Map<String, Object> envs;
}
