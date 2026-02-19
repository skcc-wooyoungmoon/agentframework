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
@Schema(description = "프로세서 생성 요청")
public class ProcessorCreate {
    
    @JsonProperty("name")
    @Schema(description = "프로세서 이름", required = true)
    private String name;
    
    @JsonProperty("type")
    @Schema(description = "프로세서 타입", required = true)
    private String type;
    
    @JsonProperty("description")
    @Schema(description = "프로세서 설명")
    private String description;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", required = true)
    private String projectId;
    
    @JsonProperty("config")
    @Schema(description = "프로세서 설정")
    private Map<String, Object> config;
    
    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<DatasetTags> tags;
    
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;
}
