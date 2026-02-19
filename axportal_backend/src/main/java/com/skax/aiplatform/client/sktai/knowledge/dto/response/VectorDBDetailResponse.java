package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 벡터 데이터베이스 상세 응답 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "벡터 데이터베이스 상세 응답 클래스")
public class VectorDBDetailResponse {
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @JsonProperty("name")
    @Schema(description = "벡터 데이터베이스 이름")
    private String name;

    @JsonProperty("type")
    @Schema(description = "벡터 데이터베이스 타입")
    private String type;

    @JsonProperty("connection_info")
    @Schema(description = "연결 정보")
    private ConnectionInfo connectionInfo;

    @JsonProperty("is_default")
    @Schema(description = "기본 여부")
    private Boolean isDefault;

    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private String createdAt;

    @JsonProperty("created_by")
    @Schema(description = "생성자")
    private String createdBy;

    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private String updatedAt;

    @JsonProperty("updated_by")
    @Schema(description = "수정자")
    private String updatedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "연결 정보")
    public static class ConnectionInfo {
        private String endpoint;
        private String key;
        private String host;
        private String port;
        private String user;
        private String password;
        private String secure;
        @JsonProperty("api_key")
        private String apiKey;

        @JsonProperty("db_name")
        private String dbName;
    }

}