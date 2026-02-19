package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * SKTAI 리소스 목록 응답 DTO
 * 
 * <p>SKTAI 플랫폼의 리소스 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 페이지네이션을 지원하며 다양한 필터링과 정렬 옵션을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>리소스 목록</strong>: 각 리소스의 기본 정보</li>
 *   <li><strong>페이지네이션</strong>: 현재 페이지, 총 개수, 다음 페이지 존재 여부</li>
 *   <li><strong>집계 정보</strong>: 리소스 타입별 통계</li>
 *   <li><strong>필터 정보</strong>: 적용된 필터 조건</li>
 * </ul>
 * 
 * <h3>활용 시나리오:</h3>
 * <ul>
 *   <li><strong>리소스 관리</strong>: 전체 리소스 현황 파악</li>
 *   <li><strong>용량 계획</strong>: 리소스 사용률 분석</li>
 *   <li><strong>비용 최적화</strong>: 미사용 리소스 식별</li>
 *   <li><strong>보안 점검</strong>: 리소스 접근 권한 검토</li>
 * </ul>
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
    description = "SKTAI 리소스 목록",
    example = """
        {
          "object": "list",
          "data": [
            {
              "resource_id": "resource-123",
              "resource_type": "gpu",
              "name": "ML-Training-Cluster-01",
              "status": "active",
              "usage_percent": 68.5,
              "cost_per_hour": 12.50
            },
            {
              "resource_id": "resource-456",
              "resource_type": "storage",
              "name": "Data-Storage-Pool-02",
              "status": "active",
              "usage_percent": 45.2,
              "cost_per_hour": 5.00
            }
          ],
          "total_count": 47,
          "page": 1,
          "page_size": 20,
          "has_more": true,
          "summary": {
            "total_resources": 47,
            "active_resources": 42,
            "total_cost_per_hour": 234.50
          }
        }
        """
)
public class ResourceListResponse {
    
    /**
     * 객체 타입
     */
    @JsonProperty("object")
    @Schema(
        description = "객체 타입",
        example = "list"
    )
    private String object;
    
    /**
     * 리소스 목록 데이터
     */
    @JsonProperty("data")
    @Schema(
        description = "리소스 목록 데이터"
    )
    private List<ResourceSummary> data;
    
    /**
     * 전체 리소스 수
     */
    @JsonProperty("total_count")
    @Schema(
        description = "필터 조건에 맞는 전체 리소스 수",
        example = "47"
    )
    private Integer totalCount;
    
    /**
     * 현재 페이지 번호
     */
    @JsonProperty("page")
    @Schema(
        description = "현재 페이지 번호 (1부터 시작)",
        example = "1"
    )
    private Integer page;
    
    /**
     * 페이지 크기
     */
    @JsonProperty("page_size")
    @Schema(
        description = "페이지당 항목 수",
        example = "20"
    )
    private Integer pageSize;
    
    /**
     * 다음 페이지 존재 여부
     */
    @JsonProperty("has_more")
    @Schema(
        description = "다음 페이지 데이터 존재 여부",
        example = "true"
    )
    private Boolean hasMore;
    
    /**
     * 첫 번째 리소스 ID
     */
    @JsonProperty("first_id")
    @Schema(
        description = "목록의 첫 번째 리소스 ID (커서 페이지네이션용)",
        example = "resource-123"
    )
    private String firstId;
    
    /**
     * 마지막 리소스 ID
     */
    @JsonProperty("last_id")
    @Schema(
        description = "목록의 마지막 리소스 ID (커서 페이지네이션용)",
        example = "resource-456"
    )
    private String lastId;
    
    /**
     * 리소스 요약 통계
     */
    @JsonProperty("summary")
    @Schema(
        description = "리소스 전체 요약 통계",
        example = """
            {
              "total_resources": 47,
              "active_resources": 42,
              "inactive_resources": 3,
              "pending_resources": 2,
              "total_cost_per_hour": 234.50,
              "total_cost_per_day": 5628.00,
              "avg_utilization": 67.3,
              "resource_types": {
                "gpu": 12,
                "cpu": 18,
                "storage": 15,
                "network": 2
              }
            }
            """
    )
    private Map<String, Object> summary;
    
    /**
     * 적용된 필터 정보
     */
    @JsonProperty("applied_filters")
    @Schema(
        description = "현재 적용된 필터 조건",
        example = """
            {
              "resource_type": "gpu",
              "status": "active",
              "project_id": "project-123",
              "region": "kr-central-1",
              "min_usage": 50.0
            }
            """
    )
    private Map<String, Object> appliedFilters;
    
    /**
     * 정렬 정보
     */
    @JsonProperty("sort_info")
    @Schema(
        description = "적용된 정렬 정보",
        example = """
            {
              "field": "usage_percent",
              "order": "desc"
            }
            """
    )
    private Map<String, String> sortInfo;
    
    /**
     * 리소스 요약 정보 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 리소스 요약 정보")
    public static class ResourceSummary {
        
        /**
         * 리소스 ID
         */
        @JsonProperty("resource_id")
        @Schema(description = "리소스 고유 ID", example = "resource-123")
        private String resourceId;
        
        /**
         * 리소스 타입
         */
        @JsonProperty("resource_type")
        @Schema(description = "리소스 타입", example = "gpu")
        private String resourceType;
        
        /**
         * 리소스 이름
         */
        @JsonProperty("name")
        @Schema(description = "리소스 이름", example = "ML-Training-Cluster-01")
        private String name;
        
        /**
         * 리소스 상태
         */
        @JsonProperty("status")
        @Schema(
            description = "리소스 상태", 
            example = "active",
            allowableValues = {"active", "inactive", "pending", "error", "maintenance"}
        )
        private String status;
        
        /**
         * 프로젝트 ID
         */
        @JsonProperty("project_id")
        @Schema(description = "소속 프로젝트 ID", example = "project-123")
        private String projectId;
        
        /**
         * 사용률
         */
        @JsonProperty("usage_percent")
        @Schema(description = "현재 사용률 (%)", example = "68.5")
        private Double usagePercent;
        
        /**
         * 시간당 비용
         */
        @JsonProperty("cost_per_hour")
        @Schema(description = "시간당 비용", example = "12.50")
        private Double costPerHour;
        
        /**
         * 지역 정보
         */
        @JsonProperty("region")
        @Schema(description = "리소스 위치 지역", example = "kr-central-1")
        private String region;
        
        /**
         * 생성 시간
         */
        @JsonProperty("created_at")
        @Schema(description = "리소스 생성 시간", example = "2025-08-15T10:30:45Z")
        private String createdAt;
        
        /**
         * 마지막 사용 시간
         */
        @JsonProperty("last_used_at")
        @Schema(description = "마지막 사용 시간", example = "2025-08-15T12:25:30Z")
        private String lastUsedAt;
        
        /**
         * 태그 정보
         */
        @JsonProperty("tags")
        @Schema(
            description = "리소스 태그",
            example = """
                {
                  "environment": "production",
                  "team": "ml-ops"
                }
                """
        )
        private Map<String, String> tags;
        
        /**
         * 간단한 사양 정보
         */
        @JsonProperty("specifications")
        @Schema(
            description = "리소스 주요 사양",
            example = """
                {
                  "type": "A100",
                  "count": 2,
                  "memory": "40GB"
                }
                """
        )
        private Map<String, Object> specifications;
    }
}
