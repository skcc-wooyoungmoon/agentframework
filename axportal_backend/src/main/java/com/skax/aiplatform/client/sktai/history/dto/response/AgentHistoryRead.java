package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 에이전트 사용 이력 조회 응답 DTO
 * 
 * <p>SKTAI History API에서 에이전트 사용 이력 목록 조회 시 반환되는 응답 데이터 구조입니다.
 * 에이전트 실행 기록, 작업 성과, 사용자 상호작용 정보 등을 포함한 페이징된 이력 데이터를 제공합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>data</strong>: 실제 에이전트 사용 이력 데이터 배열</li>
 *   <li><strong>payload</strong>: 페이징 정보 및 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 *   <li>에이전트 사용 현황 모니터링</li>
 *   <li>작업 성과 분석 및 최적화</li>
 *   <li>사용자별 에이전트 활용 패턴 분석</li>
 *   <li>시스템 리소스 사용량 추적</li>
 *   <li>에이전트 성능 벤치마킹</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 * @see Payload 페이징 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "에이전트 사용 이력 조회 응답 정보",
    example = """
        {
          "data": [
            {
              "session_id": "session-123",
              "agent_id": "agent-456",
              "user": "user@example.com",
              "start_time": "2025-09-24T10:30:00Z",
              "end_time": "2025-09-24T10:35:30Z",
              "status": "completed",
              "task_type": "data_analysis",
              "steps_executed": 12,
              "success_rate": 95.8
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 150,
              "items_per_page": 10
            }
          }
        }
        """
)
public class AgentHistoryRead {
    
    /**
     * 에이전트 사용 이력 데이터 목록
     * 
     * <p>각 항목은 에이전트 실행 세션에 대한 상세 정보를 포함합니다.
     * 동적 구조로 되어 있어 다양한 에이전트 타입의 정보를 수용할 수 있습니다.</p>
     * 
     * <h4>일반적으로 포함되는 필드:</h4>
     * <ul>
     *   <li><strong>session_id</strong>: 세션 고유 식별자</li>
     *   <li><strong>agent_id</strong>: 사용된 에이전트 식별자</li>
     *   <li><strong>user</strong>: 요청한 사용자 정보</li>
     *   <li><strong>start_time</strong>: 작업 시작 시간</li>
     *   <li><strong>end_time</strong>: 작업 완료 시간</li>
     *   <li><strong>duration</strong>: 실행 시간 (초)</li>
     *   <li><strong>status</strong>: 실행 상태 (completed/failed/running)</li>
     *   <li><strong>task_type</strong>: 작업 유형</li>
     *   <li><strong>steps_executed</strong>: 실행된 단계 수</li>
     *   <li><strong>tools_used</strong>: 사용된 도구 목록</li>
     *   <li><strong>success_rate</strong>: 작업 성공률</li>
     *   <li><strong>output_quality</strong>: 출력 품질 점수</li>
     * </ul>
     */
    @JsonProperty("data")
    @Schema(
        description = "에이전트 사용 이력 데이터 목록",
        example = """
            [
              {
                "session_id": "session-abc123",
                "agent_id": "research-agent-v2",
                "agent_name": "Research Assistant",
                "user": "john.doe@company.com",
                "project_id": "proj-789",
                "start_time": "2025-09-24T10:30:00Z",
                "end_time": "2025-09-24T10:35:30Z",
                "duration_seconds": 330,
                "status": "completed",
                "task_type": "market_research",
                "task_description": "Analyze competitor landscape",
                "steps_executed": 12,
                "steps_total": 12,
                "tools_used": ["web_search", "pdf_analyzer", "data_extractor"],
                "success_rate": 95.8,
                "output_quality_score": 8.7,
                "tokens_consumed": 15000,
                "cost": 4.50
              }
            ]
            """
    )
    private List<AgentHistoryRecord> data;
    
    /**
     * 페이징 정보 및 메타데이터
     * 
     * <p>조회 결과의 페이징 정보와 추가 메타데이터를 포함합니다.
     * 대용량 에이전트 이력 데이터의 효율적인 조회를 위한 페이징 관련 정보를 제공합니다.</p>
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이징 정보 및 메타데이터",
        implementation = Payload.class
    )
    private Payload payload;
}