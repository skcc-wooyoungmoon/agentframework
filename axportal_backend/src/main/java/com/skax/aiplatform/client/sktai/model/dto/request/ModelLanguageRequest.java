package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model 언어 요청 DTO
 * 
 * <p>SKTAI Model이 지원하는 언어를 추가하거나 제거할 때 사용하는 요청 데이터 구조입니다.
 * 모델의 다국어 지원 기능을 관리합니다.</p>
 * 
 * <h3>지원 언어 예시:</h3>
 * <ul>
 *   <li><strong>Korean</strong>: 한국어</li>
 *   <li><strong>English</strong>: 영어</li>
 *   <li><strong>Japanese</strong>: 일본어</li>
 *   <li><strong>Chinese</strong>: 중국어</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelLanguageRequest request = ModelLanguageRequest.builder()
 *     .name("Korean")
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
    description = "SKTAI Model 언어 요청 정보",
    example = """
        {
          "name": "Korean"
        }
        """
)
public class ModelLanguageRequest {
    
    /**
     * 언어 이름
     * 
     * <p>모델이 지원하는 언어의 이름입니다.
     * 표준화된 언어 이름을 사용하여 일관성을 유지합니다.</p>
     * 
     * @implNote 언어 이름은 영어로 표기하며, 첫 글자는 대문자를 사용합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "언어 이름 (모델이 지원하는 자연어)", 
        example = "Korean",
        required = true,
        maxLength = 64
    )
    private String name;
}
