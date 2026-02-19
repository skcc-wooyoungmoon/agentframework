package com.skax.aiplatform.dto.model.request;

import java.util.List;
import java.util.Map;

import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;
import com.skax.aiplatform.dto.model.response.CreateBackendAiModelDeployRes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Backend.AI 연동 기반 모델 배포 생성 요청 DTO
 *
 * <p>
 * Backend.AI 시스템과 연동하여 새로운 모델 배포를 생성하기 위한 요청 데이터 구조입니다.
 * Backend.AI의 런타임과 이미지를 사용하여 모델을 서빙합니다.
 * </p>
 *
 * <h3>필수 정보:</h3>
 * <ul>
 * <li><strong>name</strong>: 서빙 이름</li>
 * <li><strong>modelId</strong>: 서빙할 모델의 ID</li>
 * <li><strong>runtime</strong>: Backend.AI에서 제공하는 런타임 (vllm, sglang)</li>
 * <li><strong>runtimeImage</strong>: Backend.AI에서 제공하는 런타임 이미지 주소</li>
 * <li><strong>servingMode</strong>: 단일 노드/다중 노드 선택</li>
 * <li><strong>cpuRequest</strong>: CPU 코어 수</li>
 * <li><strong>gpuRequest</strong>: GPU 개수</li>
 * <li><strong>memRequest</strong>: 메모리 (Gi)</li>
 * <li><strong>minReplicas</strong>: 레플리카 개수</li>
 * <li><strong>safetyFilterInput/output</strong>: Safety Filter 적용 여부</li>
 * <li><strong>dataMaskingInput/output</strong>: Data Masking 적용 여부</li>
 * </ul>
 *
 * @author AXPortal Team
 * @version 1.0
 * @see CreateBackendAiModelDeployRes Backend.AI 모델 배포 생성 응답
 * @since 2025-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Backend.AI 연동 기반 모델 배포 생성 요청", example = """
        {
          "name": "backend-ai-serving-test",
          "description": "backend-ai-serving-test",
          "modelId": "27f2c388-fc77-4029-825f-840bab231625",
          "versionId": "660e8400-e29b-41d4-a716-446655440001",
          "runtime": "vllm",
          "runtimeImage": "bai-repo:7080/bai/vllm:0.10.1-cuda12.8-ubuntu24.04",
          "servingMode": "SINGLE_NODE",
          "isCustom": false,
          "servingParams": {},
          "envs": {},
          "cpuRequest": 0,
          "cpuLimit": 0,
          "gpuRequest": 0,
          "gpuLimit": 0,
          "memRequest": 0,
          "memLimit": 0,
          "gpuType": "string",
          "minReplicas": 0,
          "maxReplicas": 0,
          "autoscalingClass": "string",
          "autoscalingMetric": "string",
          "target": 0,
          "safetyFilterInput": true,
          "safetyFilterOutput": true,
          "dataMaskingInput": true,
          "dataMaskingOutput": true,
          "safetyFilterInputGroups": [
            "string"
          ],
          "safetyFilterOutputGroups": [
            "string"
          ],
          "resourceGroup": "default",
          "tags": [
            {}
          ],
          "policy": []
        }
        """)
public class CreateBackendAiModelDeployReq {

    /**
     * 서빙 이름
     *
     * <p>
     * 생성할 서빙의 고유한 이름입니다.
     * 시스템 내에서 중복되지 않아야 합니다.
     * </p>
     */
    @Schema(description = "서빙 이름", example = "backend-ai-serving-test", required = true, minLength = 1, maxLength = 100)
    private String name;

    /**
     * 서빙 설명
     *
     * <p>
     * 서빙에 대한 상세 설명입니다.
     * 선택적 필드로 서빙의 목적이나 특징을 설명할 수 있습니다.
     * </p>
     */
    @Schema(description = "서빙 설명", example = "backend-ai-serving-test", maxLength = 500)
    private String description;

    /**
     * 커스텀 서빙 여부
     *
     * <p>
     * 커스텀 서빙 설정을 사용할지 여부입니다.
     * </p>
     */
    @Schema(description = "커스텀 서빙 설정 사용 여부", example = "false")
    private Boolean isCustom;

    /**
     * 모델 ID
     *
     * <p>
     * 서빙할 모델의 고유 식별자입니다.
     * 모델 레지스트리에 등록된 모델의 UUID를 참조합니다.
     * </p>
     */
    @Schema(description = "서빙할 모델의 고유 식별자", example = "27f2c388-fc77-4029-825f-840bab231625", required = true, format = "uuid")
    private String modelId;

    /**
     * 모델 버전 ID
     *
     * <p>
     * Finetuning한 모델을 서빙할 경우 입력합니다.
     * 아닌 경우 항목을 삭제합니다.
     * </p>
     */
    @Schema(description = "Finetuning한 모델을 서빙할 경우 입력 / 아닌 경우 항목 삭제", example = "660e8400-e29b-41d4-a716-446655440001", format = "uuid")
    private String versionId;

    /**
     * 런타임
     *
     * <p>
     * Backend.AI에서 제공하는 런타임입니다.
     * 지원되는 런타임: vllm, sglang
     * </p>
     */
    @Schema(description = "Backend.AI에서 제공하는 런타임", example = "vllm", required = true, allowableValues = {"vllm",
            "sglang"})
    private String runtime;

    /**
     * 런타임 이미지
     *
     * <p>
     * Backend.AI에서 제공하는 런타임 이미지 주소입니다.
     * </p>
     */
    @Schema(description = "Backend.AI에서 제공하는 런타임 이미지 주소", example = "bai-repo:7080/bai/vllm:0.10.1-cuda12.8-ubuntu24.04", required = true)
    private String runtimeImage;

    /**
     * 서빙 모드
     *
     * <p>
     * 단일 노드 / 다중 노드 선택입니다.
     * 기본값: SINGLE_NODE
     * </p>
     */
    @Schema(description = "단일 노드 / 다중 노드 선택", example = "SINGLE_NODE", required = true, allowableValues = {
            "SINGLE_NODE", "MULTI_NODE"}, defaultValue = "SINGLE_NODE")
    private String servingMode;

    /**
     * 서빙 파라미터
     *
     * <p>
     * 모델 서빙 시 적용할 파라미터입니다.
     * </p>
     */
    @Schema(description = "모델 서빙 시 적용할 파라미터")
    private Object servingParams;

    /**
     * CPU 요청량
     *
     * <p>
     * CPU 코어 수입니다.
     * </p>
     */
    @Schema(description = "CPU 코어 수", example = "2", required = true, minimum = "1")
    private Number cpuRequest;

    /**
     * CPU 제한량
     *
     * <p>
     * 서빙 인스턴스가 사용할 수 있는 최대 CPU 리소스 양입니다.
     * </p>
     */
    @Schema(description = "CPU 제한량 (코어 수)", example = "0", minimum = "0")
    private Number cpuLimit;

    /**
     * GPU 요청량
     *
     * <p>
     * GPU 개수입니다.
     * </p>
     */
    @Schema(description = "GPU 개수", example = "1", required = true, minimum = "0")
    private Number gpuRequest;

    /**
     * GPU 제한량
     *
     * <p>
     * 서빙 인스턴스가 사용할 수 있는 최대 GPU 리소스 양입니다.
     * </p>
     */
    @Schema(description = "GPU 제한량", example = "0", minimum = "0")
    private Integer gpuLimit;

    /**
     * 메모리 요청량
     *
     * <p>
     * 메모리 크기 (Gi)입니다.
     * </p>
     */
    @Schema(description = "메모리 (Gi)", example = "8", required = true, minimum = "1")
    private Number memRequest;

    /**
     * 메모리 제한량
     *
     * <p>
     * 서빙 인스턴스가 사용할 수 있는 최대 메모리 리소스 양입니다.
     * </p>
     */
    @Schema(description = "메모리 제한량 (MB)", example = "0", minimum = "0")
    private Number memLimit;

    /**
     * GPU 타입
     *
     * <p>
     * 사용할 GPU의 타입을 지정합니다.
     * </p>
     */
    @Schema(description = "GPU 타입", example = "string")
    private String gpuType;

    /**
     * 최소 레플리카 수
     *
     * <p>
     * 레플리카 개수입니다.
     * </p>
     */
    @Schema(description = "레플리카 개수", example = "1", required = true, minimum = "1")
    private Integer minReplicas;

    /**
     * 최대 레플리카 수
     *
     * <p>
     * 오토스케일링 시 생성할 수 있는 최대 인스턴스 수입니다.
     * </p>
     */
    @Schema(description = "최대 레플리카 수 (오토스케일링)", example = "0", minimum = "0")
    private Integer maxReplicas;

    /**
     * 오토스케일링 클래스
     *
     * <p>
     * 사용할 오토스케일링 정책의 클래스입니다.
     * </p>
     */
    @Schema(description = "오토스케일링 클래스", example = "string")
    private String autoscalingClass;

    /**
     * 오토스케일링 메트릭
     *
     * <p>
     * 오토스케일링의 기준이 되는 메트릭입니다.
     * </p>
     */
    @Schema(description = "오토스케일링 메트릭", example = "string")
    private String autoscalingMetric;

    /**
     * 스케일링 타겟
     *
     * <p>
     * 오토스케일링의 목표 값입니다.
     * </p>
     */
    @Schema(description = "스케일링 타겟 값", example = "0", minimum = "0")
    private Integer target;

    /**
     * Safety Filter Input 적용 여부
     *
     * <p>
     * 입력 데이터에 Safety Filter를 적용할지 여부입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Input 적용 여부", example = "true", required = true)
    private Boolean safetyFilterInput;

    /**
     * Safety Filter Output 적용 여부
     *
     * <p>
     * 출력 데이터에 Safety Filter를 적용할지 여부입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Output 적용 여부", example = "true", required = true)
    private Boolean safetyFilterOutput;

    /**
     * Data Masking Input 적용 여부
     *
     * <p>
     * 입력 데이터에 Data Masking을 적용할지 여부입니다.
     * </p>
     */
    @Schema(description = "Data Masking Input 적용 여부", example = "false", required = true)
    private Boolean dataMaskingInput;

    /**
     * Data Masking Output 적용 여부
     *
     * <p>
     * 출력 데이터에 Data Masking을 적용할지 여부입니다.
     * </p>
     */
    @Schema(description = "Data Masking Output 적용 여부", example = "false", required = true)
    private Boolean dataMaskingOutput;

    /**
     * Safety Filter Input 그룹 목록
     *
     * <p>
     * 입력 데이터에 적용할 Safety Filter 그룹 목록입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Input 그룹 목록")
    private List<String> safetyFilterInputGroups;

    /**
     * Safety Filter Output 그룹 목록
     *
     * <p>
     * 출력 데이터에 적용할 Safety Filter 그룹 목록입니다.
     * </p>
     */
    @Schema(description = "Safety Filter Output 그룹 목록")
    private List<String> safetyFilterOutputGroups;

    /**
     * 리소스 그룹
     *
     * <p>
     * 서빙에 할당할 리소스 그룹입니다.
     * 기본값: "default"
     * </p>
     */
    @Schema(description = "리소스 그룹", example = "default", defaultValue = "default")
    private String resourceGroup;

    /**
     * 환경 변수
     *
     * <p>
     * 서빙에 설정할 환경 변수 맵입니다.
     * </p>
     */
    @Schema(description = "서빙에 설정할 환경 변수 맵")
    private Map<String, Object> envs;

    /**
     * 태그
     *
     * <p>
     * 서빙에 설정할 태그 리스트입니다.
     * </p>
     */
    @Schema(description = "서빙에 설정할 태그 리스트")
    private List<Object> tags;

    /**
     * 정책 목록
     *
     * <p>
     * 서빙에 적용할 정책 목록입니다.
     * </p>
     */
    @Schema(description = "서빙에 적용할 정책 목록")
    private List<PolicyPayload> policy;
}
