package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 검색 요청 DTO
 * 
 * <p>다양한 조건으로 아티팩트를 검색하기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchArtifactsRequest {
    
    @JsonProperty("pagination")
    @Schema(description = "페이지네이션 정보")
    private LablupPagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LablupPagination{
        @JsonProperty("forward")
        @Schema(description = "전진 페이지네이션 정보")
        private ForwardPagination forward;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForwardPagination{
        @JsonProperty("limit")
        @Schema(description = "페이지 크기", example = "10")
        private Integer limit;

        @JsonProperty("after")
        @Schema(description = "다음 페이지 토큰", example = "1234567890")
        private String after;
    }
    // /**
    //  * 검색 키워드
    //  */
    // private String keyword;
    
    // /**
    //  * 검색 필터
    //  */
    // private SearchFilters filters;
    
    // /**
    //  * 정렬 옵션
    //  */
    // private SortOptions sort;
    
    // /**
    //  * 페이징 정보
    //  */
    // private PaginationInfo pagination;
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class SearchFilters {
    //     /**
    //      * 아티팩트 타입 필터
    //      */
    //     private List<String> types;
        
    //     /**
    //      * 생성 날짜 범위 (ISO 8601 형식)
    //      */
    //     private DateRange createdDateRange;
        
    //     /**
    //      * 수정 날짜 범위 (ISO 8601 형식)
    //      */
    //     private DateRange modifiedDateRange;
        
    //     /**
    //      * 태그 필터
    //      */
    //     private List<String> tags;
        
    //     /**
    //      * 메타데이터 필터
    //      */
    //     private Map<String, String> metadata;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class DateRange {
    //     /**
    //      * 시작 날짜
    //      */
    //     private String startDate;
        
    //     /**
    //      * 종료 날짜
    //      */
    //     private String endDate;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class SortOptions {
    //     /**
    //      * 정렬 필드
    //      */
    //     private String field;
        
    //     /**
    //      * 정렬 방향 (asc, desc)
    //      */
    //     private String direction;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class PaginationInfo {
    //     /**
    //      * 페이지 번호 (0부터 시작)
    //      */
    //     private int page;
        
    //     /**
    //      * 페이지 크기
    //      */
    //     private int size;
    // }
}