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
 * 커스텀 스크립트 응답 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커스텀 스크립트 응답 클래스")
public class CustomScriptsResponse {    
    @JsonProperty("data")
    @Schema(description = "Few-Shot 목록")
    private List<CustomScriptsSummary> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    /**
     * Custom Script 요약 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Custom Script 요약 정보")
    public static class CustomScriptsSummary {
        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @JsonProperty("description")
        @Schema(description = "커스텀 스크립트 설명")
        private String description;

        @JsonProperty("name")
        @Schema(description = "커스텀 스크립트 이름")
        private String name;

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

        @JsonProperty("id")
        @Schema(description = "커스텀 스크립트 ID")
        private String id;

        @JsonProperty("script_type")
        @Schema(description = "커스텀 스크립트 타입")
        private String scriptType;
    }
}
