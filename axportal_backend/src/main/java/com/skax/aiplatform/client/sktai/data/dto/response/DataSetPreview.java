package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 데이터셋 미리보기 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터셋 미리보기 정보")
public class DataSetPreview {
    
    @JsonProperty("columns")
    @Schema(description = "컬럼 정보")
    private List<String> columns;
    
    @JsonProperty("rows")
    @Schema(description = "데이터 행")
    private List<Map<String, Object>> rows;
    
    @JsonProperty("total_rows")
    @Schema(description = "전체 행 수")
    private Long totalRows;
    
    @JsonProperty("sample_size")
    @Schema(description = "샘플 크기")
    private Integer sampleSize;
    
    @JsonProperty("data_types")
    @Schema(description = "데이터 타입 정보")
    private Map<String, String> dataTypes;
}
