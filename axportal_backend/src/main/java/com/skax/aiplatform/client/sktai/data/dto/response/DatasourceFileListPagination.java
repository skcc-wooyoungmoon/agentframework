package com.skax.aiplatform.client.sktai.data.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 데이터소스 파일 목록 페이징 정보 DTO
 * 
 * <p>데이터소스 파일 목록 조회 결과의 페이징 정보를 포함하는 DTO입니다.
 * 페이지 네비게이션, 링크 정보, 통계 정보를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>페이지 정보</strong>: 현재 페이지, 전체 페이지 수</li>
 *   <li><strong>항목 정보</strong>: 페이지당 항목 수, 전체 항목 수</li>
 *   <li><strong>범위 정보</strong>: 시작/끝 인덱스</li>
 *   <li><strong>링크 정보</strong>: 이전/다음 페이지 URL, 네비게이션 링크</li>
 * </ul>
 *
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Deprecated // 공통 Pagination(com.skax.aiplatform.client.sktai.common.dto.Pagination) 사용 권장
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 데이터소스 파일 목록 페이징 정보 (deprecated: 공통 Pagination 사용 권장)",
    example = """
        {
          "page": 1,
          "first_page_url": "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10",
          "from_": 1,
          "last_page": 1,
          "links": [
            {
              "url": null,
              "label": "&laquo; Previous",
              "active": false,
              "page": null
            },
            {
              "url": "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10",
              "label": "1",
              "active": true,
              "page": 1
            },
            {
              "url": null,
              "label": "Next &raquo;",
              "active": false,
              "page": null
            }
          ],
          "next_page_url": null,
          "items_per_page": 10,
          "prev_page_url": null,
          "to": 1,
          "total": 1
        }
        """
)
public class DatasourceFileListPagination {
    
    /**
     * 현재 페이지 번호
     * 
     * <p>현재 조회 중인 페이지의 번호입니다. 1부터 시작합니다.</p>
     */
    @JsonProperty("page")
    @Schema(
        description = "현재 페이지 번호",
        example = "1",
        minimum = "1"
    )
    private Integer page;
    
    /**
     * 첫 페이지 URL
     * 
     * <p>첫 번째 페이지로 이동하는 URL입니다.</p>
     */
    @JsonProperty("first_page_url")
    @Schema(
        description = "첫 페이지 URL",
        example = "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10"
    )
    private String firstPageUrl;
    
    /**
     * 시작 인덱스
     * 
     * <p>현재 페이지의 첫 번째 항목의 인덱스입니다. 1부터 시작합니다.</p>
     */
    @JsonProperty("from_")
    @Schema(
        description = "시작 인덱스",
        example = "1",
        minimum = "1"
    )
    private Integer from;
    
    /**
     * 마지막 페이지 번호
     * 
     * <p>전체 데이터를 현재 페이지 크기로 나눈 마지막 페이지 번호입니다.</p>
     */
    @JsonProperty("last_page")
    @Schema(
        description = "마지막 페이지 번호",
        example = "1",
        minimum = "1"
    )
    private Integer lastPage;
    
    /**
     * 페이징 링크 목록
     * 
     * <p>페이지 네비게이션을 위한 링크 정보 목록입니다.</p>
     */
    @JsonProperty("links")
    @Schema(
        description = "페이징 링크 목록"
    )
    private List<DatasourceFileListPaginationLink> links;
    
    /**
     * 다음 페이지 URL
     * 
     * <p>다음 페이지로 이동하는 URL입니다. null인 경우 다음 페이지가 없음을 의미합니다.</p>
     */
    @JsonProperty("next_page_url")
    @Schema(
        description = "다음 페이지 URL",
        example = "null"
    )
    private String nextPageUrl;
    
    /**
     * 페이지당 항목 수
     * 
     * <p>한 페이지에 표시되는 최대 항목 수입니다.</p>
     */
    @JsonProperty("items_per_page")
    @Schema(
        description = "페이지당 항목 수",
        example = "10",
        minimum = "1"
    )
    private Integer itemsPerPage;
    
    /**
     * 이전 페이지 URL
     * 
     * <p>이전 페이지로 이동하는 URL입니다. null인 경우 이전 페이지가 없음을 의미합니다.</p>
     */
    @JsonProperty("prev_page_url")
    @Schema(
        description = "이전 페이지 URL",
        example = "null"
    )
    private String prevPageUrl;
    
    /**
     * 끝 인덱스
     * 
     * <p>현재 페이지의 마지막 항목의 인덱스입니다.</p>
     */
    @JsonProperty("to")
    @Schema(
        description = "끝 인덱스",
        example = "1",
        minimum = "1"
    )
    private Integer to;
    
    /**
     * 전체 항목 수
     * 
     * <p>필터링 조건을 적용한 후의 전체 항목 수입니다.</p>
     */
    @JsonProperty("total")
    @Schema(
        description = "전체 항목 수",
        example = "1",
        minimum = "0"
    )
    private Integer total;
}
