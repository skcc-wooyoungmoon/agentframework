package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Model Provider 응답 DTO
 * 
 * <p>SKTAI Model 시스템에서 모델 제공자 정보를 반환하는 응답 데이터 구조입니다.
 * 모델 제공자의 기본 정보와 시스템 관리 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 이름, 설명, 로고</li>
 *   <li><strong>시스템 정보</strong>: ID, 생성일시, 수정일시</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>모델 제공자 생성 후 반환</li>
 *   <li>모델 제공자 상세 조회</li>
 *   <li>모델 제공자 수정 후 반환</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ModelProviderCreate 모델 제공자 생성 요청
 * @see ModelProviderUpdate 모델 제공자 수정 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Provider 응답 정보",
    example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "name": "OpenAI",
          "description": "OpenAI is a leading AI research company",
          "logo": "https://example.com/openai-logo.png",
          "created_at": "2025-08-15T10:30:00",
          "updated_at": "2025-08-15T10:30:00"
        }
        """
)
public class ModelProviderRead {
    
    /**
     * 모델 제공자 ID
     * 
     * <p>시스템에서 자동 생성된 모델 제공자의 고유 식별자입니다.
     * UUID 형식으로 생성되며, 모든 API 호출에서 참조 키로 사용됩니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "모델 제공자 고유 식별자 (UUID)", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 모델 제공자 이름
     * 
     * <p>모델 제공자의 고유한 이름입니다.
     * 이 이름은 모델 제공자를 식별하는 데 사용됩니다.</p>
     */
    @JsonProperty("name")
    @Schema(
        description = "모델 제공자의 고유한 이름", 
        example = "OpenAI",
        maxLength = 255
    )
    private String name;
    
    /**
     * 모델 제공자 설명
     * 
     * <p>모델 제공자에 대한 자세한 설명입니다.
     * 조직의 배경, 특징, 제공하는 모델의 특성 등이 포함됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "모델 제공자에 대한 설명", 
        example = "OpenAI is a leading AI research company focused on creating safe and beneficial artificial intelligence"
    )
    private String description;
    
    /**
     * 로고 이미지
     * 
     * <p>모델 제공자의 로고 이미지 URL 또는 파일 경로입니다.
     * UI에서 제공자를 시각적으로 식별하는 데 사용됩니다.</p>
     */
    @JsonProperty("logo")
    @Schema(
        description = "로고 이미지 URL 또는 파일 경로", 
        example = "https://example.com/openai-logo.png",
        maxLength = 255
    )
    private String logo;
    
    /**
     * 생성 일시
     * 
     * <p>모델 제공자가 시스템에 등록된 일시입니다.
     * ISO 8601 형식으로 표현됩니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "모델 제공자 생성 일시", 
        example = "2025-08-15T10:30:00",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     * 
     * <p>모델 제공자 정보가 마지막으로 수정된 일시입니다.
     * ISO 8601 형식으로 표현됩니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "모델 제공자 수정 일시", 
        example = "2025-08-15T10:30:00",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
}
