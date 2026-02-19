package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Agent API 공통 응답 DTO
 * 
 * <p>SKTAI Agent API의 모든 엔드포인트에서 사용하는 표준화된 응답 구조입니다.
 * API 호출 결과의 메타데이터와 실제 데이터를 포함합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>timestamp</strong>: 응답 생성 시간</li>
 *   <li><strong>code</strong>: HTTP 응답 코드</li>
 *   <li><strong>detail</strong>: 응답 상세 메시지</li>
 *   <li><strong>traceId</strong>: 요청 추적 ID</li>
 *   <li><strong>data</strong>: 실제 응답 데이터</li>
 *   <li><strong>payload</strong>: 페이징 정보 등 추가 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CommonResponse response = client.someApiCall();
 * if (response.getCode() == 200) {
 *     Object data = response.getData();
 *     // 데이터 처리
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent API 공통 응답 정보",
    example = """
        {
          "timestamp": "2025-08-15T10:30:00Z",
          "code": 200,
          "detail": "Successful Response",
          "traceId": "trace-12345",
          "data": {},
          "payload": null
        }
        """
)
public class CommonResponse {
    
    /**
     * 응답 생성 시간
     * 
     * <p>API 응답이 생성된 시간을 ISO 8601 형식으로 표현합니다.
     * 클라이언트에서 응답 처리 시간 계산 등에 활용할 수 있습니다.</p>
     */
    @JsonProperty("timestamp")
    @Schema(
        description = "응답 생성 시간 (ISO 8601 형식)",
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime timestamp;
    
    /**
     * HTTP 응답 코드
     * 
     * <p>API 호출 결과를 나타내는 HTTP 상태 코드입니다.
     * 200번대는 성공, 400번대는 클라이언트 오류, 500번대는 서버 오류를 의미합니다.</p>
     */
    @JsonProperty("code")
    @Schema(
        description = "HTTP 응답 코드",
        example = "200",
        minimum = "100",
        maximum = "599"
    )
    private Integer code;
    
    /**
     * 응답 상세 메시지
     * 
     * <p>API 호출 결과에 대한 상세한 설명 메시지입니다.
     * 성공 시에는 "Successful Response", 오류 시에는 구체적인 오류 내용이 포함됩니다.</p>
     */
    @JsonProperty("detail")
    @Schema(
        description = "응답 상세 메시지",
        example = "Successful Response"
    )
    private String detail;
    
    /**
     * 요청 추적 ID
     * 
     * <p>요청의 전체 처리 과정을 추적할 수 있는 고유 식별자입니다.
     * 로깅 및 디버깅 목적으로 사용됩니다.</p>
     */
    @JsonProperty("traceId")
    @Schema(
        description = "요청 추적 ID (디버깅 및 로깅용)",
        example = "trace-12345"
    )
    private String traceId;
    
    /**
     * 실제 응답 데이터
     * 
     * <p>API의 실제 결과 데이터입니다.
     * 데이터 타입은 API에 따라 달라지므로 Object 타입으로 정의됩니다.</p>
     * 
     * @apiNote 구체적인 데이터 타입은 각 API의 응답 스키마를 참조하세요.
     */
    @JsonProperty("data")
    @Schema(
        description = "API 응답 데이터 (타입은 API에 따라 다름)",
        example = "{}"
    )
    private Object data;
    
    /**
     * 추가 메타데이터
     * 
     * <p>페이징 정보 등 응답과 관련된 추가적인 메타데이터입니다.
     * 페이징이 필요한 API의 경우 페이지 정보가 포함됩니다.</p>
     */
    @JsonProperty("payload")
    @Schema(
        description = "추가 메타데이터 (페이징 정보 등)",
        example = "null"
    )
    private Payload payload;
}
