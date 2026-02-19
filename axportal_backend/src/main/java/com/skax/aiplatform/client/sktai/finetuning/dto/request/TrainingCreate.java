package com.skax.aiplatform.client.sktai.finetuning.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 생성 요청 DTO
 *
 * <p>SKTAI Fine-tuning 시스템에서 새로운 Training을 생성하기 위한 요청 데이터 구조입니다.
 * 모델 fine-tuning을 위한 데이터셋, 베이스 모델, 리소스, 파라미터 등을 설정합니다.</p>
 *
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: Training 이름</li>
 *   <li><strong>resource</strong>: 리소스 정보 (CPU, Memory, GPU)</li>
 *   <li><strong>dataset_ids</strong>: 학습에 사용할 데이터셋 ID 목록</li>
 *   <li><strong>base_model_id</strong>: Fine-tuning할 베이스 모델 ID</li>
 *   <li><strong>params</strong>: Trainer 파라미터</li>
 *   <li><strong>trainer_id</strong>: Trainer ID</li>
 * </ul>
 *
 * <h3>옵션 정보:</h3>
 * <ul>
 *   <li><strong>status</strong>: Training 상태</li>
 *   <li><strong>progress</strong>: 진행률 정보</li>
 *   <li><strong>envs</strong>: 환경 변수</li>
 *   <li><strong>description</strong>: 설명</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see TrainingRead Training 조회 응답
 * @since 2025-08-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Fine-tuning Training 생성 요청 정보", example = """
        {
          "name": "GPT-4 Fine-tuning",
          "status": "initialized",
          "resource": {
            "cpu_quota": "2",
            "mem_quota": "8Gi",
            "gpu_quota": "1"
          },
          "dataset_ids": ["dataset-123", "dataset-456"],
          "base_model_id": "model-789",
          "params": "{\\"learning_rate\\": 0.001, \\"epochs\\": 10}",
          "trainer_id": "trainer-abc",
          "description": "Customer service chatbot fine-tuning"
        }
        """)
public class TrainingCreate {

    /**
     * Training 이름
     *
     * <p>Fine-tuning Training의 고유한 이름입니다.
     * 사용자가 Training을 식별하고 관리하기 위한 목적으로 사용됩니다.</p>
     *
     * @apiNote 필수 필드이며, 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("name")
    @Schema(description = "Training 이름 (사용자 정의 식별명)", example = "GPT-4 Fine-tuning", required = true, maxLength = 255)
    private String name;

    /**
     * Training 상태
     *
     * <p>Training의 현재 상태를 나타냅니다.
     * 기본값은 "initialized"이며, 다양한 Training 단계를 표현합니다.</p>
     *
     * @implNote 허용 값: initialized, starting, stopping, stopped, resource-allocating, resource-allocated, training, trained, error
     */
    @JsonProperty("status")
    @Schema(description = "Training 상태", example = "initialized", allowableValues = {"initialized", "starting", "stopping", "stopped", "resource-allocating", "resource-allocated", "training", "trained", "error"}, defaultValue = "initialized", maxLength = 64)
    private String status;

    /**
     * 이전 상태
     *
     * <p>Training의 이전 상태를 나타냅니다.
     * 상태 변화 추적을 위해 사용됩니다.</p>
     */
    @JsonProperty("prev_status")
    @Schema(description = "이전 Training 상태", example = "initialized", allowableValues = {"initialized", "starting", "stopping", "stopped", "resource-allocating", "resource-allocated", "training", "trained", "error"}, maxLength = 64)
    private String prevStatus;

    /**
     * 진행률 정보
     *
     * <p>Training 진행률을 관리하기 위한 JSON 객체입니다.
     * 현재는 percentage를 포함하며, 향후 추가 진행률 필드가 추가될 수 있습니다.</p>
     *
     * @implNote JSON 객체 형태로 저장되며, percentage 필드 등을 포함할 수 있습니다.
     */
    @JsonProperty("progress")
    @Schema(description = "Training 진행률 정보 (JSON 객체, percentage 등 포함)", example = "{\"percentage\": 0}", type = "object")
    private Object progress;

    /**
     * 리소스 정보
     *
     * <p>Training에 필요한 리소스 정보입니다.
     * CPU, Memory, GPU 할당량을 JSON 객체 형태로 지정합니다.</p>
     *
     * @apiNote 필수 필드이며, cpu_quota, mem_quota, gpu_quota를 포함해야 합니다.
     */
    @JsonProperty("resource")
    @Schema(description = "Training 리소스 정보 (cpu_quota, mem_quota, gpu_quota)", example = "{\"cpu_quota\": \"2\", \"mem_quota\": \"8Gi\", \"gpu_quota\": \"1\"}", required = true, type = "object")
    private Object resource;

    /**
     * 데이터셋 ID 목록
     *
     * <p>Training에 사용할 데이터셋 ID들의 목록입니다.
     * 각 ID는 UUID 형식이어야 합니다.</p>
     *
     * @apiNote 필수 필드이며, 최소 하나의 데이터셋 ID가 필요합니다.
     */
    @JsonProperty("dataset_ids")
    @Schema(description = "Training에 사용할 데이터셋 ID 목록 (UUID 형식)", example = "[\"dataset-123\", \"dataset-456\"]", required = true)
    private List<String> datasetIds;

    /**
     * 베이스 모델 ID
     *
     * <p>Fine-tuning할 베이스 모델의 ID입니다.
     * UUID 형식이어야 합니다.</p>
     *
     * @apiNote 필수 필드이며, 유효한 모델 ID여야 합니다.
     */
    @JsonProperty("base_model_id")
    @Schema(description = "Fine-tuning할 베이스 모델 ID (UUID 형식)", example = "model-789", required = true, format = "uuid")
    private String baseModelId;

    /**
     * Trainer 파라미터
     *
     * <p>Training 중 Trainer가 사용할 파라미터 세부사항입니다.
     * Trainer 타입에 따라 정의됩니다.</p>
     *
     * @apiNote 필수 필드이며, JSON 문자열 형태로 저장됩니다.
     */
    @JsonProperty("params")
    @Schema(description = "Trainer 파라미터 (JSON 문자열, Trainer 타입에 따라 정의)", example = "{\"learning_rate\": 0.001, \"epochs\": 10}", required = true)
    private String params;

    /**
     * 환경 변수
     *
     * <p>Training 환경을 정의하는 필드입니다.
     * 지정하지 않으면 기본 환경에서 실행됩니다.</p>
     *
     * @implNote JSON 객체 형태로 저장되며, 선택적 필드입니다.
     */
    @JsonProperty("envs")
    @Schema(description = "Training 환경 변수 (JSON 객체, 미지정시 기본 환경 사용)", example = "{\"CUDA_VISIBLE_DEVICES\": \"0\"}", type = "object")
    private Object envs;

    /**
     * 설명
     *
     * <p>Training에 대한 설명입니다.
     * 사용자가 Training의 목적이나 특징을 기록하기 위해 사용됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(description = "Training 설명", example = "Customer service chatbot fine-tuning")
    private String description;

    /**
     * 프로젝트 ID
     *
     * <p>프로젝트 ID를 직접 지정할 수 있습니다.
     * 지정하지 않으면 인증 토큰에 포함된 프로젝트 ID로 설정됩니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID (미지정시 인증 토큰의 프로젝트 ID 사용)", example = "project-123", format = "uuid")
    private String projectId;

    /**
     * 작업 ID
     *
     * <p>Training과 연관된 작업 ID입니다.
     * 작업 관리 시스템과의 연동을 위해 사용됩니다.</p>
     */
    @JsonProperty("task_id")
    @Schema(description = "작업 ID (작업 관리 시스템 연동용)", example = "task-456", format = "uuid")
    private String taskId;

    /**
     * Trainer ID
     *
     * <p>사용할 Trainer의 ID입니다.
     * UUID 형식이어야 합니다.</p>
     *
     * @apiNote 필수 필드이며, 유효한 Trainer ID여야 합니다.
     */
    @JsonProperty("trainer_id")
    @Schema(description = "사용할 Trainer ID (UUID 형식)", example = "trainer-abc", required = true, format = "uuid")
    private String trainerId;

    /**
     * 접근 권한 정책
     *
     * <p>Training에 대한 접근 권한을 정의하는 정책 배열입니다.
     * 사용자, 그룹, 역할별 접근 권한과 허용 범위를 설정합니다.</p>
     */
    @JsonProperty("policy")
    @Schema(description = "접근 권한 정책 배열 (사용자, 그룹, 역할별 접근 권한 정의)")
    private PolicyPayload policy;

    @JsonProperty("is_auto_model_creation")
    @Schema(description = "학습완료 후 모델카탈로그 생성여부")
    private boolean isAutoModelCreation;


}
