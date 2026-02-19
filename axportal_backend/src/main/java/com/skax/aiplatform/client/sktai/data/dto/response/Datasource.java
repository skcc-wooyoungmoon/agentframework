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
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "데이터소스 정보")
public class Datasource {
    
    @JsonProperty("id")
    @Schema(description = "데이터소스 ID")
    private UUID id;
    
    @JsonProperty("name")
    @Schema(description = "데이터소스 이름")
    private String name;
    
    @JsonProperty("type")
    @Schema(description = "데이터소스 타입")
    private String type;
    
    @JsonProperty("description")
    @Schema(description = "데이터소스 설명")
    private String description;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;
    
    @JsonProperty("status")
    @Schema(description = "데이터소스 상태")
    private String status;
    
    @JsonProperty("connection_config")
    @Schema(description = "연결 설정")
    private Map<String, Object> connectionConfig;
    
    @JsonProperty("schema_config")
    @Schema(description = "스키마 설정")
    private Map<String, Object> schemaConfig;
    
    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<DatasetTag> tags;
    
    @JsonProperty("created_at")
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
    
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;
    
    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;
}
