package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 프로세서 파라미터 DTO
 * 
 * <p>OpenAPI 스펙에 따른 ProcessorParam 정의입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-20
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로세서 파라미터 정보")
public class ProcessorParam {
    
    @JsonProperty("ids")
    @Schema(description = "프로세서 ID 목록", defaultValue = "[]")
    private List<String> ids;
    
    @JsonProperty("duplicate_subset_columns")
    @Schema(description = "중복 제거 대상 컬럼 목록")
    private List<String> duplicateSubsetColumns;
    
    @JsonProperty("regular_expression")
    @Schema(description = "정규 표현식 목록")
    private List<String> regularExpression;
}
