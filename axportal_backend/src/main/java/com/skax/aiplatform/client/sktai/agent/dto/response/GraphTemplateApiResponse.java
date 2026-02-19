package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * SKT AI Platform Graph 템플릿 API 응답 래퍼 DTO
 * 
 * <p>실제 SKT AI Platform API의 응답 구조를 정확히 매핑합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-09-05
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKT AI Platform Graph 템플릿 API 응답")
public class GraphTemplateApiResponse {
    
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;
    
    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;
    
    @JsonProperty("detail")
    @Schema(description = "응답 상세")
    private String detail;
    
    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;
    
    @JsonProperty("data")
    @Schema(description = "실제 그래프 데이터")
    private GraphTemplateData data;
    
    @JsonProperty("payload")
    @Schema(description = "추가 페이로드")
    private Object payload;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "그래프 템플릿 데이터")
    public static class GraphTemplateData {
        
        @JsonProperty("id")
        @Schema(description = "그래프 ID")
        private String id;
        
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
        private List<Map<String, Object>> nodes;
        
        @JsonProperty("edges")
        @Schema(description = "그래프 엣지 목록")
        private List<Map<String, Object>> edges;
        
        @JsonProperty("config")
        @Schema(description = "그래프 설정")
        private Map<String, Object> config;
        
        @JsonProperty("created_at")
        @Schema(description = "생성일시")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정일시")
        private String updatedAt;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;
        
        @JsonProperty("updated_by")
        @Schema(description = "수정자")
        private String updatedBy;
    }
}
