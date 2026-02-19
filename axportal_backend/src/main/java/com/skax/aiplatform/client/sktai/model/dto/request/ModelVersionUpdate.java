package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Version 수정 요청 DTO
 * 
 * <p>SKTAI Model의 기존 버전을 수정할 때 사용하는 요청 데이터 구조입니다.
 * 버전의 메타데이터와 속성을 업데이트할 수 있습니다.</p>
 * 
 * <h3>수정 가능한 속성:</h3>
 * <ul>
 *   <li><strong>path</strong>: 모델 파일 경로</li>
 *   <li><strong>fine_tuning_id</strong>: Fine-tuning 작업 ID</li>
 *   <li><strong>description</strong>: 버전 설명</li>
 *   <li><strong>is_valid</strong>: 유효성 여부</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelVersionUpdate request = ModelVersionUpdate.builder()
 *     .description("성능 최적화 완료")
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
    description = "SKTAI Model Version 수정 요청 정보",
    example = """
        {
          "path": "/models/my-model-v2-updated.bin",
          "fine_tuning_id": "ft-123e4567-e89b-12d3-a456-426614174000",
          "description": "성능 최적화 완료",
          "is_valid": true
        }
        """
)
public class ModelVersionUpdate {
    
    /**
     * 모델 파일 경로
     * 
     * <p>수정할 모델 파일의 새로운 경로입니다.
     * null인 경우 기존 경로를 유지합니다.</p>
     */
    @JsonProperty("path")
    @Schema(
        description = "모델 파일 경로 (수정하지 않으려면 null)", 
        example = "/models/my-model-v2-updated.bin",
        maxLength = 255
    )
    private String path;
    
    /**
     * Fine-tuning 작업 ID
     * 
     * <p>새로운 fine-tuning 작업 ID로 업데이트합니다.
     * null인 경우 기존 값을 유지합니다.</p>
     */
    @JsonProperty("fine_tuning_id")
    @Schema(
        description = "Fine-tuning 작업 ID (수정하지 않으려면 null)", 
        example = "ft-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String fineTuningId;
    
    /**
     * 버전 설명
     * 
     * <p>버전의 새로운 설명으로 업데이트합니다.
     * null인 경우 기존 설명을 유지합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "버전 설명 (수정하지 않으려면 null)", 
        example = "성능 최적화 완료",
        maxLength = 1000
    )
    private String description;
    
    /**
     * 유효성 여부
     * 
     * <p>버전의 유효성 상태를 업데이트합니다.
     * null인 경우 기존 상태를 유지합니다.</p>
     */
    @JsonProperty("is_valid")
    @Schema(
        description = "버전 유효성 여부 (수정하지 않으려면 null)", 
        example = "true"
    )
    private Boolean isValid;
}
