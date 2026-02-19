package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model 태그 요청 DTO
 * 
 * <p>SKTAI Model에 태그를 추가하거나 제거할 때 사용하는 요청 데이터 구조입니다.
 * 모델의 분류와 검색을 위한 태그 관리를 지원합니다.</p>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>모델에 새로운 태그 추가</li>
 *   <li>모델에서 기존 태그 제거</li>
 *   <li>태그 기반 모델 분류 및 검색</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelTagRequest request = ModelTagRequest.builder()
 *     .name("LLM")
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
    description = "SKTAI Model 태그 요청 정보",
    example = """
        {
          "name": "LLM"
        }
        """
)
public class ModelTagRequest {
    
    /**
     * 태그 이름
     * 
     * <p>모델에 추가하거나 제거할 태그의 이름입니다.
     * 모델의 특성이나 용도를 나타내는 의미있는 이름을 사용합니다.</p>
     * 
     * @apiNote 태그는 대소문자를 구분하며, 중복될 수 없습니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "태그 이름 (모델의 특성이나 용도를 나타냄)", 
        example = "LLM",
        required = true,
        maxLength = 255
    )
    private String name;
}
