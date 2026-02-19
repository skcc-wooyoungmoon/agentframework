package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 통계 데이터 항목 DTO
 * 
 * <p>SKTAI History API 통계 응답에서 사용되는 개별 통계 항목의 구조입니다.
 * 그룹핑 기준별로 집계된 통계 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>그룹핑 정보</strong>: 프로젝트, 모델, 사용자 등</li>
 *   <li><strong>카운트 정보</strong>: 총 요청 수, 성공/실패 수</li>
 *   <li><strong>성능 정보</strong>: 평균 응답 시간, 처리량</li>
 *   <li><strong>사용량 정보</strong>: 토큰 사용량, 비용</li>
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
    description = "통계 데이터 항목",
    example = """
        {
          "project_id": "project-123",
          "user": "user@example.com",
          "total_count": 500,
          "success_count": 485,
          "failed_count": 15,
          "success_rate": 97.0,
          "avg_response_time": 1200,
          "total_tokens": 75000
        }
        """
)
public class StatsDataItem {
    
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
     * 모델 식별자
     */
    @JsonProperty("model_id")
    @Schema(description = "모델 식별자", example = "gpt-4")
    private String modelId;
    
    /**
     * 모델 타입
     */
    @JsonProperty("model_type")
    @Schema(description = "모델 타입", example = "chat")
    private String modelType;
    
    /**
     * 사용자 정보
     */
    @JsonProperty("user")
    @Schema(description = "사용자 식별자", example = "user@example.com")
    private String user;
    
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
     * 총 요청/세션 수
     */
    @JsonProperty("total_count")
    @Schema(description = "총 요청/세션 수", example = "500")
    private Integer totalCount;
    
    /**
     * 성공 요청/세션 수
     */
    @JsonProperty("success_count")
    @Schema(description = "성공 요청/세션 수", example = "485")
    private Integer successCount;
    
    /**
     * 실패 요청/세션 수
     */
    @JsonProperty("failed_count")
    @Schema(description = "실패 요청/세션 수", example = "15")
    private Integer failedCount;
    
    /**
     * 성공률 (%)
     */
    @JsonProperty("success_rate")
    @Schema(description = "성공률 (%)", example = "97.0")
    private Double successRate;
    
    /**
     * 평균 응답 시간 (밀리초)
     */
    @JsonProperty("avg_response_time")
    @Schema(description = "평균 응답 시간 (밀리초)", example = "1200")
    private Integer avgResponseTime;
    
    /**
     * 최소 응답 시간 (밀리초)
     */
    @JsonProperty("min_response_time")
    @Schema(description = "최소 응답 시간 (밀리초)", example = "300")
    private Integer minResponseTime;
    
    /**
     * 최대 응답 시간 (밀리초)
     */
    @JsonProperty("max_response_time")
    @Schema(description = "최대 응답 시간 (밀리초)", example = "5000")
    private Integer maxResponseTime;
    
    /**
     * 총 토큰 사용량
     */
    @JsonProperty("total_tokens")
    @Schema(description = "총 토큰 사용량", example = "75000")
    private Long totalTokens;
    
    /**
     * 평균 토큰 사용량
     */
    @JsonProperty("avg_tokens")
    @Schema(description = "평균 토큰 사용량", example = "150")
    private Integer avgTokens;
    
    /**
     * 날짜 (일별 집계 시)
     */
    @JsonProperty("date")
    @Schema(description = "날짜 (YYYY-MM-DD 형식)", example = "2025-09-24")
    private String date;
    
    /**
     * 시간 (시간별 집계 시)
     */
    @JsonProperty("hour")
    @Schema(description = "시간 (0-23)", example = "14")
    private Integer hour;
    
    /**
     * 에이전트 관련 필드들
     */
    
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
     * 서빙 타입
     */
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입", example = "sync")
    private String servingType;
    
    /**
     * 평균 세션 지속 시간 (초)
     */
    @JsonProperty("avg_duration")
    @Schema(description = "평균 세션 지속 시간 (초)", example = "180")
    private Integer avgDuration;
    
    /**
     * 평균 품질 점수
     */
    @JsonProperty("avg_quality_score")
    @Schema(description = "평균 품질 점수 (0-100)", example = "85.5")
    private Double avgQualityScore;
    
    /**
     * 문서 지능형 분석 관련 필드들
     */
    
    /**
     * 도구 식별자
     */
    @JsonProperty("tool_id")
    @Schema(description = "도구 식별자", example = "ocr-tool")
    private String toolId;
    
    /**
     * 처리된 문서 수
     */
    @JsonProperty("processed_documents")
    @Schema(description = "처리된 문서 수", example = "250")
    private Integer processedDocuments;
    
    /**
     * 평균 처리 시간 (초)
     */
    @JsonProperty("avg_processing_time")
    @Schema(description = "평균 처리 시간 (초)", example = "45")
    private Integer avgProcessingTime;
    
    /**
     * 평균 정확도 (%)
     */
    @JsonProperty("avg_accuracy")
    @Schema(description = "평균 정확도 (%)", example = "94.5")
    private Double avgAccuracy;
}