package com.skax.aiplatform.client.sktai.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 공통 페이로드 DTO
 * 
 * <p>SKTAI API 응답의 페이로드 정보를 담는 구조입니다.
 * 페이지네이션 정보를 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI API 응답 페이로드")
public class Payload {
    
    /**
     * 페이지네이션 정보
     */
    @JsonProperty("pagination")
    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
