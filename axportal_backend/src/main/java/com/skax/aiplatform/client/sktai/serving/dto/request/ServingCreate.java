package com.skax.aiplatform.client.sktai.serving.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Serving 생성 요청 DTO
 * 
 * <p>
 * SKTAI Serving 시스템에서 새로운 모델 서빙을 생성하기 위한 요청 데이터 구조입니다.
 * 모델 배포, 리소스 할당, 오토스케일링 설정 등을 포함합니다.
 * </p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 * <li><strong>name</strong>: 서빙 이름</li>
 * <li><strong>model_id</strong>: 모델 ID</li>
 * </ul>
 * 
 * <h3>옵션 정보:</h3>
 * <ul>
 * <li><strong>리소스 설정</strong>: CPU, GPU, 메모리 요청/제한</li>
 * <li><strong>오토스케일링</strong>: 최소/최대 레플리카, 스케일링 정책</li>
 * <li><strong>보안 필터</strong>: Safety Filter, Data Masking</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ServingResponse 서빙 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Serving 생성 요청 정보", example = """
        {
          "name": "gpt-4-serving",
          "description": "GPT-4 모델 서빙 인스턴스",
          "model_id": "550e8400-e29b-41d4-a716-446655440000",
          "version_id": "660e8400-e29b-41d4-a716-446655440001",
          "cpu_request": 2,
          "cpu_limit": 4,
          "gpu_request": 1,
          "gpu_limit": 1,
          "mem_request": 8192,
          "mem_limit": 16384,
          "min_replicas": 1,
          "max_replicas": 5,
          "safety_filter_input": true,
          "safety_filter_output": true,
          "safety_filter_input_groups": [],
          "safety_filter_output_groups": []
        }
        """)
public class ServingCreate {

    /**
     * 서빙 이름
     * 
     * <p>
     * 모델 서빙 인스턴스의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 서빙을 식별하는 데 사용됩니다.
     * </p>
     * 
     * @apiNote 필수 필드이며, 3-100자 사이의 문자열이어야 합니다.
     */
    @JsonProperty("name")
    @Schema(description = "서빙 이름 (프로젝트 내 고유)", example = "gpt-4-serving", required = true, minLength = 3, maxLength = 100)
    private String name;

    /**
     * 서빙 설명
     * 
     * <p>
     * 서빙의 목적과 용도를 설명하는 텍스트입니다.
     * 다른 사용자들이 서빙의 목적을 이해할 수 있도록 명확하게 작성합니다.
     * </p>
     */
    @JsonProperty("description")
    @Schema(description = "서빙 설명 (목적과 용도)", example = "GPT-4 모델을 활용한 텍스트 생성 서빙 인스턴스", maxLength = 500)
    private String description;

    /**
     * 모델 ID
     * 
     * <p>
     * 서빙할 모델의 고유 식별자입니다.
     * 모델 레지스트리에 등록된 모델의 UUID를 참조합니다.
     * </p>
     * 
     * @apiNote 필수 필드이며, 유효한 UUID 형식이어야 합니다.
     */
    @JsonProperty("model_id")
    @Schema(description = "서빙할 모델의 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String modelId;

    /**
     * 모델 버전 ID
     * 
     * <p>
     * 서빙할 모델 버전의 식별자입니다.
     * 지정하지 않으면 최신 버전이 사용됩니다.
     * </p>
     */
    @JsonProperty("version_id")
    @Schema(description = "모델 버전 ID (미지정 시 최신 버전 사용)", example = "660e8400-e29b-41d4-a716-446655440001", format = "uuid")
    private String versionId;

    /**
     * 커스텀 서빙 여부
     * 
     * <p>
     * 커스텀 서빙 설정을 사용할지 여부입니다.
     * true로 설정하면 사용자 정의 서빙 파라미터를 사용할 수 있습니다.
     * </p>
     */
    @JsonProperty("is_custom")
    @Schema(description = "커스텀 서빙 설정 사용 여부", example = "false")
    private Boolean isCustom;

    /**
     * 서빙 파라미터
     * 
     * <p>
     * 모델 서빙에 필요한 상세 파라미터들입니다.
     * 양자화, GPU 메모리 사용률, 텐서 병렬화 등의 옵션을 포함합니다.
     * </p>
     */
    @JsonProperty("serving_params")
    @Schema(description = "서빙 파라미터 설정 (양자화, GPU 설정 등)")
    private ServingParams servingParams;

    /**
     * CPU 요청량
     * 
     * <p>
     * 서빙 인스턴스가 요청할 CPU 리소스 양입니다.
     * 단위는 CPU 코어 수입니다.
     * </p>
     */
    @JsonProperty("cpu_request")
    @Schema(description = "CPU 요청량 (코어 수)", example = "2", minimum = "0")
    private Integer cpuRequest;

    /**
     * CPU 제한량
     * 
     * <p>
     * 서빙 인스턴스가 사용할 수 있는 최대 CPU 리소스 양입니다.
     * </p>
     */
    @JsonProperty("cpu_limit")
    @Schema(description = "CPU 제한량 (코어 수)", example = "4", minimum = "0")
    private Integer cpuLimit;

    /**
     * GPU 요청량
     * 
     * <p>
     * 서빙 인스턴스가 요청할 GPU 리소스 양입니다.
     * </p>
     */
    @JsonProperty("gpu_request")
    @Schema(description = "GPU 요청량", example = "1", minimum = "0")
    private Integer gpuRequest;

    /**
     * GPU 제한량
     * 
     * <p>
     * 서빙 인스턴스가 사용할 수 있는 최대 GPU 리소스 양입니다.
     * </p>
     */
    @JsonProperty("gpu_limit")
    @Schema(description = "GPU 제한량", example = "1", minimum = "0")
    private Integer gpuLimit;

    /**
     * 메모리 요청량
     * 
     * <p>
     * 서빙 인스턴스가 요청할 메모리 리소스 양입니다.
     * 단위는 MB입니다.
     * </p>
     */
    @JsonProperty("mem_request")
    @Schema(description = "메모리 요청량 (MB)", example = "8192", minimum = "0")
    private Integer memRequest;

    /**
     * 메모리 제한량
     * 
     * <p>
     * 서빙 인스턴스가 사용할 수 있는 최대 메모리 리소스 양입니다.
     * </p>
     */
    @JsonProperty("mem_limit")
    @Schema(description = "메모리 제한량 (MB)", example = "16384", minimum = "0")
    private Integer memLimit;

    /**
     * 최소 레플리카 수
     * 
     * <p>
     * 오토스케일링 시 유지할 최소 인스턴스 수입니다.
     * </p>
     */
    @JsonProperty("min_replicas")
    @Schema(description = "최소 레플리카 수 (오토스케일링)", example = "1", minimum = "0")
    private Integer minReplicas;

    /**
     * 최대 레플리카 수
     * 
     * <p>
     * 오토스케일링 시 생성할 수 있는 최대 인스턴스 수입니다.
     * </p>
     */
    @JsonProperty("max_replicas")
    @Schema(description = "최대 레플리카 수 (오토스케일링)", example = "5", minimum = "1")
    private Integer maxReplicas;

    /**
     * 오토스케일링 클래스
     * 
     * <p>
     * 사용할 오토스케일링 정책의 클래스입니다.
     * </p>
     */
    @JsonProperty("autoscaling_class")
    @Schema(description = "오토스케일링 클래스", example = "kpa.autoscaling.knative.dev")
    private String autoscalingClass;

    /**
     * 오토스케일링 메트릭
     * 
     * <p>
     * 오토스케일링의 기준이 되는 메트릭입니다.
     * </p>
     */
    @JsonProperty("autoscaling_metric")
    @Schema(description = "오토스케일링 메트릭", example = "concurrency")
    private String autoscalingMetric;

    /**
     * 스케일링 타겟
     * 
     * <p>
     * 오토스케일링의 목표 값입니다.
     * </p>
     */
    @JsonProperty("target")
    @Schema(description = "스케일링 타겟 값", example = "10", minimum = "1")
    private Integer target;

    /**
     * GPU 타입
     * 
     * <p>
     * 사용할 GPU의 타입을 지정합니다.
     * </p>
     */
    @JsonProperty("gpu_type")
    @Schema(description = "GPU 타입", example = "nvidia-tesla-v100")
    private String gpuType;

    /**
     * 입력 안전 필터 적용 여부
     * 
     * <p>
     * 입력 데이터에 대한 안전 필터링을 적용할지 여부입니다.
     * </p>
     */
    @JsonProperty("safety_filter_input")
    @Schema(description = "입력 안전 필터 적용 여부", example = "true")
    private Boolean safetyFilterInput;

    /**
     * 출력 안전 필터 적용 여부
     * 
     * <p>
     * 출력 데이터에 대한 안전 필터링을 적용할지 여부입니다.
     * </p>
     */
    @JsonProperty("safety_filter_output")
    @Schema(description = "출력 안전 필터 적용 여부", example = "true")
    private Boolean safetyFilterOutput;

    /**
     * 입력 안전 필터 그룹
     * 
     * <p>
     * 입력 데이터에 적용할 안전 필터 그룹의 리스트입니다.
     * </p>
     */
    @JsonProperty("safety_filter_input_groups")
    @Schema(description = "입력 안전 필터 그룹 리스트", example = "[]")
    private List<String> safetyFilterInputGroups;

    /**
     * 출력 안전 필터 그룹
     * 
     * <p>
     * 출력 데이터에 적용할 안전 필터 그룹의 리스트입니다.
     * </p>
     */
    @JsonProperty("safety_filter_output_groups")
    @Schema(description = "출력 안전 필터 그룹 리스트", example = "[]")
    private List<String> safetyFilterOutputGroups;

    /**
     * 입력 데이터 마스킹 적용 여부
     * 
     * <p>
     * 입력 데이터에 대한 개인정보 마스킹을 적용할지 여부입니다.
     * </p>
     */
    @JsonProperty("data_masking_input")
    @Schema(description = "입력 데이터 마스킹 적용 여부", example = "false")
    private Boolean dataMaskingInput;

    /**
     * 출력 데이터 마스킹 적용 여부
     * 
     * <p>
     * 출력 데이터에 대한 개인정보 마스킹을 적용할지 여부입니다.
     * </p>
     */
    @JsonProperty("data_masking_output")
    @Schema(description = "출력 데이터 마스킹 적용 여부", example = "false")
    private Boolean dataMaskingOutput;

    /**
     * 접근 권한 정책
     * 
     * <p>
     * 서빙에 대한 접근 권한을 정의하는 정책 배열입니다.
     * 사용자, 그룹, 역할별 접근 권한과 허용 범위를 설정합니다.
     * </p>
     */
    @JsonProperty("policy")
    @Schema(description = "접근 권한 정책 배열 (사용자, 그룹, 역할별 접근 권한 정의)")
    private List<PolicyPayload> policy;
}
