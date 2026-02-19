package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Training 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Training 정보를 조회한 결과를 담는 응답 데이터 구조입니다.
 * 생성 요청의 모든 정보와 함께 시스템 생성 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 이름, 상태, 진행률</li>
 *   <li><strong>리소스 정보</strong>: CPU, Memory, GPU 할당량</li>
 *   <li><strong>모델 정보</strong>: 베이스 모델, 데이터셋, Trainer</li>
 *   <li><strong>메타데이터</strong>: 생성일시, 수정일시</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainingCreate Training 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Training 조회 응답 정보",
    example = """
        {
          "name": "GPT-4 Fine-tuning",
          "status": "training",
          "progress": {"percentage": 45},
          "resource": {
            "cpu_quota": "2",
            "mem_quota": "8Gi",
            "gpu_quota": "1"
          },
          "dataset_ids": ["dataset-123", "dataset-456"],
          "base_model_id": "model-789",
          "params": "{\\"learning_rate\\": 0.001, \\"epochs\\": 10}",
          "trainer_id": "trainer-abc",
          "id": "training-123",
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T11:30:00Z"
        }
        """
)
public class TrainingRead {
    
    /**
     * Training 이름
     * 
     * <p>Fine-tuning Training의 고유한 이름입니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "Training 이름 (사용자 정의 식별명)", 
        example = "GPT-4 Fine-tuning",
        maxLength = 255
    )
    private String name;
    
    /**
     * Training 상태
     * 
     * <p>Training의 현재 상태를 나타냅니다.</p>
     */
    @JsonProperty("status")
    @Schema(
        description = "Training 상태", 
        example = "training",
        allowableValues = {
            "initialized", "starting", "stopping", "stopped", 
            "resource-allocating", "resource-allocated", "training", "trained", "error"
        },
        defaultValue = "initialized",
        maxLength = 64
    )
    private String status;
    
    /**
     * 이전 상태
     * 
     * <p>Training의 이전 상태를 나타냅니다.</p>
     */
    @JsonProperty("prev_status")
    @Schema(
        description = "이전 Training 상태", 
        example = "resource-allocated",
        allowableValues = {
            "initialized", "starting", "stopping", "stopped", 
            "resource-allocating", "resource-allocated", "training", "trained", "error"
        },
        maxLength = 64
    )
    private String prevStatus;
    
    /**
     * 진행률 정보
     * 
     * <p>Training 진행률을 관리하기 위한 JSON 객체입니다.</p>
     */
    @JsonProperty("progress")
    @Schema(
        description = "Training 진행률 정보 (JSON 객체)", 
        example = "{\"percentage\": 45}",
        type = "object"
    )
    private Object progress;
    
    /**
     * 리소스 정보
     * 
     * <p>Training에 할당된 리소스 정보입니다.</p>
     */
    @JsonProperty("resource")
    @Schema(
        description = "Training 리소스 정보 (cpu_quota, mem_quota, gpu_quota)", 
        example = "{\"cpu_quota\": \"2\", \"mem_quota\": \"8Gi\", \"gpu_quota\": \"1\"}",
        type = "object"
    )
    private Object resource;
    
    /**
     * 데이터셋 ID 목록
     * 
     * <p>Training에 사용된 데이터셋 ID들의 목록입니다.</p>
     */
    @JsonProperty("dataset_ids")
    @Schema(
        description = "Training에 사용된 데이터셋 ID 목록", 
        example = "[\"dataset-123\", \"dataset-456\"]"
    )
    private List<String> datasetIds;
    
    /**
     * 베이스 모델 ID
     * 
     * <p>Fine-tuning된 베이스 모델의 ID입니다.</p>
     */
    @JsonProperty("base_model_id")
    @Schema(
        description = "Fine-tuning된 베이스 모델 ID", 
        example = "model-789",
        format = "uuid"
    )
    private String baseModelId;
    
    /**
     * Trainer 파라미터
     * 
     * <p>Training에서 사용된 Trainer 파라미터입니다.</p>
     */
    @JsonProperty("params")
    @Schema(
        description = "Trainer 파라미터 (JSON 문자열)", 
        example = "{\"learning_rate\": 0.001, \"epochs\": 10}"
    )
    private String params;
    
    /**
     * 환경 변수
     * 
     * <p>Training 실행 환경 변수입니다.</p>
     */
    @JsonProperty("envs")
    @Schema(
        description = "Training 환경 변수 (JSON 객체)", 
        example = "{\"CUDA_VISIBLE_DEVICES\": \"0\"}",
        type = "object"
    )
    private Object envs;
    
    /**
     * 설명
     * 
     * <p>Training에 대한 설명입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Training 설명", 
        example = "Customer service chatbot fine-tuning"
    )
    private String description;
    
    /**
     * 프로젝트 ID
     * 
     * <p>Training이 속한 프로젝트의 ID입니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Training이 속한 프로젝트 ID", 
        example = "project-123",
        format = "uuid"
    )
    private String projectId;
    
    /**
     * 작업 ID
     * 
     * <p>Training과 연관된 작업 ID입니다.</p>
     */
    @JsonProperty("task_id")
    @Schema(
        description = "작업 ID", 
        example = "task-456",
        format = "uuid"
    )
    private String taskId;
    
    /**
     * Training 고유 식별자
     * 
     * <p>시스템에서 자동 생성되는 UUID 형식의 고유 식별자입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "Training 고유 식별자 (UUID)", 
        example = "training-123",
        format = "uuid"
    )
    private String id;
    
    /**
     * Trainer ID
     * 
     * <p>사용된 Trainer의 ID입니다.</p>
     */
    @JsonProperty("trainer_id")
    @Schema(
        description = "사용된 Trainer ID", 
        example = "trainer-abc",
        format = "uuid"
    )
    private String trainerId;
    
    /**
     * 생성 일시
     * 
     * <p>Training이 생성된 일시입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "생성 일시", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     * 
     * <p>Training이 마지막으로 수정된 일시입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "수정 일시", 
        example = "2025-08-15T11:30:00Z",
        format = "date-time"
    )
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;
}
