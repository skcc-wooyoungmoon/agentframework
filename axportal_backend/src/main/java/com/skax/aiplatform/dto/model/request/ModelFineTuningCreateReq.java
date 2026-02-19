package com.skax.aiplatform.dto.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 파인튜닝 모델 생성 요청 DTO
 *
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파인튜닝 모델 생성 요청")
public class ModelFineTuningCreateReq {

    /**
     * Training 이름
     *
     * <p>Fine-tuning Training의 고유한 이름입니다.
     * 사용자가 Training을 식별하고 관리하기 위한 목적으로 사용됩니다.</p>
     *
     * @apiNote 필수 필드이며, 최대 255자까지 입력 가능합니다.
     */
    @Schema(
            description = "Training 이름 (사용자 정의 식별명)",
            example = "GPT-4 Fine-tuning",
            required = true,
            maxLength = 255
    )
    @NotBlank(message = "트레이닝 이름은 필수입니다")
    @Size(max = 255, message = "트레이닝 이름은 255자를 초과할 수 없습니다")
    @JsonProperty("name")
    private String name;

    /**
     * Training 상태
     *
     * <p>Training의 현재 상태를 나타냅니다.
     * 기본값은 "initialized"이며, 다양한 Training 단계를 표현합니다.</p>
     *
     * @implNote 허용 값: initialized, starting, stopping, stopped, resource-allocating, resource-allocated, training, trained, error
     */
    @Schema(
            description = "Training 상태",
            example = "initialized",
            allowableValues = {
                    "initialized", "starting", "stopping", "stopped",
                    "resource-allocating", "resource-allocated", "training", "trained", "error"
            },
            defaultValue = "initialized",
            maxLength = 64
    )
    @JsonProperty("status")
    private String status;

    /**
     * 이전 상태
     *
     * <p>Training의 이전 상태를 나타냅니다.
     * 상태 변화 추적을 위해 사용됩니다.</p>
     */
    @Schema(
            description = "이전 Training 상태",
            example = "initialized",
            allowableValues = {
                    "initialized", "starting", "stopping", "stopped",
                    "resource-allocating", "resource-allocated", "training", "trained", "error"
            },
            maxLength = 64
    )
    @JsonProperty("prev_status")
    private String prevStatus;

    /**
     * Training 진행률
     *
     * <p>Training 진행률을 관리하기 위한 JSON 객체입니다.
     * 현재는 percentage를 포함하며, 향후 추가 진행률 필드가 추가될 수 있습니다.</p>
     *
     * @implNote JSON 객체 형태로 저장되며, percentage 필드 등을 포함할 수 있습니다.
     */
    @Schema(
            description = "Training 진행률 정보 (JSON 객체, percentage 등 포함)",
            example = "{\"percentage\": 0}",
            type = "object"
    )
    @JsonProperty("progress")
    private Object progress;

    /**
     * Training 리소스 정보
     *
     * <p>Training에 필요한 리소스 정보입니다.
     * CPU, Memory, GPU 할당량을 JSON 객체 형태로 지정합니다.</p>
     *
     * @apiNote 필수 필드이며, cpu_quota, mem_quota, gpu_quota를 포함해야 합니다.
     */
    @Schema(
            description = "Training 리소스 정보 (cpu_quota, mem_quota, gpu_quota)",
            example = "{\"cpu_quota\": \"2\", \"mem_quota\": \"8Gi\", \"gpu_quota\": \"1\"}",
            required = true,
            type = "object"
    )
    @NotNull(message = "리소스 정보는 필수입니다")
    @JsonProperty("resource")
    private Object resource;

    /**
     * 데이터셋 ID 목록
     *
     * <p>Training에 사용할 데이터셋 ID들의 목록입니다.
     * 각 ID는 UUID 형식이어야 합니다.</p>
     *
     * @apiNote 필수 필드이며, 최소 하나의 데이터셋 ID가 필요합니다.
     */
    @Schema(
            description = "Training에 사용할 데이터셋 ID 목록 (UUID 형식)",
            example = "[\"dataset-123\", \"dataset-456\"]",
            required = true
    )
    @NotNull(message = "데이터셋 ID 목록은 필수입니다")
    @Size(min = 1, message = "최소 하나의 데이터셋 ID가 필요합니다")
    @JsonProperty("dataset_ids")
    private List<String> datasetIds;

