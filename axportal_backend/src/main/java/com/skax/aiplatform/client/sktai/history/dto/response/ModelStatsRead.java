package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 모델 통계 정보 응답 DTO
 * 
 * <p>SKTAI History API에서 모델 사용 통계 조회 시 반환되는 응답 데이터 구조입니다.
 * 모델별 성능 지표, 사용량 통계, 비용 정보 등을 포함한 집계 데이터를 제공합니다.</p>
 * 
 * <h3>통계 종류:</h3>
 * <ul>
 *   <li><strong>사용량 통계</strong>: 총 요청 수, 성공/실패 비율</li>
 *   <li><strong>성능 통계</strong>: 평균 응답 시간, 처리량</li>
 *   <li><strong>비용 통계</strong>: 토큰 사용량, 비용 집계</li>
 *   <li><strong>시간별 통계</strong>: 시간대별 사용 패턴</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 *   <li>모델 성능 모니터링 및 최적화</li>
 *   <li>비용 분석 및 예산 관리</li>
 *   <li>사용 패턴 분석 및 리소스 계획</li>
 *   <li>SLA 준수 여부 확인</li>
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
    description = "모델 통계 정보 응답",
    example = """
        {
          "data": {
            "total_requests": 1500,
            "success_requests": 1485,
            "failed_requests": 15,
            "success_rate": 99.0,
            "average_response_time": 1200,
            "total_tokens": 450000,
            "total_cost": 135.50,
            "models": {
              "gpt-4": {
                "requests": 800,
                "success_rate": 99.2,
                "avg_response_time": 1100
              },
              "gpt-3.5": {
                "requests": 700,
                "success_rate": 98.8,
                "avg_response_time": 1300
              }
            }
          }
        }
        """
)
public class ModelStatsRead {
    
    /**
     * 모델 통계 데이터
     * 
     * <p>모델 사용에 대한 종합적인 통계 정보를 포함하는 동적 객체입니다.
     * 다양한 모델 타입과 메트릭을 수용할 수 있도록 유연한 구조로 설계되었습니다.</p>
     * 
     * <h4>일반적으로 포함되는 통계:</h4>
     * <ul>
     *   <li><strong>총 요청 수</strong>: total_requests</li>
     *   <li><strong>성공/실패 건수</strong>: success_requests, failed_requests</li>
     *   <li><strong>성공률</strong>: success_rate (백분율)</li>
     *   <li><strong>평균 응답 시간</strong>: average_response_time (밀리초)</li>
     *   <li><strong>토큰 사용량</strong>: input_tokens, output_tokens, total_tokens</li>
     *   <li><strong>비용 정보</strong>: total_cost (달러 단위)</li>
     *   <li><strong>모델별 세부 통계</strong>: models 객체 내 모델별 상세 정보</li>
     *   <li><strong>시간대별 분포</strong>: hourly_distribution 등</li>
     * </ul>
     * 
     * @implNote 타입 안전성을 위해 구체적인 StatsDataItem DTO 배열을 사용합니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "모델 통계 데이터 (동적 구조)",
        example = """
            {
              "summary": {
                "total_requests": 1500,
                "success_requests": 1485,
                "failed_requests": 15,
                "success_rate": 99.0,
                "average_response_time_ms": 1200,
                "median_response_time_ms": 980,
                "p95_response_time_ms": 2100,
                "total_input_tokens": 225000,
                "total_output_tokens": 225000,
                "total_cost_usd": 135.50
              },
              "models": {
                "gpt-4": {
                  "model_id": "gpt-4",
                  "requests": 800,
                  "success_rate": 99.2,
                  "avg_response_time": 1100,
                  "total_tokens": 240000,
                  "cost": 72.00
                },
                "gpt-3.5-turbo": {
                  "model_id": "gpt-3.5-turbo", 
                  "requests": 700,
                  "success_rate": 98.8,
                  "avg_response_time": 1300,
                  "total_tokens": 210000,
                  "cost": 63.50
                }
              },
              "time_series": {
                "hourly": [
                  {"hour": "00", "requests": 45, "avg_response_time": 1150},
                  {"hour": "01", "requests": 38, "avg_response_time": 1200}
                ]
              },
              "error_analysis": {
                "timeout_errors": 8,
                "rate_limit_errors": 4,
                "server_errors": 3
              }
            }
            """
    )
    private List<StatsDataItem> data;
}