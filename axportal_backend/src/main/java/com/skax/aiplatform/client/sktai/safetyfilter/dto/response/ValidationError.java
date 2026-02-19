package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * SKTAI SafetyFilter API 유효성 검증 오류 상세 정보 DTO
 * 
 * <p>API 요청 시 발생하는 유효성 검증 오류의 상세 정보를 담는 데이터 구조입니다.
 * 필드별 오류 위치, 메시지, 타입 정보를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>loc</strong>: 오류가 발생한 필드 위치 (배열 형태)</li>
 *   <li><strong>msg</strong>: 오류 메시지</li>
 *   <li><strong>type</strong>: 오류 타입</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ValidationError error = ValidationError.builder()
 *     .loc(List.of("stopword"))
 *     .msg("Field required")
 *     .type("missing")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 * @see HTTPValidationError HTTP 유효성 검증 오류 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI SafetyFilter API 유효성 검증 오류 상세 정보",
    example = """
        {
          "loc": ["stopword"],
          "msg": "field required",
          "type": "missing"
        }
        """
)
public class ValidationError {
    
    /**
     * 오류 발생 위치
     * 
     * <p>오류가 발생한 필드의 위치를 나타내는 경로 배열입니다.
     * 중첩된 객체의 경우 계층 구조를 나타냅니다.</p>
     * 
     * @apiNote 예: ["request", "stopword"] → request.stopword 필드에서 오류 발생
     */
    @JsonProperty("loc")
    @Schema(
        description = "오류가 발생한 필드의 위치 경로",
        example = "[\"stopword\"]"
    )
    private List<Object> loc;
    
    /**
     * 오류 메시지
     * 
     * <p>발생한 오류에 대한 설명 메시지입니다.
     * 사용자가 이해할 수 있는 형태로 제공됩니다.</p>
     */
    @JsonProperty("msg")
    @Schema(
        description = "오류에 대한 설명 메시지",
        example = "field required"
    )
    private String msg;
    
    /**
     * 오류 타입
     * 
     * <p>발생한 오류의 종류를 나타내는 타입 정보입니다.
     * 시스템에서 정의한 오류 분류를 따릅니다.</p>
     * 
     * @implNote 일반적인 타입: missing, type_error, value_error 등
     */
    @JsonProperty("type")
    @Schema(
        description = "오류 타입 (missing, type_error, value_error 등)",
        example = "missing"
    )
    private String type;
}