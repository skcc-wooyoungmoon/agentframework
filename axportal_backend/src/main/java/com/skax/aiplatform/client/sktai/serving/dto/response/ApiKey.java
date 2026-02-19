package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI API 키 정보 DTO
 * 
 * <p>SKTAI Serving 시스템에서 생성된 API 키의 상세 정보를 나타냅니다.
 * API 키를 통해 서빙 엔드포인트에 인증된 접근을 할 수 있습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>API 키 정보</strong>: 키 값, 키 ID</li>
 *   <li><strong>서빙 정보</strong>: 연관된 서빙 ID 목록</li>
 *   <li><strong>유효 기간</strong>: 시작일, 만료일</li>
 *   <li><strong>접근 제어</strong>: 허용 호스트, 마스터 키 여부</li>
 *   <li><strong>메타데이터</strong>: 태그, 프로젝트 ID, 게이트웨이 타입</li>
 *   <li><strong>상태 정보</strong>: 활성화 여부, 생성 시간</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ApiKey apiKey = response.getData().get(0);
 * String keyValue = apiKey.getApiKey();
 * List&lt;String&gt; servingIds = apiKey.getServingId();
 * boolean isActive = apiKey.getIsActive();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see ApiKeyRead API 키 목록 응답
 * @see com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyCreate API 키 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI API 키 정보",
    example = """
        {
          "api_key": "sk-92a846d7f3014bb817bd57487fa00eec",
          "serving_id": ["srv-123e4567-e89b-12d3-a456-426614174000"],
          "created_at": "2024-10-02T10:30:00Z",
          "started_at": "2024-10-03",
          "expires_at": "2025-10-02",
          "tag": ["production", "web-api"],
          "allowed_host": ["127.0.0.1", "api.example.com"],
          "is_master": false,
          "project_id": "proj-123e4567-e89b-12d3-a456-426614174000",
          "is_active": true,
          "gateway_type": "model",
          "api_key_id": "key-123e4567-e89b-12d3-a456-426614174000"
        }
        """
)
public class ApiKey {
    
    /**
     * API 키 값
     * 
     * <p>실제 인증에 사용되는 API 키 문자열입니다.
     * 'sk-'로 시작하는 32자리 헥사 문자열입니다.</p>
     */
    @JsonProperty("api_key")
    @Schema(
        description = "API 키 값 (sk-로 시작하는 32자리 헥사 문자열)",
        example = "sk-92a846d7f3014bb817bd57487fa00eec",
        required = true
    )
    private String apiKey;
    
    /**
     * 연관된 서빙 ID 목록
     * 
     * <p>이 API 키로 접근할 수 있는 서빙들의 ID 목록입니다.
     * null인 경우 모든 서빙에 접근 가능함을 의미합니다.</p>
     */
    @JsonProperty("serving_id")
    @Schema(
        description = "연관된 서빙 ID 목록 (null인 경우 모든 서빙 접근 가능)",
        example = "[\"srv-123e4567-e89b-12d3-a456-426614174000\"]",
        required = true
    )
    private List<String> servingId;
    
    /**
     * 생성 시간
     * 
     * <p>API 키가 생성된 시간입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "API 키 생성 시간",
        example = "2024-10-02T10:30:00Z",
        format = "date-time",
        required = true
    )
    private LocalDateTime createdAt;
    
    /**
     * 유효 시작일
     * 
     * <p>API 키가 유효해지는 시작 날짜입니다.</p>
     */
    @JsonProperty("started_at")
    @Schema(
        description = "API 키 유효 시작일",
        example = "2024-10-03",
        format = "date",
        required = true
    )
    private LocalDate startedAt;
    
    /**
     * 만료일
     * 
     * <p>API 키가 만료되는 날짜입니다.</p>
     */
    @JsonProperty("expires_at")
    @Schema(
        description = "API 키 만료일",
        example = "2025-10-02",
        format = "date",
        required = true
    )
    private LocalDate expiresAt;
    
    /**
     * 태그 목록
     * 
     * <p>API 키를 분류하고 관리하기 위한 태그들입니다.</p>
     */
    @JsonProperty("tag")
    @Schema(
        description = "API 키 태그 목록 (분류 및 관리용)",
        example = "[\"production\", \"web-api\"]"
    )
    private List<String> tag;
    
    /**
     * 허용된 호스트 목록
     * 
     * <p>이 API 키로 접근할 수 있는 호스트들의 목록입니다.
     * IP 주소 또는 도메인 이름을 포함할 수 있습니다.</p>
     */
    @JsonProperty("allowed_host")
    @Schema(
        description = "허용된 호스트 목록 (IP 주소 또는 도메인)",
        example = "[\"127.0.0.1\", \"api.example.com\"]"
    )
    private List<String> allowedHost;
    
    /**
     * 마스터 키 여부
     * 
     * <p>이 키가 마스터 키인지를 나타냅니다.
     * 마스터 키는 모든 권한을 가지며 주의해서 사용해야 합니다.</p>
     */
    @JsonProperty("is_master")
    @Schema(
        description = "마스터 키 여부 (모든 권한 보유)",
        example = "false",
        defaultValue = "false",
        required = true
    )
    private Boolean isMaster;
    
    /**
     * 프로젝트 ID
     * 
     * <p>API 키가 속한 프로젝트의 ID입니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "API 키가 속한 프로젝트 ID",
        example = "proj-123e4567-e89b-12d3-a456-426614174000"
    )
    private String projectId;
    
    /**
     * 활성화 상태
     * 
     * <p>API 키의 활성화 상태를 나타냅니다.
     * false인 경우 키가 비활성화되어 사용할 수 없습니다.</p>
     */
    @JsonProperty("is_active")
    @Schema(
        description = "API 키 활성화 상태",
        example = "true",
        defaultValue = "true",
        required = true
    )
    private Boolean isActive;
    
    /**
     * 게이트웨이 타입
     * 
     * <p>API 키가 사용될 게이트웨이의 타입입니다.
     * 'model' (모델 서빙용) 또는 'agent' (에이전트 서빙용)를 지정할 수 있습니다.</p>
     */
    @JsonProperty("gateway_type")
    @Schema(
        description = "게이트웨이 타입 (model 또는 agent)",
        example = "model",
        defaultValue = "model"
    )
    private String gatewayType;
    
    /**
     * API 키 ID
     * 
     * <p>API 키의 고유 식별자입니다.</p>
     */
    @JsonProperty("api_key_id")
    @Schema(
        description = "API 키 고유 식별자",
        example = "key-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid",
        required = true
    )
    private String apiKeyId;
}