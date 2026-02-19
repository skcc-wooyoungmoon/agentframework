package com.skax.aiplatform.dto.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class DataToolVectorDBDetailRes {
    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "벡터 데이터베이스 이름")
    private String name;

    @Schema(description = "벡터 데이터베이스 타입")
    private String type;

    @Schema(description = "연결 정보")
    private ConnectionInfo connectionInfo;

    @Schema(description = "기본 여부")
    private Boolean isDefault;

    @Schema(description = "생성 시간")
    private String createdAt;

    @Schema(description = "생성자")
    private String createdBy;

    @Schema(description = "수정 시간")
    private String updatedAt;

    @Schema(description = "수정자")
    private String updatedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "연결 정보")
    public static class ConnectionInfo {
        private String endpoint;
        private String key;
        private String host;
        private String port;
        private String user;
        private String password;
        private String secure;
        private String dbName;
        private String apiKey;
    }
}
