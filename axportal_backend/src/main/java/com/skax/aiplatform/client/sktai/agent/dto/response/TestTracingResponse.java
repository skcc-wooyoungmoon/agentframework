package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent 테스트 추적 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 클라이언트와 앱의 테스트 추적 정보를 담는 응답 데이터 구조입니다.
 * 테스트 실행, 성능 모니터링, 디버깅 정보 등을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>trace_id</strong>: 테스트 추적 식별자</li>
 *   <li><strong>client_id</strong>: 클라이언트 식별자</li>
 *   <li><strong>app_id</strong>: 애플리케이션 식별자</li>
 *   <li><strong>status</strong>: 테스트 상태</li>
 *   <li><strong>message</strong>: 테스트 결과 메시지</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>특정 클라이언트/앱에 대한 테스트 추적 정보 조회</li>
 *   <li>테스트 실행 상태 및 결과 확인</li>
 *   <li>디버깅 및 성능 분석</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see TestTracingRequest 테스트 추적 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent 테스트 추적 응답 정보",
    example = """
        {
          "trace_id": "trace-12345",
          "client_id": "client-001",
          "app_id": "app-456",
          "status": "completed",
          "message": "Test completed successfully"
        }
        """
)
public class TestTracingResponse {
    
    /**
     * 테스트 추적 식별자
     * 
     * <p>특정 테스트 실행을 고유하게 식별하는 ID입니다.
     * 로그 추적, 성능 분석 등에 사용됩니다.</p>
     */
    @JsonProperty("trace_id")
    @Schema(
        description = "테스트 추적 고유 식별자", 
        example = "trace-12345"
    )
    private String traceId;
    
    /**
     * 클라이언트 식별자
     * 
     * <p>테스트를 요청한 클라이언트의 식별자입니다.</p>
     */
    @JsonProperty("client_id")
    @Schema(
        description = "클라이언트 식별자", 
        example = "client-001"
    )
    private String clientId;
    
    /**
     * 애플리케이션 식별자
     * 
     * <p>테스트 대상 애플리케이션의 식별자입니다.</p>
     */
    @JsonProperty("app_id")
    @Schema(
        description = "애플리케이션 식별자", 
        example = "app-456"
    )
    private String appId;
    
    /**
     * 테스트 상태
     * 
     * <p>테스트의 현재 상태를 나타냅니다.
     * (예: running, completed, failed, cancelled)</p>
     */
    @JsonProperty("status")
    @Schema(
        description = "테스트 실행 상태", 
        example = "completed",
        allowableValues = {"running", "completed", "failed", "cancelled"}
    )
    private String status;
    
    /**
     * 테스트 결과 메시지
     * 
     * <p>테스트 실행 결과에 대한 상세 메시지입니다.
     * 성공, 실패 원인, 경고 등의 정보를 포함합니다.</p>
     */
    @JsonProperty("message")
    @Schema(
        description = "테스트 결과 메시지", 
        example = "Test completed successfully"
    )
    private String message;
}
