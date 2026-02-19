package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 페이지네이션 링크 DTO
 * 
 * <p>페이지네이션에서 사용되는 개별 링크 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이지네이션 링크 정보")
public class PaginationLink {
    
    /**
     * 링크 URL
     */
    @JsonProperty("url")
    @Schema(description = "링크 URL")
    private String url;
    
    /**
     * 링크 레이블
     */
    @JsonProperty("label")
    @Schema(description = "링크 레이블", example = "1")
    private String label;
    
    /**
     * 활성 상태
     */
    @JsonProperty("active")
    @Schema(description = "활성 상태", example = "true")
    private Boolean active;
    
    /**
     * 페이지 번호
     */
    @JsonProperty("page")
    @Schema(description = "페이지 번호", example = "1")
    private Integer page;
}
