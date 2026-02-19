package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

/**
 * 벡터 데이터베이스 응답 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "벡터 데이터베이스 응답 클래스")
public class VectorDbsResponse { 
    @JsonProperty("data")
    @Schema(description = "벡터 데이터베이스 목록")
    private List<VectorDBsSummary> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    /**
     * VectorDBsSummary 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "벡터 데이터베이스 정보")
    public static class VectorDBsSummary {
        
        @JsonProperty("name")
        @Schema(description = "벡터 데이터베이스 이름")
        private String name;
        
        @JsonProperty("type")
        @Schema(description = "벡터 데이터베이스 타입")
        private String type;
        
        @JsonProperty("id")
        @Schema(description = "벡터 데이터베이스 ID")
        private String id;
        
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;

        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;

        @JsonProperty("updated_at")
        @Schema(description = "생성 시간")
        private String updatedAt;

        @JsonProperty("updated_by")
        @Schema(description = "수정자")
        private String updatedBy;

        @JsonProperty("is_deleted")
        @Schema(description = "삭제 여부")
        private Boolean isDeleted;

        @JsonProperty("is_default")
        @Schema(description = "기본 여부")
        private Boolean isDefault;
    }
}
