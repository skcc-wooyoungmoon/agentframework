package com.skax.aiplatform.client.sktai.lineage.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유효성 검증 오류 정보 DTO
 * 
 * <p>API 요청 시 특정 필드에서 발생한 유효성 검증 오류를 나타내는 DTO입니다.
 * Pydantic 유효성 검증 오류의 세부 정보를 포함합니다.</p>
 * 
 * <h3>오류 정보:</h3>
 * <ul>
 *   <li><strong>loc</strong>: 오류가 발생한 필드 경로</li>
 *   <li><strong>msg</strong>: 오류 메시지</li>
 *   <li><strong>type</strong>: 오류 타입</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "유효성 검증 오류 정보",
    example = """
        {
          "loc": ["source_key"],
          "msg": "field required",
          "type": "value_error.missing"
        }
        """
)
public class ValidationError {
    
    /**
     * 오류 발생 필드 경로
     * 
     * <p>오류가 발생한 필드의 경로를 배열로 나타냅니다.
     * 중첩된 객체의 경우 경로가 여러 단계로 표현됩니다.</p>
     */
    @JsonProperty("loc")
    @Schema(
        description = "오류가 발생한 필드 경로",
        example = "[\"source_key\"]",
        required = true
    )
    private Object[] loc;
    
    /**
     * 오류 메시지
     * 
     * <p>사용자가 이해할 수 있는 오류 설명 메시지입니다.</p>
     */
    @JsonProperty("msg")
    @Schema(
        description = "오류 설명 메시지",
        example = "field required",
        required = true
    )
    private String msg;
    
    /**
     * 오류 타입
     * 
     * <p>유효성 검증 오류의 구체적인 타입을 나타냅니다.</p>
     */
    @JsonProperty("type")
    @Schema(
        description = "오류 타입",
        example = "value_error.missing",
        required = true
    )
    private String type;
}