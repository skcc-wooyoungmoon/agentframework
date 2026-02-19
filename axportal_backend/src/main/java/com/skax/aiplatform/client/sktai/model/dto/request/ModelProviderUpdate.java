package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Provider 수정 요청 DTO
 * 
 * <p>SKTAI Model 시스템에서 기존 모델 제공자 정보를 수정하기 위한 요청 데이터 구조입니다.
 * 모든 필드는 선택사항이며, 제공된 필드만 업데이트됩니다.</p>
 * 
 * <h3>수정 가능한 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: 모델 제공자 이름</li>
 *   <li><strong>description</strong>: 모델 제공자 설명</li>
 *   <li><strong>logo</strong>: 로고 이미지 URL 또는 경로</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelProviderUpdate request = ModelProviderUpdate.builder()
 *     .name("OpenAI Corp")
 *     .description("Updated description for OpenAI")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ModelProviderRead 수정된 모델 제공자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Provider 수정 요청 정보",
    example = """
        {
          "name": "OpenAI Corp",
          "description": "Updated description for OpenAI Corporation",
          "logo": "https://example.com/openai-new-logo.png"
        }
        """
)
public class ModelProviderUpdate {
    
    /**
     * 모델 제공자 이름
     * 
     * <p>수정할 모델 제공자의 이름입니다.
     * null인 경우 기존 이름이 유지됩니다.</p>
     * 
     * @apiNote 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "수정할 모델 제공자 이름 (선택사항)", 
        example = "OpenAI Corp",
        maxLength = 255
    )
    private String name;
    
    /**
     * 모델 제공자 설명
     * 
     * <p>수정할 모델 제공자의 설명입니다.
     * null인 경우 기존 설명이 유지됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "수정할 모델 제공자 설명 (선택사항)", 
        example = "Updated description for OpenAI Corporation"
    )
    private String description;
    
    /**
     * 로고 이미지
     * 
     * <p>수정할 로고 이미지 URL 또는 파일 경로입니다.
     * null인 경우 기존 로고가 유지됩니다.</p>
     * 
     * @apiNote 최대 255자까지 입력 가능하며, 유효한 URL 형식을 권장합니다.
     */
    @JsonProperty("logo")
    @Schema(
        description = "수정할 로고 이미지 URL 또는 파일 경로 (선택사항)", 
        example = "https://example.com/openai-new-logo.png",
        maxLength = 255
    )
    private String logo;
}
