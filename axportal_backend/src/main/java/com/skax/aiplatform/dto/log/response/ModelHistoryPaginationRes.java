package com.skax.aiplatform.dto.log.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 모델 사용 이력 페이징 정보 응답 DTO (카멜케이스)
 * 
 * <p>
 * 모델 사용 이력 조회 응답에서 사용되는 페이징 정보입니다.
 * Laravel 스타일의 페이징 구조를 카멜케이스로 변환한 구조입니다.
 * </p>
 * 
 * <h3>구조 특징:</h3>
 * <ul>
 * <li><strong>카멜케이스</strong>: Java 표준 네이밍 컨벤션 사용</li>
 * <li><strong>Laravel 스타일</strong>: Laravel의 pagination 구조를 따름</li>
 * <li><strong>링크 정보</strong>: 페이지 네비게이션을 위한 링크 배열 포함</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 * <li>모델 사용 이력 조회 시 페이징 정보</li>
 * <li>프론트엔드 페이지네이션 컴포넌트 연동</li>
 * <li>API 응답 구조 표준화</li>
 * </ul>
 *
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 페이징 정보", example = """
        {
          "page": 1,
          "firstPageUrl": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=1&size=1",
          "from": 1,
          "lastPage": 152,
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
          "nextPageUrl": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=2&size=1",
          "itemsPerPage": 1,
          "prevPageUrl": null,
          "to": 1,
          "total": 152
        }
        """)
public class ModelHistoryPaginationRes {

    /**
     * 현재 페이지 번호 (1부터 시작)
     */
    @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer page;

    /**
     * 첫 번째 페이지 URL
     */
    @Schema(description = "첫 번째 페이지 URL", example = "/history/model/list?page=1&size=1")
    private String firstPageUrl;

    /**
     * 현재 페이지의 첫 번째 항목 번호 (1부터 시작)
     */
    @Schema(description = "현재 페이지의 첫 번째 항목 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer from;

    /**
     * 마지막 페이지 번호
     */
    @Schema(description = "마지막 페이지 번호", example = "152", minimum = "1")
    private Integer lastPage;

    /**
     * 페이지 네비게이션 링크 목록
     */
    @Schema(description = "페이지 네비게이션 링크 목록")
    private List<PaginationLinkRes> links;

    /**
     * 다음 페이지 URL
     */
    @Schema(description = "다음 페이지 URL", example = "/history/model/list?page=2&size=1")
    private String nextPageUrl;

    /**
     * 페이지당 항목 수
     */
    @Schema(description = "페이지당 항목 수", example = "1", minimum = "1", maximum = "1000")
    private Integer itemsPerPage;

    /**
     * 이전 페이지 URL
     */
    @Schema(description = "이전 페이지 URL", example = "/history/model/list?page=1&size=1")
    private String prevPageUrl;

    /**
     * 현재 페이지의 마지막 항목 번호 (1부터 시작)
     */
    @Schema(description = "현재 페이지의 마지막 항목 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer to;

    /**
     * 전체 항목 수
     */
    @Schema(description = "전체 항목 수", example = "152", minimum = "0")
    private Integer total;

    /**
     * 페이지 네비게이션 링크 응답 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "페이지 네비게이션 링크")
    public static class PaginationLinkRes {

        /**
         * 링크 URL
         */
        @Schema(description = "링크 URL", example = "/history/model/list?page=2&size=1")
        private String url;

        /**
         * 링크 라벨
         */
        @Schema(description = "링크 라벨", example = "2")
        private String label;

        /**
         * 현재 활성 페이지 여부
         */
        @Schema(description = "현재 활성 페이지 여부", example = "false")
        private Boolean active;

        /**
         * 페이지 번호
         */
        @Schema(description = "페이지 번호", example = "2")
        private Integer page;
    }
}
