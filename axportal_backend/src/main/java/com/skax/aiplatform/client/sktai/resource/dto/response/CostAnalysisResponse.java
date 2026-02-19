package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 비용 분석 응답
 * 
 * <p>리소스 사용에 따른 비용 분석 결과를 포함합니다.
 * 상세한 비용 내역, 트렌드, 예측 등을 제공합니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "비용 분석 응답")
public class CostAnalysisResponse {

    @JsonProperty("cost_summary")
    @Schema(description = "비용 요약")
    private CostSummary costSummary;

    @JsonProperty("cost_breakdown")
    @Schema(description = "비용 상세 내역")
    private CostBreakdown costBreakdown;

    @JsonProperty("cost_trends")
    @Schema(description = "비용 트렌드")
    private CostTrends costTrends;

    @JsonProperty("cost_forecasts")
    @Schema(description = "비용 예측")
    private CostForecasts costForecasts;

    @JsonProperty("billing_alerts")
    @Schema(description = "청구 알림")
    private List<BillingAlert> billingAlerts;

    @JsonProperty("analysis_metadata")
    @Schema(description = "분석 메타데이터")
    private AnalysisMetadata analysisMetadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "비용 요약")
    public static class CostSummary {

        @JsonProperty("total_cost")
        @Schema(description = "총 비용")
        private Double totalCost;

        @JsonProperty("currency")
        @Schema(description = "통화")
        private String currency;

        @JsonProperty("period_start")
        @Schema(description = "분석 기간 시작")
        private LocalDateTime periodStart;

        @JsonProperty("period_end")
        @Schema(description = "분석 기간 종료")
        private LocalDateTime periodEnd;

        @JsonProperty("previous_period_cost")
        @Schema(description = "이전 기간 비용")
        private Double previousPeriodCost;

        @JsonProperty("cost_change_percentage")
        @Schema(description = "비용 변화율 (%)")
        private Double costChangePercentage;

        @JsonProperty("average_daily_cost")
        @Schema(description = "일일 평균 비용")
        private Double averageDailyCost;

        @JsonProperty("peak_daily_cost")
        @Schema(description = "최대 일일 비용")
        private Double peakDailyCost;

        @JsonProperty("lowest_daily_cost")
        @Schema(description = "최소 일일 비용")
        private Double lowestDailyCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "비용 상세 내역")
    public static class CostBreakdown {

        @JsonProperty("by_service")
        @Schema(description = "서비스별 비용")
        private Map<String, ServiceCost> byService;

        @JsonProperty("by_resource_type")
        @Schema(description = "리소스 타입별 비용")
        private Map<String, Double> byResourceType;

        @JsonProperty("by_region")
        @Schema(description = "지역별 비용")
        private Map<String, Double> byRegion;

        @JsonProperty("by_project")
        @Schema(description = "프로젝트별 비용")
        private Map<String, Double> byProject;

        @JsonProperty("by_usage_type")
        @Schema(description = "사용 유형별 비용")
        private Map<String, Double> byUsageType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "서비스 비용")
    public static class ServiceCost {

        @JsonProperty("total_cost")
        @Schema(description = "서비스 총 비용")
        private Double totalCost;

        @JsonProperty("usage_hours")
        @Schema(description = "사용 시간")
        private Double usageHours;

        @JsonProperty("unit_cost")
        @Schema(description = "단위 비용")
        private Double unitCost;

        @JsonProperty("cost_percentage")
        @Schema(description = "전체 비용 대비 비율 (%)")
        private Double costPercentage;

        @JsonProperty("resource_details")
        @Schema(description = "리소스 상세")
        private List<ResourceCost> resourceDetails;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "리소스 비용")
    public static class ResourceCost {

        @JsonProperty("resource_id")
        @Schema(description = "리소스 ID")
        private String resourceId;

        @JsonProperty("resource_name")
        @Schema(description = "리소스 이름")
        private String resourceName;

        @JsonProperty("cost")
        @Schema(description = "비용")
        private Double cost;

        @JsonProperty("usage_metrics")
        @Schema(description = "사용 지표")
        private Map<String, Object> usageMetrics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "비용 트렌드")
    public static class CostTrends {

        @JsonProperty("daily_costs")
        @Schema(description = "일일 비용 추이")
        private List<DailyCost> dailyCosts;

        @JsonProperty("weekly_costs")
        @Schema(description = "주간 비용 추이")
        private List<WeeklyCost> weeklyCosts;

        @JsonProperty("monthly_costs")
        @Schema(description = "월간 비용 추이")
        private List<MonthlyCost> monthlyCosts;

        @JsonProperty("trend_analysis")
        @Schema(description = "트렌드 분석")
        private TrendAnalysis trendAnalysis;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "일일 비용")
    public static class DailyCost {

        @JsonProperty("date")
        @Schema(description = "날짜")
        private String date;

        @JsonProperty("cost")
        @Schema(description = "비용")
        private Double cost;

        @JsonProperty("usage_hours")
        @Schema(description = "사용 시간")
        private Double usageHours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "주간 비용")
    public static class WeeklyCost {

        @JsonProperty("week_start")
        @Schema(description = "주 시작일")
        private String weekStart;

        @JsonProperty("week_end")
        @Schema(description = "주 종료일")
        private String weekEnd;

        @JsonProperty("cost")
        @Schema(description = "비용")
        private Double cost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "월간 비용")
    public static class MonthlyCost {

        @JsonProperty("month")
        @Schema(description = "월 (YYYY-MM)")
        private String month;

        @JsonProperty("cost")
        @Schema(description = "비용")
        private Double cost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "트렌드 분석")
    public static class TrendAnalysis {

        @JsonProperty("trend_direction")
        @Schema(description = "트렌드 방향", allowableValues = {"INCREASING", "DECREASING", "STABLE"})
        private String trendDirection;

        @JsonProperty("growth_rate")
        @Schema(description = "증가율 (%)")
        private Double growthRate;

        @JsonProperty("seasonal_patterns")
        @Schema(description = "계절성 패턴")
        private List<String> seasonalPatterns;

        @JsonProperty("anomalies")
        @Schema(description = "이상치")
        private List<CostAnomaly> anomalies;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "비용 예측")
    public static class CostForecasts {

        @JsonProperty("next_month_forecast")
        @Schema(description = "다음 달 예측 비용")
        private Double nextMonthForecast;

        @JsonProperty("next_quarter_forecast")
        @Schema(description = "다음 분기 예측 비용")
        private Double nextQuarterForecast;

        @JsonProperty("annual_forecast")
        @Schema(description = "연간 예측 비용")
        private Double annualForecast;

        @JsonProperty("confidence_interval")
        @Schema(description = "신뢰구간")
        private ConfidenceInterval confidenceInterval;

        @JsonProperty("forecast_assumptions")
        @Schema(description = "예측 가정사항")
        private List<String> forecastAssumptions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "신뢰구간")
    public static class ConfidenceInterval {

        @JsonProperty("lower_bound")
        @Schema(description = "하한값")
        private Double lowerBound;

        @JsonProperty("upper_bound")
        @Schema(description = "상한값")
        private Double upperBound;

        @JsonProperty("confidence_level")
        @Schema(description = "신뢰수준 (%)")
        private Double confidenceLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "비용 이상치")
    public static class CostAnomaly {

        @JsonProperty("date")
        @Schema(description = "발생 날짜")
        private String date;

        @JsonProperty("expected_cost")
        @Schema(description = "예상 비용")
        private Double expectedCost;

        @JsonProperty("actual_cost")
        @Schema(description = "실제 비용")
        private Double actualCost;

        @JsonProperty("deviation_percentage")
        @Schema(description = "편차율 (%)")
        private Double deviationPercentage;

        @JsonProperty("possible_causes")
        @Schema(description = "가능한 원인")
        private List<String> possibleCauses;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "청구 알림")
    public static class BillingAlert {

        @JsonProperty("alert_type")
        @Schema(description = "알림 타입", allowableValues = {"BUDGET_EXCEEDED", "UNUSUAL_SPENDING", "COST_SPIKE", "FORECAST_WARNING"})
        private String alertType;

        @JsonProperty("severity")
        @Schema(description = "심각도", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
        private String severity;

        @JsonProperty("message")
        @Schema(description = "알림 메시지")
        private String message;

        @JsonProperty("threshold_value")
        @Schema(description = "임계값")
        private Double thresholdValue;

        @JsonProperty("current_value")
        @Schema(description = "현재값")
        private Double currentValue;

        @JsonProperty("triggered_at")
        @Schema(description = "알림 발생 시간")
        private LocalDateTime triggeredAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "분석 메타데이터")
    public static class AnalysisMetadata {

        @JsonProperty("analysis_id")
        @Schema(description = "분석 ID")
        private String analysisId;

        @JsonProperty("generated_at")
        @Schema(description = "생성 시간")
        private LocalDateTime generatedAt;

        @JsonProperty("data_freshness")
        @Schema(description = "데이터 신선도 (분석 시점 기준)")
        private String dataFreshness;

        @JsonProperty("analysis_scope")
        @Schema(description = "분석 범위")
        private Map<String, Object> analysisScope;

        @JsonProperty("data_sources")
        @Schema(description = "데이터 소스")
        private List<String> dataSources;
    }
}
