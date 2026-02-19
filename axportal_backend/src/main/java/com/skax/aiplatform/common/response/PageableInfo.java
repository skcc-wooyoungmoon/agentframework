package com.skax.aiplatform.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 페이지 정보 DTO
 * 
 * <p>PageResponse에서 사용하는 페이징 정보를 담는 클래스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-20
 * @version 1.0.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이지 정보")
public class PageableInfo {
    
    /**
     * 현재 페이지 번호 (0부터 시작)
     */
    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int page;
    
    /**
     * 페이지 크기
     */
    @Schema(description = "페이지 크기", example = "20")
    private int size;
    
    /**
     * 정렬 정보
     */
    @Schema(description = "정렬 정보", example = "createdAt,desc")
    private String sort;
}
