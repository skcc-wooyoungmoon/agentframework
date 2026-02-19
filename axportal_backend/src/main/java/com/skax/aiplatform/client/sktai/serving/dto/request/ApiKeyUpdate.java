package com.skax.aiplatform.client.sktai.serving.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI API Key 업데이트 요청 DTO
 *
 * <p>기존 API Key의 설정을 업데이트하기 위한 요청 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-09-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API Key 업데이트 요청 정보",
    example = """
        {
          "serving_id": ["serving-123"],
          "started_at": "2024-10-02",
          "expires_at": "2025-10-02",
          "tag": ["updated-tag"],
          "allowed_host": ["127.0.0.1", "localhost"],
          "is_master": false,
          "is_active": true
        }
        """
)
public class ApiKeyUpdate {

    /**
     * 서빙 ID 목록
     */
    @JsonProperty("serving_id")
    @Schema(
        description = "서빙 ID 목록",
        example = "[\"serving-123\"]",
        required = true
    )
    private List<String> servingId;

    /**
     * 시작 날짜
     */
    @JsonProperty("started_at")
    @Schema(
        description = "API 키 시작 날짜",
        example = "2024-10-02",
        required = true
    )
    private String startedAt;

    /**
     * 만료 날짜
     */
    @JsonProperty("expires_at")
    @Schema(
        description = "API 키 만료 날짜",
        example = "2025-10-02",
        required = true
    )
    private String expiresAt;

    /**
     * 태그 목록
     */
    @JsonProperty("tag")
    @Schema(
        description = "API 키 태그 목록",
        example = "[\"updated-tag\"]",
        required = true
    )
    private List<String> tag;

    /**
     * 허용된 호스트 목록
     */
    @JsonProperty("allowed_host")
    @Schema(
        description = "허용된 호스트 목록",
        example = "[\"127.0.0.1\", \"localhost\"]",
        required = true
    )
    private List<String> allowedHost;

    /**
     * 마스터 키 여부
     */
    @JsonProperty("is_master")
    @Schema(
        description = "마스터 키 여부",
        example = "false",
        required = true
    )
    private Boolean isMaster;

    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID")
    private String projectId;

    /**
     * 활성화 여부
     */
    @JsonProperty("is_active")
    @Schema(
        description = "API 키 활성화 여부",
        example = "true",
        required = true
    )
    private Boolean isActive;

    /**
     * 정책 설정
     */
    @JsonProperty("policy")
    @Schema(description = "정책 설정")
    private Object policy;

    /**
     * 게이트웨이 타입
     */
    @JsonProperty("gateway_type")
    @Schema(description = "게이트웨이 타입")
    private String gatewayType;

    /**
     * MCP 에이전트 앱 목록
     */
    @JsonProperty("mcp_agent_app_list")
    @Schema(description = "MCP 에이전트 앱 목록")
    private List<String> mcpAgentAppList;
}
