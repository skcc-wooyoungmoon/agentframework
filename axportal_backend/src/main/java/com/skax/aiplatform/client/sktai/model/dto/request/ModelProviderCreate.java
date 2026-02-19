package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Provider 생성 요청 DTO
 * 
 * <p>SKTAI Model 시스템에서 새로운 모델 제공자를 생성하기 위한 요청 데이터 구조입니다.
 * 모델 제공자는 모델을 제공하는 조직, 회사 또는 서비스를 나타냅니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: 모델 제공자의 고유한 이름</li>
 * </ul>
 * 
 * <h3>선택 정보:</h3>
 * <ul>
 *   <li><strong>description</strong>: 모델 제공자에 대한 설명</li>
 *   <li><strong>logo</strong>: 로고 이미지 URL 또는 경로</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelProviderCreate request = ModelProviderCreate.builder()
 *     .name("OpenAI")
 *     .description("OpenAI is a leading AI research company")
 *     .logo("https://example.com/openai-logo.png")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ModelProviderRead 생성된 모델 제공자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Provider 생성 요청 정보",
    example = """
        {
          "name": "OpenAI",
          "description": "OpenAI is a leading AI research company",
          "logo": "https://example.com/openai-logo.png"
        }
        """
)
public class ModelProviderCreate {
    
    /**
     * 모델 제공자 이름
     * 
     * <p>모델 제공자의 고유한 이름입니다.
     * 이 이름은 모델 제공자를 식별하는 데 사용되며, 중복될 수 없습니다.</p>
     * 
     * @apiNote 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "모델 제공자의 고유한 이름", 
        example = "OpenAI",
        required = true,
        maxLength = 255
    )
    private String name;
    
    /**
     * 모델 제공자 설명
     * 
     * <p>모델 제공자에 대한 자세한 설명입니다.
     * 조직의 배경, 특징, 제공하는 모델의 특성 등을 설명할 수 있습니다.</p>
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
     * 
     * @apiNote 최대 255자까지 입력 가능하며, 유효한 URL 형식을 권장합니다.
     */
    @JsonProperty("logo")
    @Schema(
        description = "로고 이미지 URL 또는 파일 경로", 
        example = "https://example.com/openai-logo.png",
        maxLength = 255
    )
    private String logo;
}
