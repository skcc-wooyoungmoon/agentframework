package com.skax.aiplatform.dto.model.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 배포 생성 요청 DTO
 * 
 * <p>
 * AXPortal에서 모델 배포를 생성하기 위한 요청 데이터 구조입니다.
 * 사용자 친화적인 필드명과 검증 규칙을 포함합니다.
 * </p>
 * 
 * @author AXPortal Team
 * @since 2025-01-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 배포 생성 요청 정보", example = """
        {
          "name": "gpt-4-serving",
          "description": "GPT-4 모델 서빙 인스턴스",
          "modelId": "550e8400-e29b-41d4-a716-446655440000",
          "versionId": "660e8400-e29b-41d4-a716-446655440001",
          "cpuRequest": 2,
          "cpuLimit": 4,
          "gpuRequest": 1,
          "gpuLimit": 1,
          "memRequest": 8192,
          "memLimit": 16384,
          "minReplicas": 1,
          "maxReplicas": 5,
          "safetyFilterInput": true,
          "safetyFilterOutput": true
        }
        """)
public class CreateModelDeployReq {

    /**
     * 서빙 이름
     */
    @Schema(description = "서빙 이름 (프로젝트 내 고유)", example = "gpt-4-serving", required = true, minLength = 3, maxLength = 100)
    private String name;

    /**
     * 서빙 설명
     */
    @Schema(description = "서빙 설명 (목적과 용도)", example = "GPT-4 모델을 활용한 텍스트 생성 서빙 인스턴스", maxLength = 500)
    private String description;

    /**
     * 모델 ID
     */
    @Schema(description = "서빙할 모델의 고유 식별자", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String modelId;

    /**
     * 모델 버전 ID
     */
    @Schema(description = "모델 버전 ID (미지정 시 최신 버전 사용)", example = "660e8400-e29b-41d4-a716-446655440001", format = "uuid")
    private String versionId;

    /**
     * 커스텀 서빙 여부
     */
    @Schema(description = "커스텀 서빙 설정 사용 여부", example = "false")
    private Boolean isCustom;

    /**
     * 서빙 파라미터
     */
    @Schema(description = "서빙 파라미터 설정 (양자화, GPU 설정 등)")
    private Object servingParams;

    /**
     * CPU 요청량
     */
    @Schema(description = "CPU 요청량 (코어 수)", example = "2", minimum = "0")
    private Integer cpuRequest;

    /**
     * CPU 제한량
     */
    @Schema(description = "CPU 제한량 (코어 수)", example = "4", minimum = "0")
    private Integer cpuLimit;

    /**
     * GPU 요청량
     */
    @Schema(description = "GPU 요청량", example = "1", minimum = "0")
    private Integer gpuRequest;

    /**
     * GPU 제한량
     */
    @Schema(description = "GPU 제한량", example = "1", minimum = "0")
    private Integer gpuLimit;

    /**
     * 메모리 요청량
     */
    @Schema(description = "메모리 요청량 (MB)", example = "8192", minimum = "0")
    private Integer memRequest;

    /**
     * 메모리 제한량
     */
    @Schema(description = "메모리 제한량 (MB)", example = "16384", minimum = "0")
    private Integer memLimit;

    /**
     * 최소 레플리카 수
     */
    @Schema(description = "최소 레플리카 수 (오토스케일링)", example = "1", minimum = "0")
    private Integer minReplicas;

    /**
     * 최대 레플리카 수
     */
    @Schema(description = "최대 레플리카 수 (오토스케일링)", example = "5", minimum = "1")
    private Integer maxReplicas;

    /**
     * 오토스케일링 클래스
     */
    @Schema(description = "오토스케일링 클래스", example = "kpa.autoscaling.knative.dev")
    private String autoscalingClass;

    /**
     * 오토스케일링 메트릭
     */
    @Schema(description = "오토스케일링 메트릭", example = "concurrency")
    private String autoscalingMetric;

    /**
     * 스케일링 타겟
     */
    @Schema(description = "스케일링 타겟 값", example = "10", minimum = "1")
    private Integer target;

    /**
     * GPU 타입
     */
    @Schema(description = "GPU 타입", example = "nvidia-tesla-v100")
    private String gpuType;

    /**
     * 입력 안전 필터 적용 여부
     */
    @Schema(description = "입력 안전 필터 적용 여부", example = "true")
    private Boolean safetyFilterInput;

    /**
     * 출력 안전 필터 적용 여부
     */
    @Schema(description = "출력 안전 필터 적용 여부", example = "true")
    private Boolean safetyFilterOutput;

    /**
     * 입력 안전 필터 그룹
     */
    @Schema(description = "입력 안전 필터 그룹 리스트", example = "[]")
    private List<String> safetyFilterInputGroups;

    /**
     * 출력 안전 필터 그룹
     */
    @Schema(description = "출력 안전 필터 그룹 리스트", example = "[]")
    private List<String> safetyFilterOutputGroups;

    /**
     * 입력 데이터 마스킹 적용 여부
     */
    @Schema(description = "입력 데이터 마스킹 적용 여부", example = "false")
    private Boolean dataMaskingInput;

    /**
     * 출력 데이터 마스킹 적용 여부
     */
    @Schema(description = "출력 데이터 마스킹 적용 여부", example = "false")
    private Boolean dataMaskingOutput;

    /**
     * 접근 권한 정책
     */
    @Schema(description = "접근 권한 정책 배열 (사용자, 그룹, 역할별 접근 권한 정의)")
    private List<PolicyPayload> policy;
}
