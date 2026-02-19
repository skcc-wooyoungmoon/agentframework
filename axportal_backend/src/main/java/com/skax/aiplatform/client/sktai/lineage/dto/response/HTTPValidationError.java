package com.skax.aiplatform.client.sktai.lineage.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * HTTP 유효성 검증 오류 응답 DTO
 * 
 * <p>API 요청 시 발생하는 HTTP 422 Unprocessable Entity 오류의 세부 정보를 포함하는 DTO입니다.
 * Pydantic 유효성 검증 오류들의 집합을 나타냅니다.</p>
 * 
 * <h3>오류 구조:</h3>
 * <ul>
 *   <li><strong>detail</strong>: 개별 유효성 검증 오류들의 목록</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Lineage 생성 요청 시 필수 필드 누락</li>
 *   <li>잘못된 데이터 타입 전송</li>
 *   <li>비즈니스 규칙 위반</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 * @see ValidationError 개별 유효성 검증 오류
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "HTTP 유효성 검증 오류 응답 (422 Unprocessable Entity)",
    example = """
        {
          "detail": [
            {
              "loc": ["source_key"],
              "msg": "field required",
              "type": "value_error.missing"
            },
            {
              "loc": ["target_key"],
              "msg": "field required", 
              "type": "value_error.missing"
            }
          ]
        }
        """
)
public class HTTPValidationError {
    
    /**
     * 유효성 검증 오류 목록
     * 
     * <p>API 요청에서 발생한 모든 유효성 검증 오류들의 세부 정보를 포함합니다.
     * 각 오류는 발생한 필드 위치, 오류 메시지, 오류 타입을 포함합니다.</p>
     * 
     * @apiNote 여러 필드에서 동시에 오류가 발생할 수 있으므로 배열로 처리됩니다.
     */
    @JsonProperty("detail")
    @Schema(
        description = "유효성 검증 오류들의 세부 정보 목록",
        required = true
    )
    private List<ValidationError> detail;
}