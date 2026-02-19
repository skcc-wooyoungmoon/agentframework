package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API Key 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 API 키 관련 작업의 응답 데이터를 담는 구조입니다.
 * API 키 생성, 조회 등의 작업 결과를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: API 키 ID, API 키 값, 내부 키</li>
 *   <li><strong>서빙 정보</strong>: 접근 가능한 서빙 ID 목록</li>
 *   <li><strong>유효 기간</strong>: 시작일, 만료일</li>
 *   <li><strong>보안 정보</strong>: 허용 호스트, 마스터 키 여부, 활성화 여부</li>
 *   <li><strong>메타데이터</strong>: 태그, 게이트웨이 타입</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API Key 응답 정보",
    example = """
        {
          "allowed_host": ["127.0.0.1"],
          "api_key": "sk-92a846d7f3014bb817bd57487fa00eec",
          "api_key_id": "uuid",
          "created_at": "2024-10-02",
          "expires_at": "2025-10-02",
          "gateway_type": "mcp",
          "internal_key": "sk-d21a4bbd5d9e4eaa3842cb4c8911cb7c",
          "is_active": true,
          "is_master": false,
          "serving_id": ["serving_id1"],
          "started_at": "2024-10-03",
          "tag": ["tag1"]
        }
        """
)
public class ApiKeyResponse {
    
    /**
     * 허용된 호스트 목록
     * 
     * <p>API 키를 사용할 수 있는 호스트 주소 목록입니다.</p>
     */
    @JsonProperty("allowed_host")
    @Schema(description = "허용된 호스트 주소 목록", example = "[\"127.0.0.1\"]")
    private List<String> allowedHost;
    
    /**
     * API 키 값
     * 
     * <p>실제 인증에 사용되는 API 키 값입니다.
     * 'sk-'로 시작하는 32자리 헥사 문자열입니다.</p>
     */
    @JsonProperty("api_key")
    @Schema(description = "API 키 값 (sk-로 시작하는 32자리 헥사 문자열)", example = "sk-92a846d7f3014bb817bd57487fa00eec")
    private String apiKey;
    
    /**
     * API 키 식별자
     * 
     * <p>API 키의 고유 식별자입니다.</p>
     */
    @JsonProperty("api_key_id")
    @Schema(description = "API 키 고유 식별자", example = "uuid")
    private String apiKeyId;
    
    /**
     * 생성 날짜
     * 
     * <p>API 키가 생성된 날짜입니다.
     * YYYY-MM-DD 형식의 날짜 문자열입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(description = "API 키 생성 날짜 (YYYY-MM-DD 형식)", example = "2024-10-02", format = "date")
    private String createdAt;
    
    /**
     * 만료 날짜
     * 
     * <p>API 키가 만료되는 날짜입니다.
     * YYYY-MM-DD 형식의 날짜 문자열입니다.</p>
     */
    @JsonProperty("expires_at")
    @Schema(description = "API 키 만료 날짜 (YYYY-MM-DD 형식)", example = "2025-10-02", format = "date")
    private String expiresAt;
    
    /**
     * 게이트웨이 타입
     * 
     * <p>API 키가 사용될 게이트웨이의 타입입니다.
     * model, agent, mcp 등의 값을 가질 수 있습니다.</p>
     */
    @JsonProperty("gateway_type")
    @Schema(description = "게이트웨이 타입", example = "mcp")
    private String gatewayType;
    
    /**
     * 내부 API 키
     * 
     * <p>내부 시스템에서 사용하는 API 키 값입니다.</p>
     */
    @JsonProperty("internal_key")
    @Schema(description = "내부 API 키 값", example = "sk-d21a4bbd5d9e4eaa3842cb4c8911cb7c")
    private String internalKey;
    
    /**
     * 활성화 여부
     * 
     * <p>API 키가 활성화되어 있는지 여부를 지정합니다.
     * 비활성화된 키는 사용할 수 없습니다.</p>
     */
    @JsonProperty("is_active")
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
    
    /**
     * 마스터 키 여부
     * 
     * <p>마스터 키인지 여부를 지정합니다.
     * 마스터 키는 모든 서빙에 접근할 수 있는 권한을 가집니다.</p>
     */
    @JsonProperty("is_master")
    @Schema(description = "마스터 키 여부", example = "false")
    private Boolean isMaster;
    
    /**
     * 서빙 ID 목록
     * 
     * <p>API 키가 접근할 수 있는 서빙들의 ID 목록입니다.</p>
     */
    @JsonProperty("serving_id")
    @Schema(description = "접근 가능한 서빙 ID 목록", example = "[\"serving_id1\"]")
    private List<String> servingId;
    
    /**
     * 유효 시작일
     * 
     * <p>API 키가 유효해지는 시작 날짜입니다.
     * YYYY-MM-DD 형식의 날짜 문자열입니다.</p>
     */
    @JsonProperty("started_at")
    @Schema(description = "API 키 유효 시작일 (YYYY-MM-DD 형식)", example = "2024-10-03", format = "date")
    private String startedAt;
    
    /**
     * 태그 목록
     * 
     * <p>API 키를 분류하고 관리하기 위한 태그 목록입니다.</p>
     */
    @JsonProperty("tag")
    @Schema(description = "API 키 태그 목록", example = "[\"tag1\"]")
    private List<String> tag;
}
