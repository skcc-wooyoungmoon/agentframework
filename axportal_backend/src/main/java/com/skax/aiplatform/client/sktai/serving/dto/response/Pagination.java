package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 페이지네이션 정보 DTO
 * 
 * <p>SKTAI API에서 사용하는 페이지네이션 메타데이터를 나타냅니다.
 * 목록 조회 API의 응답에서 페이지 정보와 내비게이션 링크를 제공합니다.</p>
 * 
 * <h3>페이지네이션 정보:</h3>
 * <ul>
 *   <li><strong>현재 페이지 정보</strong>: page, from, to</li>
 *   <li><strong>전체 정보</strong>: total, last_page, items_per_page</li>
 *   <li><strong>내비게이션 링크</strong>: first_page_url, next_page_url, prev_page_url, links</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * Pagination pagination = response.getPayload().getPagination();
 * int currentPage = pagination.getPage();
 * int totalItems = pagination.getTotal();
 * boolean hasNext = pagination.getNextPageUrl() != null;
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see PaginationLinks 페이지네이션 링크 정보
 */
@Deprecated // 공통 Pagination(com.skax.aiplatform.client.sktai.common.dto.Pagination) 사용 권장
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API 페이지네이션 정보 (deprecated: 공통 Pagination 사용 권장)",
    example = """
        {
          "page": 1,
          "first_page_url": "https://api.example.com/servings?page=1",
          "from_": 1,
          "last_page": 5,
          "links": [
            {"url": null, "label": "이전", "active": false, "page": null},
            {"url": "https://api.example.com/servings?page=1", "label": "1", "active": true, "page": 1},
            {"url": "https://api.example.com/servings?page=2", "label": "다음", "active": false, "page": 2}
          ],
          "next_page_url": "https://api.example.com/servings?page=2",
          "items_per_page": 10,
          "prev_page_url": null,
          "to": 10,
          "total": 50
        }
        """
)
public class Pagination {
    
    /**
     * 현재 페이지 번호
     * 
     * <p>1부터 시작하는 현재 페이지 번호입니다.</p>
     */
    @JsonProperty("page")
    @Schema(
        description = "현재 페이지 번호 (1부터 시작)",
        example = "1",
        required = true
    )
    private Integer page;
    
    /**
     * 첫 페이지 URL
     * 
     * <p>첫 번째 페이지로 이동할 수 있는 URL입니다.</p>
     */
    @JsonProperty("first_page_url")
    @Schema(
        description = "첫 페이지 URL",
        example = "https://api.example.com/servings?page=1",
        required = true
    )
    private String firstPageUrl;
    
    /**
     * 현재 페이지의 첫 번째 항목 순번
     * 
     * <p>전체 목록에서 현재 페이지의 첫 번째 항목이 몇 번째인지를 나타냅니다.</p>
     */
    @JsonProperty("from_")
    @Schema(
        description = "현재 페이지의 첫 번째 항목 순번",
        example = "1",
        required = true
    )
    private Integer from;
    
    /**
     * 마지막 페이지 번호
     * 
     * <p>전체 데이터를 기준으로 한 마지막 페이지 번호입니다.</p>
     */
    @JsonProperty("last_page")
    @Schema(
        description = "마지막 페이지 번호",
        example = "5",
        required = true
    )
    private Integer lastPage;
    
    /**
     * 페이지네이션 링크 목록
     * 
     * <p>페이지네이션 UI에서 사용할 수 있는 링크들의 목록입니다.
     * 이전/다음 버튼과 페이지 번호 링크들을 포함합니다.</p>
     */
    @JsonProperty("links")
    @Schema(
        description = "페이지네이션 링크 목록 (이전/다음 버튼 및 페이지 번호 링크)",
        required = true
    )
    private List<PaginationLinks> links;
    
    /**
     * 다음 페이지 URL
     * 
     * <p>다음 페이지로 이동할 수 있는 URL입니다.
     * 마지막 페이지인 경우 null입니다.</p>
     */
    @JsonProperty("next_page_url")
    @Schema(
        description = "다음 페이지 URL (마지막 페이지인 경우 null)",
        example = "https://api.example.com/servings?page=2",
        maxLength = 255
    )
    private String nextPageUrl;
    
    /**
     * 페이지당 항목 수
     * 
     * <p>한 페이지에 표시되는 항목의 개수입니다.</p>
     */
    @JsonProperty("items_per_page")
    @Schema(
        description = "페이지당 항목 수",
        example = "10",
        required = true
    )
    private Integer itemsPerPage;
    
    /**
     * 이전 페이지 URL
     * 
     * <p>이전 페이지로 이동할 수 있는 URL입니다.
     * 첫 번째 페이지인 경우 null입니다.</p>
     */
    @JsonProperty("prev_page_url")
    @Schema(
        description = "이전 페이지 URL (첫 번째 페이지인 경우 null)",
        example = "https://api.example.com/servings?page=1",
        maxLength = 255
    )
    private String prevPageUrl;
    
    /**
     * 현재 페이지의 마지막 항목 순번
     * 
     * <p>전체 목록에서 현재 페이지의 마지막 항목이 몇 번째인지를 나타냅니다.</p>
     */
    @JsonProperty("to")
    @Schema(
        description = "현재 페이지의 마지막 항목 순번",
        example = "10",
        required = true
    )
    private Integer to;
    
    /**
     * 전체 항목 수
     * 
     * <p>필터링된 전체 데이터의 항목 개수입니다.</p>
     */
    @JsonProperty("total")
    @Schema(
        description = "전체 항목 수",
        example = "50",
        required = true
    )
    private Integer total;
}