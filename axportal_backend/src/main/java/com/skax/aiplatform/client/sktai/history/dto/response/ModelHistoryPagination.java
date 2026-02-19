package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 모델 사용 이력 페이징 정보 DTO
 * 
 * <p>
 * SKTAI History API의 모델 사용 이력 조회 응답에서 사용되는 페이징 정보입니다.
 * Laravel 스타일의 페이징 구조를 따릅니다.
 * </p>
 * 
 * <h3>구조 특징:</h3>
 * <ul>
 * <li><strong>Laravel 스타일</strong>: Laravel의 pagination 구조를 따름</li>
 * <li><strong>링크 정보</strong>: 페이지 네비게이션을 위한 링크 배열 포함</li>
 * <li><strong>URL 정보</strong>: 각 페이지의 URL 정보 제공</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 * <li>모델 사용 이력 조회 시 페이징 정보</li>
 * <li>프론트엔드 페이지네이션 컴포넌트 연동</li>
 * <li>API 응답 구조 표준화</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-30
 * @version 1.0
 */
@Deprecated // 공통 Pagination(com.skax.aiplatform.client.sktai.common.dto.Pagination)로 교체 예정
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 페이징 정보 (deprecated: 공통 Pagination 사용 권장)", example = """
        {
          "page": 1,
          "first_page_url": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=1&size=1",
          "from_": 1,
          "last_page": 152,
          "links": [
            {
              "url": null,
              "label": "&laquo; Previous",
              "active": false,
              "page": null
            },
            {
              "url": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=1&size=1",
              "label": "1",
              "active": true,
              "page": 1
            }
          ],
          "next_page_url": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=2&size=1",
          "items_per_page": 1,
          "prev_page_url": null,
          "to": 1,
          "total": 152
        }
        """)
public class ModelHistoryPagination {

    /**
     * 현재 페이지 번호 (1부터 시작)
     */
    @JsonProperty("page")
    @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer page;

    /**
     * 첫 번째 페이지 URL
     */
    @JsonProperty("first_page_url")
    @Schema(description = "첫 번째 페이지 URL", example = "/history/model/list?page=1&size=1")
    private String firstPageUrl;

    /**
     * 현재 페이지의 첫 번째 항목 번호 (1부터 시작)
     */
    @JsonProperty("from_")
    @Schema(description = "현재 페이지의 첫 번째 항목 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer from;

    /**
     * 마지막 페이지 번호
     */
    @JsonProperty("last_page")
    @Schema(description = "마지막 페이지 번호", example = "152", minimum = "1")
    private Integer lastPage;

    /**
     * 페이지 네비게이션 링크 목록
     */
    @JsonProperty("links")
    @Schema(description = "페이지 네비게이션 링크 목록")
    private List<PaginationLink> links;

    /**
     * 다음 페이지 URL
     */
    @JsonProperty("next_page_url")
    @Schema(description = "다음 페이지 URL", example = "/history/model/list?page=2&size=1")
    private String nextPageUrl;

    /**
     * 페이지당 항목 수
     */
    @JsonProperty("items_per_page")
    @Schema(description = "페이지당 항목 수", example = "1", minimum = "1", maximum = "1000")
    private Integer itemsPerPage;

    /**
     * 이전 페이지 URL
     */
    @JsonProperty("prev_page_url")
    @Schema(description = "이전 페이지 URL", example = "/history/model/list?page=1&size=1")
    private String prevPageUrl;

    /**
     * 현재 페이지의 마지막 항목 번호 (1부터 시작)
     */
    @JsonProperty("to")
    @Schema(description = "현재 페이지의 마지막 항목 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer to;

    /**
     * 전체 항목 수
     */
    @JsonProperty("total")
    @Schema(description = "전체 항목 수", example = "152", minimum = "0")
    private Integer total;

    /**
     * 페이지 네비게이션 링크 DTO
     */
    @Deprecated // 공통 PaginationLink 사용 권장
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "페이지 네비게이션 링크 (deprecated)")
    public static class PaginationLink {

        /**
         * 링크 URL
         */
        @JsonProperty("url")
        @Schema(description = "링크 URL", example = "/history/model/list?page=2&size=1")
        private String url;

        /**
         * 링크 라벨
         */
        @JsonProperty("label")
        @Schema(description = "링크 라벨", example = "2")
        private String label;

        /**
         * 현재 활성 페이지 여부
         */
        @JsonProperty("active")
        @Schema(description = "현재 활성 페이지 여부", example = "false")
        private Boolean active;

        /**
         * 페이지 번호
         */
        @JsonProperty("page")
        @Schema(description = "페이지 번호", example = "2")
        private Integer page;
    }
}