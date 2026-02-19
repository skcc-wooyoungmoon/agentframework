package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * SKTAI SafetyFilter API HTTP 유효성 검증 오류 응답 DTO
 * 
 * <p>SafetyFilter API에서 422 Unprocessable Entity 응답 시 반환되는 오류 정보입니다.
 * 여러 필드의 유효성 검증 오류 목록을 포함합니다.</p>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>필수 필드 누락</strong>: stopword 필드가 비어있을 때</li>
 *   <li><strong>형식 오류</strong>: UUID 형식이 올바르지 않을 때</li>
 *   <li><strong>길이 제한 초과</strong>: 문자열 길이가 제한을 초과할 때</li>
 *   <li><strong>타입 오류</strong>: 필드 타입이 올바르지 않을 때</li>
 * </ul>
 * 
 * <h3>오류 처리 방법:</h3>
 * <pre>
 * try {
 *     safetyFilterClient.registerSafetyFilter(request);
 * } catch (BusinessException e) {
 *     // SktaiErrorDecoder에서 변환된 예외 처리
 *     log.error("SafetyFilter 등록 실패: {}", e.getMessage());
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 * @see ValidationError 개별 유효성 검증 오류 상세
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI SafetyFilter API HTTP 유효성 검증 오류 응답",
    example = """
        {
          "detail": [
            {
              "loc": ["stopword"],
              "msg": "field required",
              "type": "missing"
            }
          ]
        }
        """
)
public class HTTPValidationError {
    
    /**
     * 유효성 검증 오류 상세 목록
     * 
     * <p>API 요청에서 발생한 모든 유효성 검증 오류의 상세 정보입니다.
     * 각 오류는 필드 위치, 메시지, 타입 정보를 포함합니다.</p>
     * 
     * @apiNote 빈 배열일 수도 있지만, 422 응답에서는 보통 1개 이상의 오류가 포함됩니다.
     */
    @JsonProperty("detail")
    @Schema(
        description = "유효성 검증 오류 상세 목록",
        example = """
            [
              {
                "loc": ["stopword"],
                "msg": "field required", 
                "type": "missing"
              },
              {
                "loc": ["group_id"],
                "msg": "invalid UUID format",
                "type": "type_error.uuid"
              }
            ]
            """
    )
    private List<ValidationError> detail;
}