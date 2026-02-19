package com.skax.aiplatform.client.sktai.serving.dto.response;

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
 * 에이전트 서빙 목록 조회 응답
 * 
 * <p>지능형 에이전트들의 서빙 상태와 정보를 포함합니다.
 * 각 에이전트의 기능, 성능, 가용성 등을 제공합니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "에이전트 서빙 목록 조회 응답")
public class AgentServingsResponse {

    @JsonProperty("agent_servings")
    @Schema(description = "에이전트 서빙 목록")
    private List<AgentServingInfo> agentServings;

    @JsonProperty("total_count")
    @Schema(description = "전체 에이전트 서빙 수")
    private Integer totalCount;

    @JsonProperty("categories")
    @Schema(description = "에이전트 카테고리 목록")
    private List<String> categories;

    @JsonProperty("page_info")
    @Schema(description = "페이지네이션 정보")
    private PageInfo pageInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "에이전트 서빙 정보")
    public static class AgentServingInfo {

        @JsonProperty("agent_id")
        @Schema(description = "에이전트 ID")
        private String agentId;

        @JsonProperty("agent_name")
        @Schema(description = "에이전트 이름")
        private String agentName;

        @JsonProperty("description")
        @Schema(description = "에이전트 설명")
        private String description;

        @JsonProperty("category")
        @Schema(description = "에이전트 카테고리")
        private String category;

        @JsonProperty("version")
        @Schema(description = "에이전트 버전")
        private String version;

        @JsonProperty("status")
        @Schema(description = "서빙 상태", allowableValues = {"ACTIVE", "INACTIVE", "MAINTENANCE", "ERROR"})
        private String status;

        @JsonProperty("capabilities")
        @Schema(description = "에이전트 기능 목록")
        private List<String> capabilities;

        @JsonProperty("endpoint_info")
        @Schema(description = "엔드포인트 정보")
        private EndpointInfo endpointInfo;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private LocalDateTime createdAt;

        @JsonProperty("last_updated")
        @Schema(description = "최종 업데이트 시간")
        private LocalDateTime lastUpdated;

        @JsonProperty("performance_stats")
        @Schema(description = "성능 통계")
        private PerformanceStats performanceStats;

        @JsonProperty("configuration")
        @Schema(description = "에이전트 설정")
        private Map<String, Object> configuration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "엔드포인트 정보")
    public static class EndpointInfo {

        @JsonProperty("base_url")
        @Schema(description = "기본 URL")
        private String baseUrl;

        @JsonProperty("api_version")
        @Schema(description = "API 버전")
        private String apiVersion;

        @JsonProperty("health_check_url")
        @Schema(description = "헬스체크 URL")
        private String healthCheckUrl;

        @JsonProperty("documentation_url")
        @Schema(description = "문서 URL")
        private String documentationUrl;

        @JsonProperty("rate_limit")
        @Schema(description = "요청 제한 (분당)")
        private Integer rateLimit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "성능 통계")
    public static class PerformanceStats {

        @JsonProperty("total_requests")
        @Schema(description = "총 요청 수")
        private Long totalRequests;

        @JsonProperty("success_rate")
        @Schema(description = "성공률 (%)")
        private Double successRate;

        @JsonProperty("avg_response_time")
        @Schema(description = "평균 응답 시간 (밀리초)")
        private Double avgResponseTime;

        @JsonProperty("peak_requests_per_minute")
        @Schema(description = "최대 분당 요청 수")
        private Integer peakRequestsPerMinute;

        @JsonProperty("uptime_percentage")
        @Schema(description = "가동률 (%)")
        private Double uptimePercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "페이지네이션 정보")
    public static class PageInfo {

        @JsonProperty("current_page")
        @Schema(description = "현재 페이지")
        private Integer currentPage;

        @JsonProperty("page_size")
        @Schema(description = "페이지 크기")
        private Integer pageSize;

        @JsonProperty("total_pages")
        @Schema(description = "전체 페이지 수")
        private Integer totalPages;

        @JsonProperty("has_next")
        @Schema(description = "다음 페이지 존재 여부")
        private Boolean hasNext;

        @JsonProperty("has_previous")
        @Schema(description = "이전 페이지 존재 여부")
        private Boolean hasPrevious;
    }
}
