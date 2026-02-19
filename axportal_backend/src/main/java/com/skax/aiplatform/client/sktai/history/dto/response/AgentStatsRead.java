package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 에이전트 통계 정보 응답 DTO
 * 
 * <p>SKTAI History API에서 에이전트 사용 통계 조회 시 반환되는 응답 데이터 구조입니다.
 * 에이전트별 성능 지표, 작업 성공률, 리소스 사용량 등을 포함한 집계 데이터를 제공합니다.</p>
 * 
 * <h3>통계 종류:</h3>
 * <ul>
 *   <li><strong>사용량 통계</strong>: 총 세션 수, 완료/실패 비율</li>
 *   <li><strong>성능 통계</strong>: 평균 실행 시간, 작업 성공률</li>
 *   <li><strong>품질 통계</strong>: 출력 품질 점수, 사용자 만족도</li>
 *   <li><strong>리소스 통계</strong>: 도구 사용량, 토큰 소비량</li>
 *   <li><strong>시간별 통계</strong>: 시간대별 사용 패턴</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 *   <li>에이전트 성능 모니터링 및 최적화</li>
 *   <li>작업 효율성 분석 및 개선</li>
 *   <li>사용 패턴 분석 및 리소스 계획</li>
 *   <li>에이전트 버전별 성능 비교</li>
 *   <li>ROI 분석 및 비용 최적화</li>
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
    description = "에이전트 통계 정보 응답",
    example = """
        {
          "data": {
            "total_sessions": 850,
            "completed_sessions": 798,
            "failed_sessions": 52,
            "success_rate": 93.9,
            "average_duration": 245,
            "average_quality_score": 8.2,
            "total_tokens": 125000,
            "agents": {
              "research-agent": {
                "sessions": 400,
                "success_rate": 95.5,
                "avg_duration": 220,
                "avg_quality": 8.5
              },
              "analysis-agent": {
                "sessions": 450,
                "success_rate": 92.4,
                "avg_duration": 270,
                "avg_quality": 7.9
              }
            }
          }
        }
        """
)
public class AgentStatsRead {
    
    /**
     * 에이전트 통계 데이터
     * 
     * <p>에이전트 사용에 대한 종합적인 통계 정보를 포함하는 동적 객체입니다.
     * 다양한 에이전트 타입과 메트릭을 수용할 수 있도록 유연한 구조로 설계되었습니다.</p>
     * 
     * <h4>일반적으로 포함되는 통계:</h4>
     * <ul>
     *   <li><strong>총 세션 수</strong>: total_sessions</li>
     *   <li><strong>완료/실패 건수</strong>: completed_sessions, failed_sessions</li>
     *   <li><strong>성공률</strong>: success_rate (백분율)</li>
     *   <li><strong>평균 실행 시간</strong>: average_duration (초)</li>
     *   <li><strong>품질 점수</strong>: average_quality_score, median_quality_score</li>
     *   <li><strong>토큰 사용량</strong>: total_tokens, average_tokens_per_session</li>
     *   <li><strong>도구 사용 통계</strong>: tools_usage 객체</li>
     *   <li><strong>에이전트별 세부 통계</strong>: agents 객체 내 에이전트별 상세 정보</li>
     *   <li><strong>작업 유형별 분포</strong>: task_type_distribution</li>
     *   <li><strong>시간대별 분포</strong>: hourly_distribution 등</li>
     * </ul>
     * 
     * @implNote 타입 안전성을 위해 구체적인 StatsDataItem DTO 배열을 사용합니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "에이전트 통계 데이터 (동적 구조)",
        example = """
            {
              "summary": {
                "total_sessions": 850,
                "completed_sessions": 798,
                "failed_sessions": 52,
                "success_rate": 93.9,
                "average_duration_seconds": 245,
                "median_duration_seconds": 180,
                "p95_duration_seconds": 480,
                "average_quality_score": 8.2,
                "total_tokens_consumed": 125000,
                "total_cost_usd": 37.50
              },
              "agents": {
                "research-agent-v2": {
                  "agent_id": "research-agent-v2",
                  "agent_name": "Research Assistant v2",
                  "sessions": 400,
                  "success_rate": 95.5,
                  "avg_duration": 220,
                  "avg_quality_score": 8.5,
                  "total_tokens": 60000,
                  "cost": 18.00
                },
                "analysis-agent-v1": {
                  "agent_id": "analysis-agent-v1",
                  "agent_name": "Data Analysis Agent",
                  "sessions": 450,
                  "success_rate": 92.4,
                  "avg_duration": 270,
                  "avg_quality_score": 7.9,
                  "total_tokens": 65000,
                  "cost": 19.50
                }
              },
              "tools_usage": {
                "web_search": {"usage_count": 650, "success_rate": 98.2},
                "pdf_analyzer": {"usage_count": 320, "success_rate": 94.7},
                "data_extractor": {"usage_count": 280, "success_rate": 91.8}
              },
              "task_type_distribution": {
                "research": 45.2,
                "analysis": 32.1,
                "summarization": 22.7
              },
              "time_series": {
                "hourly": [
                  {"hour": "00", "sessions": 25, "avg_duration": 230},
                  {"hour": "01", "sessions": 18, "avg_duration": 245}
                ]
              },
              "error_analysis": {
                "timeout_errors": 28,
                "tool_failures": 15,
                "parsing_errors": 9
              }
            }
            """
    )
    private List<StatsDataItem> data;
}