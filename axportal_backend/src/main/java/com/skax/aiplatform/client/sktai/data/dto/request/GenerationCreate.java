package com.skax.aiplatform.client.sktai.data.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 생성 작업 생성 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "생성 작업 생성 요청 정보")
public class GenerationCreate {
    
    @JsonProperty("name")
    @Schema(description = "생성 작업 이름", required = true)
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "생성 작업 설명")
    private String description;
    
    @JsonProperty("type")
    @Schema(description = "생성 작업 타입", example = "text_generation")
    private String type;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", required = true)
    private String projectId;
    
    @JsonProperty("dataset_id")
    @Schema(description = "데이터셋 ID")
    private UUID datasetId;
    
    @JsonProperty("generator_id")
    @Schema(description = "생성기 ID")
    private String generatorId;
    
    @JsonProperty("parameters")
    @Schema(description = "생성 파라미터")
    private Map<String, Object> parameters;
    
    @JsonProperty("tags")
    @Schema(description = "태그 목록")
    private List<DatasetTags> tags;
    
    @JsonProperty("config")
    @Schema(description = "생성 설정")
    private Object config;
    
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;
}
