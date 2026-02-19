package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Graph 생성 응답 DTO
 * 
 * <p>Agent 그래프 생성 성공 시 반환되는 응답 데이터 구조입니다.
 * 생성된 그래프의 기본 정보를 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Graph 생성 성공 응답")
public class GraphCreateResponse {
    
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
    @Schema(description = "응답 데이터")
    private GraphData data;
    
    @JsonProperty("payload")
    @Schema(description = "추가 페이로드")
    private Object payload;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "그래프 데이터")
    public static class GraphData {
        
        @JsonProperty("id")
        @Schema(description = "생성된 그래프의 고유 식별자")
        private String id;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;
        
        @JsonProperty("name")
        @Schema(description = "생성된 그래프 이름")
        private String name;
        
        @JsonProperty("description")
        @Schema(description = "그래프 설명")
        private String description;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "업데이트 시간")
        private String updatedAt;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;
        
        @JsonProperty("updated_by")
        @Schema(description = "업데이트자")
        private String updatedBy;
    }
    
    // 편의 메서드: graphUuid를 data.id로 매핑
    public String getGraphUuid() {
        return data != null ? data.getId() : null;
    }
    
    public String getName() {
        return data != null ? data.getName() : null;
    }
    
    public String getCreatedAt() {
        return data != null ? data.getCreatedAt() : null;
    }
}
