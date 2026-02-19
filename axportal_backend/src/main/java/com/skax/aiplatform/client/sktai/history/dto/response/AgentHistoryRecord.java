package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 에이전트 사용 이력 개별 레코드 DTO
 * 
 * <p>SKTAI History API에서 반환되는 개별 에이전트 사용 이력 레코드의 구조입니다.
 * 에이전트 실행 세부 정보, 작업 성과, 사용자 상호작용 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>세션 정보</strong>: 세션 ID, 사용자, 시간</li>
 *   <li><strong>에이전트 정보</strong>: 에이전트 ID, 버전, 타입</li>
 *   <li><strong>성과 정보</strong>: 실행 시간, 작업 결과</li>
 *   <li><strong>상태 정보</strong>: 완료/실패, 오류 메시지</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "에이전트 사용 이력 개별 레코드",
    example = """
        {
          "session_id": "session-123-456",
          "agent_app_id": "agent-gpt4",
          "user": "user@example.com",
          "start_time": "2025-09-24T10:30:00Z",
          "end_time": "2025-09-24T10:32:30Z",
          "status": "completed"
        }
        """
)
public class AgentHistoryRecord {
    
    /**
     * 세션 고유 식별자
     */
    @JsonProperty("session_id")
    @Schema(description = "세션 고유 식별자", example = "session-123-456")
    private String sessionId;
    
    /**
     * 에이전트 애플리케이션 식별자
     */
    @JsonProperty("agent_app_id")
    @Schema(description = "에이전트 애플리케이션 식별자", example = "agent-gpt4")
    private String agentAppId;
    
    /**
     * 에이전트 애플리케이션 버전
     */
    @JsonProperty("agent_app_version")
    @Schema(description = "에이전트 애플리케이션 버전", example = "v1.2.0")
    private String agentAppVersion;
    
    /**
     * 프로젝트 식별자
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 식별자", example = "project-123")
    private String projectId;
    
    /**
     * 애플리케이션 식별자
     */
    @JsonProperty("app_id")
    @Schema(description = "애플리케이션 식별자", example = "app-456")
    private String appId;
    
    /**
     * 사용자 정보
     */
    @JsonProperty("user")
    @Schema(description = "사용자 식별자", example = "user@example.com")
    private String user;
    
    /**
     * 세션 시작 시간
     */
    @JsonProperty("start_time")
    @Schema(description = "세션 시작 시간 (ISO 8601 형식)", example = "2025-09-24T10:30:00Z")
    private String startTime;
    
    /**
     * 세션 종료 시간
     */
    @JsonProperty("end_time")
    @Schema(description = "세션 종료 시간 (ISO 8601 형식)", example = "2025-09-24T10:32:30Z")
    private String endTime;
    
    /**
     * 실행 시간 (초)
     */
    @JsonProperty("duration")
    @Schema(description = "실행 시간 (초)", example = "150")
    private Integer duration;
    
    /**
     * 세션 상태
     */
    @JsonProperty("status")
    @Schema(description = "세션 상태", example = "completed", allowableValues = {"completed", "failed", "timeout", "cancelled"})
    private String status;
    
    /**
     * 서빙 타입
     */
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입", example = "sync")
    private String servingType;
    
    /**
     * 에이전트 서빙 식별자
     */
    @JsonProperty("agent_app_serving_id")
    @Schema(description = "에이전트 서빙 식별자", example = "serving-789")
    private String agentAppServingId;
    
    /**
     * 회사 정보
     */
    @JsonProperty("company")
    @Schema(description = "회사 정보", example = "SKTelecom")
    private String company;
    
    /**
     * 부서 정보
     */
    @JsonProperty("department")
    @Schema(description = "부서 정보", example = "AI Platform")
    private String department;
    
    /**
     * API 키
     */
    @JsonProperty("api_key")
    @Schema(description = "API 키", example = "ak-123456")
    private String apiKey;
    
    /**
     * 오류 메시지 (실패 시)
     */
    @JsonProperty("error_message")
    @Schema(description = "오류 메시지 (실패 시)", example = "Agent execution timeout")
    private String errorMessage;
    
    /**
     * 작업 결과 품질 점수
     */
    @JsonProperty("quality_score")
    @Schema(description = "작업 결과 품질 점수 (0-100)", example = "85")
    private Integer qualityScore;
}