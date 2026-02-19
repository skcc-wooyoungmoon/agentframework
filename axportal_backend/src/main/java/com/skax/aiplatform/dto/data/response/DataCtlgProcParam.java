package com.skax.aiplatform.dto.data.response;

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
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로세서 파라미터 정보")
public class DataCtlgProcParam {
    
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
