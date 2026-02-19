package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 페이지네이션 정보 DTO
 * 
 * <p>SKTAI History API 응답에서 사용되는 페이지네이션 관련 정보입니다.
 * 전체 데이터 수, 현재 페이지, 페이지 크기 등의 정보를 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "페이지네이션 정보",
    example = """
        {
          "page": 1,
          "per_page": 20,
          "total": 150,
          "total_pages": 8,
          "has_next": true,
          "has_previous": false
        }
        """
)
public class PaginationInfo {
    
    /**
     * 현재 페이지 번호 (1부터 시작)
     */
    @JsonProperty("page")
    @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1", minimum = "1")
    private Integer page;
    
    /**
     * 페이지당 항목 수
     */
    @JsonProperty("per_page")
    @Schema(description = "페이지당 항목 수", example = "20", minimum = "1", maximum = "1000")
    private Integer perPage;
    
    /**
     * 전체 항목 수
     */
    @JsonProperty("total")
    @Schema(description = "전체 항목 수", example = "150", minimum = "0")
    private Integer total;
    
    /**
     * 전체 페이지 수
     */
    @JsonProperty("total_pages")
    @Schema(description = "전체 페이지 수", example = "8", minimum = "0")
    private Integer totalPages;
    
    /**
     * 다음 페이지 존재 여부
     */
    @JsonProperty("has_next")
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
    
    /**
     * 이전 페이지 존재 여부
     */
    @JsonProperty("has_previous")
    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private Boolean hasPrevious;
    
    /**
     * 현재 페이지의 첫 번째 항목 인덱스 (0부터 시작)
     */
    @JsonProperty("start_index")
    @Schema(description = "현재 페이지의 첫 번째 항목 인덱스 (0부터 시작)", example = "0")
    private Integer startIndex;
    
    /**
     * 현재 페이지의 마지막 항목 인덱스 (0부터 시작)
     */
    @JsonProperty("end_index")
    @Schema(description = "현재 페이지의 마지막 항목 인덱스 (0부터 시작)", example = "19")
    private Integer endIndex;
    
    /**
     * 현재 페이지의 실제 항목 수
     */
    @JsonProperty("count")
    @Schema(description = "현재 페이지의 실제 항목 수", example = "20")
    private Integer count;
}