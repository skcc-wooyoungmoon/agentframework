package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Model Version 응답 DTO
 * 
 * <p>SKTAI Model Version의 상세 정보를 담는 응답 데이터 구조입니다.
 * 모델의 버전 관리와 이력 추적을 지원합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 버전 번호, 부모 모델 ID</li>
 *   <li><strong>파일 정보</strong>: 모델 파일 경로</li>
 *   <li><strong>메타데이터</strong>: 설명, 유효성, Fine-tuning 정보</li>
 *   <li><strong>시간 정보</strong>: 생성/수정 시간</li>
 * </ul>
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
    description = "SKTAI Model Version 응답 정보",
    example = """
        {
          "id": "mv-123e4567-e89b-12d3-a456-426614174000",
          "parent_id": "m-123e4567-e89b-12d3-a456-426614174000",
          "version": 2,
          "path": "/models/my-model-v2.bin",
          "fine_tuning_id": "ft-123e4567-e89b-12d3-a456-426614174000",
          "description": "성능 개선 버전",
          "is_valid": true,
          "created_at": "2025-09-01T10:30:00",
          "updated_at": "2025-09-01T10:30:00"
        }
        """
)
public class ModelVersionRead {
    
    /**
     * 버전 ID
     * 
     * <p>모델 버전의 고유 식별자입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "모델 버전 고유 식별자", 
        example = "mv-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 부모 모델 ID
     * 
     * <p>이 버전이 속한 부모 모델의 ID입니다.</p>
     */
    @JsonProperty("parent_id")
    @Schema(
        description = "부모 모델 ID", 
        example = "m-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String parentId;
    
    /**
     * 버전 번호
     * 
     * <p>모델의 버전 번호입니다. 1부터 시작하며 순차적으로 증가합니다.</p>
     */
    @JsonProperty("version")
    @Schema(
        description = "버전 번호 (1부터 시작)", 
        example = "2",
        minimum = "1"
    )
    private Integer version;
    
    /**
     * 모델 파일 경로
     * 
     * <p>이 버전의 모델 파일이 저장된 경로입니다.</p>
     */
    @JsonProperty("path")
    @Schema(
        description = "모델 파일 경로", 
        example = "/models/my-model-v2.bin",
        maxLength = 255
    )
    private String path;
    
    /**
     * Fine-tuning 작업 ID
     * 
     * <p>이 버전이 fine-tuning 작업의 결과인 경우 해당 작업의 ID입니다.</p>
     */
    @JsonProperty("fine_tuning_id")
    @Schema(
        description = "Fine-tuning 작업 ID", 
        example = "ft-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String fineTuningId;
    
    /**
     * 버전 설명
     * 
     * <p>이 버전의 변경사항이나 특징을 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "버전 설명", 
        example = "성능 개선 버전"
    )
    private String description;
    
    /**
     * 유효성 여부
     * 
     * <p>이 버전의 유효성 상태를 나타냅니다.</p>
     */
    @JsonProperty("is_valid")
    @Schema(
        description = "버전 유효성 여부", 
        example = "true"
    )
    private Boolean isValid;
    
    /**
     * 생성 시간
     * 
     * <p>버전이 생성된 시간입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "생성 시간", 
        example = "2025-09-01T10:30:00"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     * 
     * <p>버전이 마지막으로 수정된 시간입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "수정 시간", 
        example = "2025-09-01T10:30:00"
    )
    private LocalDateTime updatedAt;
}
