package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI API 표준 페이징 정보 DTO
 * 
 * <p>SKTAI API에서 사용되는 표준 페이지네이션 응답 구조입니다.
 * Laravel 스타일의 페이징 정보를 제공하며, 페이지 번호, 범위, 링크 등의 
 * 정보를 포함합니다.</p>
 * 
 * <h3>주요 특징:</h3>
 * <ul>
 *   <li><strong>Laravel 호환</strong>: Laravel의 기본 페이징 구조와 호환</li>
 *   <li><strong>풍부한 메타데이터</strong>: 현재 페이지, 총 페이지, 아이템 범위 등 제공</li>
 *   <li><strong>네비게이션 링크</strong>: 페이지 이동을 위한 URL 링크 제공</li>
 *   <li><strong>유연한 설정</strong>: 페이지당 아이템 수 조정 가능</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * {
 *   "page": 2,
 *   "from_": 21,
 *   "to": 40,
 *   "total": 100,
 *   "last_page": 5,
 *   "items_per_page": 20
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see PaginationLink 페이징 링크 정보
 * @see Payload 페이징 정보를 포함하는 페이로드
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API 표준 페이징 정보",
    example = """
        {
          "page": 2,
          "first_page_url": "/api/v1/users?page=1",
          "from_": 21,
          "last_page": 5,
          "links": [],
          "next_page_url": "/api/v1/users?page=3",
          "items_per_page": 20,
          "prev_page_url": "/api/v1/users?page=1",
          "to": 40,
          "total": 100
        }
        """
)
public class Pagination {
    
    /**
     * 현재 페이지 번호
     * 
     * <p>사용자가 현재 보고 있는 페이지의 번호입니다.
     * 1부터 시작하며, 최대값은 last_page와 같습니다.</p>
     */
    @JsonProperty("page")
    @Schema(
        description = "현재 페이지 번호 (1부터 시작)", 
        example = "2",
        minimum = "1"
    )
    private Integer page;
    
    /**
     * 첫 번째 페이지 URL
     * 
     * <p>첫 번째 페이지로 이동하기 위한 URL입니다.
     * 현재 페이지가 첫 번째 페이지인 경우에도 제공됩니다.</p>
     */
    @JsonProperty("first_page_url")
    @Schema(
        description = "첫 번째 페이지로 이동하는 URL", 
        example = "/api/v1/users?page=1",
        format = "uri"
    )
    private String firstPageUrl;
    
    /**
     * 현재 페이지 시작 아이템 번호
     * 
     * <p>현재 페이지에서 첫 번째로 표시되는 아이템의 전체 순번입니다.
     * 1부터 시작하며, (page - 1) * items_per_page + 1로 계산됩니다.</p>
     * 
     * @implNote 필드명이 'from_'인 이유는 'from'이 SQL 예약어이기 때문입니다.
     */
    @JsonProperty("from_")
    @Schema(
        description = "현재 페이지 시작 아이템 번호 (1부터 시작)", 
        example = "21",
        minimum = "1"
    )
    private Integer from;
    
    /**
     * 전체 페이지 수
     * 
     * <p>전체 데이터를 기준으로 계산된 총 페이지 수입니다.
     * Math.ceil(total / items_per_page)로 계산됩니다.</p>
     */
    @JsonProperty("last_page")
    @Schema(
        description = "전체 페이지 수", 
        example = "5",
        minimum = "1"
    )
    private Integer lastPage;
    
    /**
     * 페이지네이션 링크 목록
     * 
     * <p>페이징 네비게이션을 위한 링크 정보 배열입니다.
     * 이전/다음 페이지, 페이지 번호 링크 등이 포함됩니다.</p>
     */
    @JsonProperty("links")
    @Schema(
        description = "페이징 네비게이션 링크 목록"
    )
    private List<PaginationLink> links;

    /**
     * 다음 페이지 URL
     * 
     * <p>다음 페이지로 이동하기 위한 URL입니다.
     * 마지막 페이지인 경우 null이 될 수 있습니다.</p>
     */
    @JsonProperty("next_page_url")
    @Schema(
        description = "다음 페이지로 이동하는 URL (마지막 페이지인 경우 null)", 
        example = "/api/v1/users?page=3",
        format = "uri",
        nullable = true
    )
    private String nextPageUrl;
    
    /**
     * 페이지당 아이템 수
     * 
     * <p>한 페이지에 표시되는 아이템의 개수입니다.
     * API 요청 시 지정하거나 기본값이 사용됩니다.</p>
     */
    @JsonProperty("items_per_page")
    @Schema(
        description = "페이지당 아이템 수", 
        example = "20",
        minimum = "1",
        maximum = "100"
    )
    private Integer itemsPerPage;
    
    /**
     * 이전 페이지 URL
     * 
     * <p>이전 페이지로 이동하기 위한 URL입니다.
     * 첫 번째 페이지인 경우 null이 될 수 있습니다.</p>
     */
    @JsonProperty("prev_page_url")
    @Schema(
        description = "이전 페이지로 이동하는 URL (첫 번째 페이지인 경우 null)", 
        example = "/api/v1/users?page=1",
        format = "uri",
        nullable = true
    )
    private String prevPageUrl;
    
    /**
     * 현재 페이지 마지막 아이템 번호
     * 
     * <p>현재 페이지에서 마지막으로 표시되는 아이템의 전체 순번입니다.
     * Math.min(from + items_per_page - 1, total)로 계산됩니다.</p>
     */
    @JsonProperty("to")
    @Schema(
        description = "현재 페이지 마지막 아이템 번호", 
        example = "40",
        minimum = "1"
    )
    private Integer to;
    
    /**
     * 전체 아이템 수
     * 
     * <p>필터링이 적용된 후의 전체 데이터 개수입니다.
     * 페이지 수 계산의 기준이 됩니다.</p>
     */
    @JsonProperty("total")
    @Schema(
        description = "전체 아이템 수", 
        example = "100",
        minimum = "0"
    )
    private Integer total;
}
