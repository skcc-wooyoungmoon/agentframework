package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Version 생성 요청 DTO
 * 
 * <p>SKTAI Model의 새로운 버전을 생성할 때 사용하는 요청 데이터 구조입니다.
 * 모델의 버전 관리와 업데이트를 지원합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>path</strong>: 모델 파일 경로</li>
 * </ul>
 * 
 * <h3>선택 정보:</h3>
 * <ul>
 *   <li><strong>fine_tuning_id</strong>: Fine-tuning 작업 ID</li>
 *   <li><strong>description</strong>: 버전 설명</li>
 *   <li><strong>is_valid</strong>: 유효성 여부</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelVersionCreate request = ModelVersionCreate.builder()
 *     .path("/models/my-model-v2.bin")
 *     .description("성능 개선 버전")
 *     .isValid(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Version 생성 요청 정보",
    example = """
        {
          "path": "/models/my-model-v2.bin",
          "fine_tuning_id": "ft-123e4567-e89b-12d3-a456-426614174000",
          "description": "성능 개선 버전",
          "is_valid": true
        }
        """
)
public class ModelVersionCreate {
    
    /**
     * 모델 파일 경로
     * 
     * <p>새로운 버전의 모델 파일이 저장된 경로입니다.
     * 모델 파일 업로드 후 받은 경로를 사용해야 합니다.</p>
     * 
     * @apiNote 반드시 유효한 모델 파일 경로여야 하며, 접근 가능해야 합니다.
     */
    @JsonProperty("path")
    @Schema(
        description = "모델 파일 경로 (파일 업로드 후 받은 경로)", 
        example = "/models/my-model-v2.bin",
        required = true,
        maxLength = 255
    )
    private String path;
    
    /**
     * Fine-tuning 작업 ID
     * 
     * <p>이 버전이 fine-tuning 작업의 결과인 경우 해당 작업의 ID입니다.
     * Fine-tuning을 통해 생성된 모델 버전을 추적할 수 있습니다.</p>
     */
    @JsonProperty("fine_tuning_id")
    @Schema(
        description = "Fine-tuning 작업 ID (해당하는 경우)", 
        example = "ft-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String fineTuningId;
    
    /**
     * 버전 설명
     * 
     * <p>새로운 버전의 변경사항이나 특징을 설명하는 텍스트입니다.
     * 버전 관리와 이력 추적에 도움이 됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "버전 설명 (변경사항이나 특징)", 
        example = "성능 개선 버전",
        maxLength = 1000
    )
    private String description;
    
    /**
     * 유효성 여부
     * 
     * <p>새로운 버전의 유효성 상태를 나타냅니다.
     * 기본값은 true이며, 테스트 중인 버전은 false로 설정할 수 있습니다.</p>
     */
    @JsonProperty("is_valid")
    @Schema(
        description = "버전 유효성 여부", 
        example = "true",
        defaultValue = "true"
    )
    private Boolean isValid;
}
