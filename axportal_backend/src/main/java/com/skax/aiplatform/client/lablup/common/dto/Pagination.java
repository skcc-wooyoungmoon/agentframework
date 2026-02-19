package com.skax.aiplatform.client.lablup.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup API 페이지네이션 정보 DTO
 * 
 * <p>Lablup API의 페이지네이션 요청 및 응답에 사용되는 공통 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이지네이션 정보")
public class Pagination {
    
    /**
     * 페이지 번호 (0부터 시작)
     */
    @JsonProperty("page")
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", minimum = "0")
    private Integer page;
    
    /**
     * 페이지 크기
     */
    @JsonProperty("size")
    @Schema(description = "페이지 크기", example = "20", minimum = "1", maximum = "100")
    private Integer size;
    
    /**
     * 전체 요소 수
     */
    @JsonProperty("total_elements")
    @Schema(description = "전체 요소 수", example = "150")
    private Long totalElements;
    
    /**
     * 전체 페이지 수
     */
    @JsonProperty("total_pages")
    @Schema(description = "전체 페이지 수", example = "8")
    private Integer totalPages;
}