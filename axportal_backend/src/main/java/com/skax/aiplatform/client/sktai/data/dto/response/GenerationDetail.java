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

/**
 * 생성 작업 상세 정보 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "생성 작업 상세 정보")
public class GenerationDetail {
    
    @JsonProperty("id")
    @Schema(description = "생성 작업 ID")
    private UUID id;
    
    @JsonProperty("name")
    @Schema(description = "생성 작업 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "생성 작업 설명")
    private String description;
    
    @JsonProperty("type")
    @Schema(description = "생성 작업 타입")
    private String type;
    
    @JsonProperty("status")
    @Schema(description = "생성 작업 상태")
    private String status;
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
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
    private List<DatasetTag> tags;
    
    @JsonProperty("config")
    @Schema(description = "생성 설정")
    private Object config;
    
    @JsonProperty("progress")
    @Schema(description = "진행률 (0-100)")
    private Integer progress;
    
    @JsonProperty("error_message")
    @Schema(description = "에러 메시지")
    private String errorMessage;
    
    @JsonProperty("result")
    @Schema(description = "생성 결과")
    private Object result;
    
    @JsonProperty("logs")
    @Schema(description = "실행 로그")
    private List<String> logs;
    
    @JsonProperty("metrics")
    @Schema(description = "성능 메트릭")
    private Map<String, Object> metrics;
    
    @JsonProperty("created_at")
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
    
    @JsonProperty("started_at")
    @Schema(description = "시작일시")
    private LocalDateTime startedAt;
    
    @JsonProperty("completed_at")
    @Schema(description = "완료일시")
    private LocalDateTime completedAt;
    
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;
    
    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;
}
