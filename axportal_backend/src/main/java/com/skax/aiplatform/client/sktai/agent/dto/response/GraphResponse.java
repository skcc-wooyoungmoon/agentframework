package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Graph 상세 정보 응답 DTO
 * 
 * <p>Agent 그래프의 상세 정보를 담는 응답 데이터 구조입니다.
 * 그래프의 모든 메타데이터, 노드, 엣지, 설정 정보를 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Graph 상세 정보 응답")
public class GraphResponse {
    
    @JsonProperty("id")
    @Schema(description = "그래프 ID")
    private String id;
    
    // graph_uuid는 SKT AI Platform API에서 제공하지 않으므로 제거
    // @JsonProperty("graph_uuid")
    // @Schema(description = "그래프 고유 식별자")
    // private String graphUuid;
    
    @JsonProperty("name")
    @Schema(description = "그래프 이름")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "그래프 설명")
    private String description;
    
    @JsonProperty("type")
    @Schema(description = "그래프 타입")
    private String type;
    
    @JsonProperty("category")
    @Schema(description = "그래프 카테고리")
    private String category;
    
    @JsonProperty("status")
    @Schema(description = "그래프 상태")
    private String status;
    
    @JsonProperty("nodes")
    @Schema(description = "그래프 노드 목록")
    private List<Object> nodes;
    
    @JsonProperty("edges")
    @Schema(description = "그래프 엣지 목록")
    private List<Object> edges;
    
    @JsonProperty("config")
    @Schema(description = "그래프 설정")
    private Object config;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private String createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "마지막 수정 시간")
    private String updatedAt;
    
    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;
    
    @JsonProperty("updated_by")
    @Schema(description = "마지막 수정자")
    private String updatedBy;
}
