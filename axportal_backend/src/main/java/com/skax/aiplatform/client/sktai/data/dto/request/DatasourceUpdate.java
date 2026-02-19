package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 업데이트 요청")
public class DatasourceUpdate {
    
    @JsonProperty("name")
    @Schema(description = "데이터소스 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "데이터소스 설명")
    private String description;
    
    @JsonProperty("connection_config")
    @Schema(description = "연결 설정")
    private Map<String, Object> connectionConfig;
    
    @JsonProperty("schema_config")
    @Schema(description = "스키마 설정")
    private Map<String, Object> schemaConfig;
    
    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<DatasetTags> tags;
    
    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;
}
