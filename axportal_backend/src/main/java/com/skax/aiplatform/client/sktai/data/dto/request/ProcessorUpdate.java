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
@Schema(description = "프로세서 업데이트 요청")
public class ProcessorUpdate {
    
    @JsonProperty("name")
    @Schema(description = "프로세서 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "프로세서 설명")
    private String description;
    
    @JsonProperty("config")
    @Schema(description = "프로세서 설정")
    private Map<String, Object> config;
    
    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<DatasetTags> tags;
    
    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;
}