    /**
     * 베이스 모델 ID
     *
     * <p>Fine-tuning할 베이스 모델의 ID입니다.
     * UUID 형식이어야 합니다.</p>
     *
     * @apiNote 필수 필드이며, 유효한 모델 ID여야 합니다.
     */
    @Schema(
            description = "Fine-tuning할 베이스 모델 ID (UUID 형식)",
            example = "model-789",
            required = true,
            format = "uuid"
    )
    @NotBlank(message = "베이스 모델 ID는 필수입니다")
    @JsonProperty("base_model_id")
    private String baseModelId;

    /**
     * Trainer 파라미터
     *
     * <p>Training 중 Trainer가 사용할 파라미터 세부사항입니다.
     * Trainer 타입에 따라 정의됩니다.</p>
     *
     * @apiNote 필수 필드이며, JSON 문자열 형태로 저장됩니다.
     */
    @Schema(
            description = "Trainer 파라미터 (JSON 문자열, Trainer 타입에 따라 정의)",
            example = "{\"learning_rate\": 0.001, \"epochs\": 10}",
            required = true
    )
    @NotBlank(message = "파라미터는 필수입니다")
    @JsonProperty("params")
    private String params;

    /**
     * Training 환경 변수
     *
     * <p>Training 환경을 정의하는 필드입니다.
     * 지정하지 않으면 기본 환경에서 실행됩니다.</p>
     *
     * @implNote JSON 객체 형태로 저장되며, 선택적 필드입니다.
     */
    @Schema(
            description = "Training 환경 변수 (JSON 객체, 미지정시 기본 환경 사용)",
            example = "{\"CUDA_VISIBLE_DEVICES\": \"0\"}",
            type = "object"
    )
    @JsonProperty("envs")
    private Object envs;

    /**
     * 설명
     *
     * <p>Training에 대한 설명입니다.
     * 사용자가 Training의 목적이나 특징을 기록하기 위해 사용됩니다.</p>
     */
    @Schema(
            description = "Training 설명",
            example = "Customer service chatbot fine-tuning"
    )
    @JsonProperty("description")
    private String description;

    /**
     * 프로젝트 ID
     *
     * <p>프로젝트 ID를 직접 지정할 수 있습니다.
     * 지정하지 않으면 인증 토큰에 포함된 프로젝트 ID로 설정됩니다.</p>
     */
    @Schema(
            description = "프로젝트 ID (미지정시 인증 토큰의 프로젝트 ID 사용)",
            example = "project-123",
            format = "uuid"
    )
    @JsonProperty("project_id")
    private String projectId;

    /**
     * 작업 ID
     *
     * <p>Training과 연관된 작업 ID입니다.
     * 작업 관리 시스템과의 연동을 위해 사용됩니다.</p>
     */
    @Schema(
            description = "작업 ID (작업 관리 시스템 연동용)",
            example = "task-456",
            format = "uuid"
    )
    @JsonProperty("task_id")
    private String taskId;

    /**
     * Trainer ID
     *
     * <p>사용할 Trainer의 ID입니다.
     * UUID 형식이어야 합니다.</p>
     *
     * @apiNote 필수 필드이며, 유효한 Trainer ID여야 합니다.
     */
    @Schema(
            description = "사용할 Trainer ID (UUID 형식)",
            example = "trainer-abc",
            required = true,
            format = "uuid"
    )
    @NotBlank(message = "Trainer ID는 필수입니다")
    @JsonProperty("trainer_id")
    private String trainerId;

    /**
     * 접근 권한 정책
     *
     * <p>Training에 대한 접근 권한을 정의하는 정책 배열입니다.
     * 사용자, 그룹, 역할별 접근 권한과 허용 범위를 설정합니다.</p>
     */
    @Schema(
            description = "접근 권한 정책 배열 (사용자, 그룹, 역할별 접근 권한 정의)"
    )
    @JsonProperty("policy")
    private PolicyPayload policy;

    /**
     * Backend.ai scaling group for resource allocation")
     *
     * <p>Backend.ai scaling group for resource allocation</p>
     */
    @Schema(
            description = "Backend.ai scaling group for resource allocation"
    )
    @JsonProperty("scalingGroup")
    private String scalingGroup;

    /**
     * Backend.ai target agents (comma-separated list)
     *
     * <p>Backend.ai target agents (comma-separated list)</p>
     */
    @Schema(
            description = "Backend.ai target agents (comma-separated list)"
    )
    @JsonProperty("agentList")
    private String agentList;
}
