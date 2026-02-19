package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "데이터 벡터 데이터베이스 생성 요청 정보"
)
public class DataToolVectorDBUpdateReq {
    @Schema(
        description = "Vector DB 이름 (최대 200자)",
        example = "Milvus_add",
        required = true,
        maxLength = 200
    )
    private String name;

    @Schema(
        description = "Vector DB 종류",
        example = "Milvus",
        required = true,
        allowableValues = {"Milvus", "AzureAISearch", "AzureAISearchShared", "OpenSearch", "ElasticSearch"}
    )
    private String type;

    @Schema(
        description = "기본 Vector DB로 설정 여부",
        example = "False"
    )
    private String isDefault;

    @Schema(description = "데이터 수집 도구 연결 정보", required = true)
    private ConnectionInfo connectionInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "연결 정보")
    public static class ConnectionInfo {
        private String key;
        private String endpoint;
        private String host;
        private String port;
        private String user;
        private String password;
        private String secure;
        private String dbName;
        private String apiKey;
    }
}
