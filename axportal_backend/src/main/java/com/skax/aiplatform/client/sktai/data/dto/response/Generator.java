package com.skax.aiplatform.client.sktai.data.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "생성기 정보")
public class Generator {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("project_id")
    private String projectId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("model_config")
    private Map<String, Object> modelConfig;
    
    @JsonProperty("parameters")
    private Map<String, Object> parameters;
    
    @JsonProperty("tags")
    private List<DatasetTag> tags;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    @JsonProperty("created_by")
    private String createdBy;
    
    @JsonProperty("updated_by")
    private String updatedBy;
}
