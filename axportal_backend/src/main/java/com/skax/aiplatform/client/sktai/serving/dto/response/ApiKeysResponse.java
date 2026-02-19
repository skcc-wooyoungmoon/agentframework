package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * API 키 목록 조회 응답
 *
 * <p>모델 서빙에서 사용 가능한 Gateway API 키 목록을 조회하는 응답입니다.
 * 페이지네이션 정보와 함께 API 키 목록을 제공합니다.</p>
 *
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>data</strong>: API 키 목록</li>
 *   <li><strong>payload</strong>: 페이지네이션 메타데이터</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-12-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "API 키 목록 조회 응답")
public class ApiKeysResponse {

    @JsonProperty("data")
    @Schema(description = "API 키 목록")
    private List<ApiKeyItem> data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보 (페이지네이션 포함)")
    private PayloadApiKey payload;

    /**
     * API 키 정보 항목
     *
     * <p>각 API 키의 상세 정보를 포함합니다.</p>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "API 키 정보")
    public static class ApiKeyItem {

        @JsonProperty("serving_id")
        @Schema(description = "서빙 ID (null 가능)")
        private String servingId;

        @JsonProperty("created_at")
        @Schema(description = "생성일 (YYYY-MM-DD 형식)", example = "2025-12-08")
        private String createdAt;

        @JsonProperty("expires_at")
        @Schema(description = "만료일 (null 가능)", example = "2025-12-31")
        private String expiresAt;

        @JsonProperty("allowed_host")
        @Schema(description = "허용된 호스트 (null 가능)")
        private String allowedHost;

        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
        private String projectId;

        @JsonProperty("gateway_type")
        @Schema(description = "게이트웨이 타입", example = "model")
        private String gatewayType;

        @JsonProperty("tags")
        @Schema(description = "태그 목록 (빈 배열 가능)")
        private List<String> tags;

        @JsonProperty("started_at")
        @Schema(description = "시작일 (YYYY-MM-DD 형식)", example = "2025-12-08")
        private String startedAt;

        @JsonProperty("api_key")
        @Schema(description = "API 키 값", example = "sk-cda6cc4080c020f9ab702e29625cc816")
        private String apiKey;

        @JsonProperty("tag")
        @Schema(description = "태그 목록", example = "[\"agent_graph\", \"graph-2f7073ae-6930-41ed-8015-c230270e8619\"]")
        private List<String> tag;

        @JsonProperty("is_master")
        @Schema(description = "마스터 키 여부", example = "true")
        private Boolean isMaster;

        @JsonProperty("is_active")
        @Schema(description = "활성화 여부", example = "true")
        private Boolean isActive;

        @JsonProperty("internal_key")
        @Schema(description = "내부 키 (null 가능)")
        private String internalKey;

        @JsonProperty("api_key_id")
        @Schema(description = "API 키 ID", example = "9675b343-25ef-4788-a430-8b0ed4d8d5e0")
        private String apiKeyId;
    }
}
