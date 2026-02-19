package com.skax.aiplatform.dto.lineage.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lineage 관계 응답 DTO
 * 
 * <p>Lineage 관계 정보를 담는 응답 데이터입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lineage 관계 정보")
public class LineageRelationRes {
    
    @JsonProperty("source_key")
    @Schema(description = "소스 객체 키", example = "a0f49edd-6766-4758-92a3-13c066648bc0")
    private String sourceKey;
    
    @JsonProperty("target_key")
    @Schema(description = "타겟 객체 키", example = "a0f49edd-6766-4758-92a3-13c066648bc0")
    private String targetKey;
    
    @JsonProperty("action")
    @Schema(description = "액션 타입", example = "USE")
    private String action;
    
    @JsonProperty("depth")
    @Schema(description = "탐색 깊이", example = "1")
    private Integer depth;
    
    @JsonProperty("source_type")
    @Schema(description = "소스 객체 타입", example = "VECTOR_DB")
    private String sourceType;
    
    @JsonProperty("target_type")
    @Schema(description = "타겟 객체 타입", example = "VECTOR_DB")
    private String targetType;
}
