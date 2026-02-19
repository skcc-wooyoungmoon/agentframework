package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Graph 전체 저장 요청 DTO
 * 
 * <p>SKT AI Platform에서 Agent Graph의 전체 구조(노드, 엣지, 메타데이터)를 저장하기 위한 요청 데이터 구조입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-09-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 전체 저장 요청",
    example = """
        {
          "name": "research_flow",
          "description": "search from wikipedia and create answer",
          "graph": {
            "edges": [
              {
                "id": "edge_in",
                "source": "inputnode_id",
                "target": "agent_id",
                "type": "none"
              }
            ],
            "nodes": [
              {
                "id": "inputnode_id",
                "type": "input__basic",
                "data": {
                  "name": "input",
                  "input_keys": []
                }
              }
            ]
          }
        }
        """
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphSaveRequest {
    
    /**
     * Graph 이름
     */
    @JsonProperty("name")
    @Schema(
        description = "Graph의 이름", 
        example = "research_flow",
        required = true,
        minLength = 2,
        maxLength = 100
    )
    private String name;
    
    /**
     * Graph 설명
     */
    @JsonProperty("description")
    @Schema(
        description = "Graph의 설명", 
        example = "search from wikipedia and create answer",
        maxLength = 1000
    )
    private String description;
    
    /**
     * Graph 구조 (노드와 엣지)
     */
    @JsonProperty("graph")
    @Schema(
        description = "Graph의 구조 정보 (노드와 엣지)",
        required = true
    )
    private GraphStructure graph;
    
    /**
     * Graph 구조 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GraphStructure {
        
        /**
         * Graph의 엣지 목록
         */
        @JsonProperty("edges")
        @Schema(description = "Graph의 엣지 목록")
        private List<GraphEdge> edges;
        
        /**
         * Graph의 노드 목록
         */
        @JsonProperty("nodes")
        @Schema(description = "Graph의 노드 목록")
        private List<GraphNode> nodes;
    }
    
    /**
     * Graph 엣지 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GraphEdge {
        
        // String 타입 필드들
        @JsonProperty("condition_label")
        @Schema(description = "조건 레이블")
        private String condition_label;
        
        @JsonProperty("id")
        @Schema(description = "엣지 ID")
        private String id;
        
        @JsonProperty("label")
        @Schema(description = "엣지 레이블")
        private String label;
        
        @JsonProperty("marker_end")
        @Schema(description = "엣지 끝 마커 (snake_case)")
        private String marker_end;
        
        @JsonProperty("marker_start")
        @Schema(description = "엣지 시작 마커 (snake_case)")
        private String marker_start;
        
        @JsonProperty("reconnectable")
        @Schema(description = "재연결 가능 여부")
        private String reconnectable;
        
        @JsonProperty("source")
        @Schema(description = "소스 노드 ID")
        private String source;
        
        @JsonProperty("sourceHandle")
        @Schema(description = "소스 노드의 Handle ID (camelCase)")
        private String sourceHandle;
        
        @JsonProperty("source_handle")
        @Schema(description = "소스 노드의 Handle ID (snake_case)")
        private String source_handle;
        
        @JsonProperty("target")
        @Schema(description = "타겟 노드 ID")
        private String target;
        
        @JsonProperty("target_handle")
        @Schema(description = "타겟 노드의 Handle ID (snake_case)")
        private String target_handle;
        
        @JsonProperty("type")
        @Schema(description = "엣지 타입")
        private String type;
        
        // Object 타입 필드들
        @JsonProperty("data")
        @Schema(description = "엣지 데이터 객체 (send_to, category, edge_type, send_from 포함)")
        private Object data;
        
        @JsonProperty("markerEnd")
        @Schema(description = "엣지 끝 마커 (camelCase)")
        private Object markerEnd;
        
        @JsonProperty("style")
        @Schema(description = "엣지 스타일 (stroke, strokeWidth 등)")
        private Object style;
    }
    
    /**
     * Graph 노드 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GraphNode {
        
        @JsonProperty("id")
        @Schema(description = "노드 ID")
        private String id;
        
        @JsonProperty("type")
        @Schema(description = "노드 타입")
        private String type;
        
            @JsonProperty("data")
    @Schema(description = "노드 데이터")
    private Object data;
    
    @JsonProperty("position")
    @Schema(description = "노드 위치")
    private Object position;
    
    @JsonProperty("style")
    @Schema(description = "노드 스타일")
    private Object style;
    
    @JsonProperty("source_position")
    @Schema(description = "소스 포지션 (left/right)")
    private String sourcePosition;
    
    @JsonProperty("target_position")
    @Schema(description = "타겟 포지션 (left/right)")
    private String targetPosition;
    
    @JsonProperty("measured")
    @Schema(description = "노드 측정값 (width, height)")
    private Object measured;
    }
}
