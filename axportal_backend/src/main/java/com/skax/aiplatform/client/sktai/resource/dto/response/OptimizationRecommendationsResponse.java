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
 * 최적화 권장사항 응답
 * 
 * <p>리소스 사용 최적화를 위한 분석 결과와 권장사항을 포함합니다.
 * 비용 절약, 성능 향상, 효율성 개선 등의 제안을 제공합니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "최적화 권장사항 응답")
public class OptimizationRecommendationsResponse {

    @JsonProperty("recommendations")
    @Schema(description = "최적화 권장사항 목록")
    private List<OptimizationRecommendation> recommendations;

    @JsonProperty("analysis_summary")
    @Schema(description = "분석 요약")
    private AnalysisSummary analysisSummary;

    @JsonProperty("potential_savings")
    @Schema(description = "예상 절약 효과")
    private PotentialSavings potentialSavings;

    @JsonProperty("generated_at")
    @Schema(description = "분석 수행 시간")
    private LocalDateTime generatedAt;

    @JsonProperty("analysis_period")
    @Schema(description = "분석 기간")
    private AnalysisPeriod analysisPeriod;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "최적화 권장사항")
    public static class OptimizationRecommendation {

        @JsonProperty("recommendation_id")
        @Schema(description = "권장사항 ID")
        private String recommendationId;

        @JsonProperty("category")
        @Schema(description = "카테고리", allowableValues = {"COST_OPTIMIZATION", "PERFORMANCE", "SECURITY", "RESOURCE_UTILIZATION"})
        private String category;

        @JsonProperty("priority")
        @Schema(description = "우선순위", allowableValues = {"HIGH", "MEDIUM", "LOW"})
        private String priority;

        @JsonProperty("title")
        @Schema(description = "권장사항 제목")
        private String title;

        @JsonProperty("description")
        @Schema(description = "상세 설명")
        private String description;

        @JsonProperty("current_state")
        @Schema(description = "현재 상태")
        private String currentState;

        @JsonProperty("recommended_action")
        @Schema(description = "권장 조치")
        private String recommendedAction;

        @JsonProperty("expected_impact")
        @Schema(description = "예상 효과")
        private ExpectedImpact expectedImpact;

        @JsonProperty("implementation_effort")
        @Schema(description = "구현 난이도", allowableValues = {"EASY", "MEDIUM", "HARD"})
        private String implementationEffort;

        @JsonProperty("affected_resources")
        @Schema(description = "영향 받는 리소스 목록")
        private List<String> affectedResources;

        @JsonProperty("implementation_steps")
        @Schema(description = "구현 단계")
        private List<String> implementationSteps;

        @JsonProperty("risks")
        @Schema(description = "위험 요소")
        private List<String> risks;

        @JsonProperty("tags")
        @Schema(description = "태그")
        private List<String> tags;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "예상 효과")
    public static class ExpectedImpact {

        @JsonProperty("cost_savings_percentage")
        @Schema(description = "비용 절약률 (%)")
        private Double costSavingsPercentage;

        @JsonProperty("cost_savings_amount")
        @Schema(description = "예상 절약 금액")
        private Double costSavingsAmount;

        @JsonProperty("performance_improvement")
        @Schema(description = "성능 향상 정도")
        private String performanceImprovement;

        @JsonProperty("resource_efficiency_gain")
        @Schema(description = "리소스 효율성 향상")
        private String resourceEfficiencyGain;

        @JsonProperty("timeline")
        @Schema(description = "효과 발현 기간")
        private String timeline;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "분석 요약")
    public static class AnalysisSummary {

        @JsonProperty("total_recommendations")
        @Schema(description = "전체 권장사항 수")
        private Integer totalRecommendations;

        @JsonProperty("high_priority_count")
        @Schema(description = "높은 우선순위 권장사항 수")
        private Integer highPriorityCount;

        @JsonProperty("medium_priority_count")
        @Schema(description = "중간 우선순위 권장사항 수")
        private Integer mediumPriorityCount;

        @JsonProperty("low_priority_count")
        @Schema(description = "낮은 우선순위 권장사항 수")
        private Integer lowPriorityCount;

        @JsonProperty("analyzed_resources_count")
        @Schema(description = "분석된 리소스 수")
        private Integer analyzedResourcesCount;

        @JsonProperty("optimization_categories")
        @Schema(description = "최적화 카테고리별 개수")
        private Map<String, Integer> optimizationCategories;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "예상 절약 효과")
    public static class PotentialSavings {

        @JsonProperty("total_monthly_savings")
        @Schema(description = "월간 총 절약 예상 금액")
        private Double totalMonthlySavings;

        @JsonProperty("total_annual_savings")
        @Schema(description = "연간 총 절약 예상 금액")
        private Double totalAnnualSavings;

        @JsonProperty("currency")
        @Schema(description = "통화")
        private String currency;

        @JsonProperty("savings_breakdown")
        @Schema(description = "카테고리별 절약 효과")
        private Map<String, Double> savingsBreakdown;

        @JsonProperty("confidence_level")
        @Schema(description = "신뢰도", allowableValues = {"HIGH", "MEDIUM", "LOW"})
        private String confidenceLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "분석 기간")
    public static class AnalysisPeriod {

        @JsonProperty("start_date")
        @Schema(description = "분석 시작일")
        private LocalDateTime startDate;

        @JsonProperty("end_date")
        @Schema(description = "분석 종료일")
        private LocalDateTime endDate;

        @JsonProperty("duration_days")
        @Schema(description = "분석 기간 (일)")
        private Integer durationDays;

        @JsonProperty("data_points_count")
        @Schema(description = "분석된 데이터 포인트 수")
        private Long dataPointsCount;
    }
}
