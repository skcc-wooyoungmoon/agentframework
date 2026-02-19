package com.skax.aiplatform.client.sktai.serving.dto.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API Key 생성 요청 DTO
 * 
 * <p>SKTAI Serving 시스템에서 새로운 API 키를 생성하기 위한 요청 데이터 구조입니다.
 * API 키는 서빙 엔드포인트에 대한 접근 권한을 관리하는 데 사용됩니다.</p>
 * 
 * <h3>API 키의 용도:</h3>
 * <ul>
 *   <li><strong>인증</strong>: 서빙 엔드포인트 접근 권한 제어</li>
 *   <li><strong>권한 관리</strong>: 특정 서빙에 대한 접근 권한 부여</li>
 *   <li><strong>사용량 추적</strong>: API 사용량 모니터링 및 과금</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 2.0
 * @see com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyResponse API 키 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API Key 생성 요청 정보",
    example = """
        {
          "serving_id": ["srv-123e4567-e89b-12d3-a456-426614174000"],
          "started_at": "2024-01-01",
          "expires_at": "2024-12-31",
          "tag": ["production", "web-api"],
          "allowed_host": ["127.0.0.1", "api.example.com"],
          "is_master": false,
          "project_id": "proj-123e4567-e89b-12d3-a456-426614174000",
          "is_active": true,
          "policy": [
            {
              "cascade": false,
              "decision_strategy": "UNANIMOUS",
              "logic": "POSITIVE",
              "policies": [
                {
                  "logic": "POSITIVE",
                  "names": ["admin"],
                  "type": "user"
                }
              ],
              "scopes": ["GET", "POST", "PUT", "DELETE"]
            }
          ],
          "gateway_type": "model",
          "mcp_agent_app_list": ["app-123"],
          "tags": [{}]
        }
        """
)
public class ApiKeyCreate {
    
    /**
     * 서빙 ID 목록
     * 
     * <p>API 키가 접근할 수 있는 서빙들의 ID 목록입니다.
     * 특정 서빙들에 대한 접근 권한을 부여합니다.</p>
     * 
     * @apiNote 서빙이 존재해야 하며, 사용자가 해당 서빙에 대한 권한을 가져야 합니다.
     */
    @JsonProperty("serving_id")
    @Schema(
        description = "접근 권한을 부여할 서빙 ID 목록", 
        example = "[\"srv-123e4567-e89b-12d3-a456-426614174000\"]"
    )
    private List<String> servingId;
    
    /**
     * API 키 유효 시작일
     * 
     * <p>API 키가 유효해지는 시작 날짜입니다.
     * YYYY-MM-DD 형식의 날짜 문자열로 지정합니다.</p>
     * 
     * @implNote 시작일 이전에는 API 키를 사용할 수 없습니다.
     */
    @JsonProperty("started_at")
    @Schema(
        description = "API 키 유효 시작일 (YYYY-MM-DD 형식)", 
        example = "2024-01-01",
        format = "date"
    )
    private String startedAt;
    
    /**
     * API 키 만료일
     * 
     * <p>API 키가 만료되는 날짜입니다.
     * YYYY-MM-DD 형식의 날짜 문자열로 지정합니다.</p>
     * 
     * @implNote 만료일 이후에는 API 키를 사용할 수 없습니다.
     * @apiNote 보안을 위해 적절한 만료일을 설정하는 것이 중요합니다.
     */
    @JsonProperty("expires_at")
    @Schema(
        description = "API 키 만료일 (YYYY-MM-DD 형식)", 
        example = "2024-12-31",
        format = "date"
    )
    private String expiresAt;
    
    /**
     * 태그 목록
     * 
     * <p>API 키를 분류하고 관리하기 위한 태그 목록입니다.</p>
     */
    @JsonProperty("tag")
    @Schema(
        description = "API 키 태그 목록",
        example = "[\"production\", \"web-api\"]"
    )
    private List<String> tag;
    
    /**
     * 허용된 호스트 목록
     * 
     * <p>API 키를 사용할 수 있는 호스트 주소 목록입니다.
     * 보안 강화를 위해 특정 호스트에서만 접근하도록 설정할 수 있습니다.</p>
     */
    @JsonProperty("allowed_host")
    @Schema(
        description = "허용된 호스트 주소 목록",
        example = "[\"127.0.0.1\", \"api.example.com\"]"
    )
    private List<String> allowedHost;
    
    /**
     * 마스터 키 여부
     * 
     * <p>마스터 키인지 여부를 지정합니다.
     * 마스터 키는 모든 서빙에 접근할 수 있는 권한을 가집니다.</p>
     */
    @JsonProperty("is_master")
    @Schema(
        description = "마스터 키 여부",
        example = "false",
        defaultValue = "false"
    )
    @Builder.Default
    private Boolean isMaster = false;
    
    /**
     * 프로젝트 ID
     * 
     * <p>API 키가 속한 프로젝트의 ID입니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "프로젝트 ID",
        example = "proj-123e4567-e89b-12d3-a456-426614174000"
    )
    private String projectId;
    
    /**
     * 활성화 여부
     * 
     * <p>API 키가 활성화되어 있는지 여부를 지정합니다.
     * 비활성화된 키는 사용할 수 없습니다.</p>
     */
    @JsonProperty("is_active")
    @Schema(
        description = "활성화 여부",
        example = "true",
        defaultValue = "true"
    )
    @Builder.Default
    private Boolean isActive = true;
    
    /**
     * 정책 목록
     * 
     * <p>API 키에 적용될 접근 정책 목록입니다.
     * HTTP 메서드 범위, 사용자/그룹/역할 기반 권한 등을 정의할 수 있습니다.</p>
     */
    @JsonProperty("policy")
    @Schema(
        description = "접근 정책 목록",
        example = """
            [
              {
                "cascade": false,
                "decision_strategy": "UNANIMOUS",
                "logic": "POSITIVE",
                "policies": [
                  {
                    "logic": "POSITIVE",
                    "names": ["admin"],
                    "type": "user"
                  }
                ],
                "scopes": ["GET", "POST", "PUT", "DELETE"]
              }
            ]
            """
    )
    private List<PolicyRequest> policy;
    
    /**
     * 게이트웨이 타입
     * 
     * <p>API 키가 사용될 게이트웨이의 타입입니다.
     * model, agent, mcp 등의 값을 가질 수 있습니다.</p>
     */
    @JsonProperty("gateway_type")
    @Schema(
        description = "게이트웨이 타입",
        example = "model",
        allowableValues = {"model", "agent", "mcp"}
    )
    private String gatewayType;
    
    /**
     * MCP 에이전트 앱 목록
     * 
     * <p>MCP 에이전트 앱 ID 목록입니다.</p>
     */
    @JsonProperty("mcp_agent_app_list")
    @Schema(
        description = "MCP 에이전트 앱 ID 목록",
        example = "[\"app-123\"]"
    )
    private List<String> mcpAgentAppList;
    
    /**
     * 태그 목록 (추가 속성)
     * 
     * <p>추가적인 태그 정보를 담는 객체 목록입니다.</p>
     */
    @JsonProperty("tags")
    @Schema(
        description = "추가 태그 정보 목록",
        example = "[{}]"
    )
    private List<Map<String, Object>> tags;
}
