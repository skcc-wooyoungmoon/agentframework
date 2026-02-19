package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Agent Few-Shot Internal API 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot의 Internal API 통합 정보를 나타내는 응답 데이터 구조입니다.
 * Few-Shot을 다른 시스템이나 서비스에서 사용할 수 있도록 제공되는 API 엔드포인트와 통합 방법에 대한 정보를 포함합니다.</p>
 * 
 * <h3>Internal API 정보 포함 항목:</h3>
 * <ul>
 *   <li><strong>엔드포인트 정보</strong>: API URL, 메서드, 파라미터</li>
 *   <li><strong>인증 정보</strong>: API 키, 토큰 정보</li>
 *   <li><strong>사용 예시</strong>: 호출 방법 및 응답 형식</li>
 *   <li><strong>제한사항</strong>: 사용량 제한, 권한 정보</li>
 * </ul>
 * 
 * <h3>Internal API 사용 시나리오:</h3>
 * <ul>
 *   <li><strong>서비스 통합</strong>: 외부 서비스에서 Few-Shot 사용</li>
 *   <li><strong>마이크로서비스</strong>: 내부 시스템 간 Few-Shot 공유</li>
 *   <li><strong>배치 처리</strong>: 대량 데이터 처리 시 Few-Shot 적용</li>
 *   <li><strong>실시간 서비스</strong>: 실시간 추론 서비스 구축</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotInternalApiResponse api = fewShotClient.getInternalApi(fewShotUuid);
 * String endpoint = api.getApiEndpoint();
 * String apiKey = api.getApiKey();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot Internal API 통합 정보 응답",
    example = """
        {
          "few_shot_uuid": "fs-456e7890-e12b-34d5-a678-426614174111",
          "api_endpoint": "https://api.sktai.com/v1/few-shots/fs-456e7890-e12b-34d5-a678-426614174111/inference",
          "api_method": "POST",
          "api_key": "sk-abcd1234567890",
          "content_type": "application/json",
          "rate_limit": "1000/hour",
          "example_request": {
            "input": "고객 문의 텍스트",
            "parameters": {
              "temperature": 0.7
            }
          },
          "example_response": {
            "output": "응답 텍스트",
            "confidence": 0.95
          },
          "status": "active",
          "created_at": "2025-08-22T10:30:00Z"
        }
        """
)
public class FewShotInternalApiResponse {
    
    /**
     * Few-Shot 고유 식별자
     * 
     * <p>Internal API가 연결된 Few-Shot의 UUID입니다.</p>
     */
    @JsonProperty("few_shot_uuid")
    @Schema(
        description = "Few-Shot UUID", 
        example = "fs-456e7890-e12b-34d5-a678-426614174111",
        format = "uuid"
    )
    private String fewShotUuid;
    
    /**
     * API 엔드포인트 URL
     * 
     * <p>Few-Shot을 호출할 수 있는 Internal API의 전체 URL입니다.</p>
     */
    @JsonProperty("api_endpoint")
    @Schema(
        description = "Internal API 엔드포인트 URL", 
        example = "https://api.sktai.com/v1/few-shots/fs-456e7890-e12b-34d5-a678-426614174111/inference",
        format = "uri"
    )
    private String apiEndpoint;
    
    /**
     * HTTP 메서드
     * 
     * <p>API 호출 시 사용할 HTTP 메서드입니다.</p>
     */
    @JsonProperty("api_method")
    @Schema(
        description = "API 호출 HTTP 메서드", 
        example = "POST",
        allowableValues = {"GET", "POST", "PUT", "PATCH"}
    )
    private String apiMethod;
    
    /**
     * API 인증 키
     * 
     * <p>Internal API 호출 시 사용할 인증 키입니다.
     * Authorization 헤더에 Bearer 토큰으로 사용됩니다.</p>
     */
    @JsonProperty("api_key")
    @Schema(
        description = "API 인증 키 (Bearer 토큰)", 
        example = "sk-abcd1234567890"
    )
    private String apiKey;
    
    /**
     * Content-Type
     * 
     * <p>API 요청 시 사용할 Content-Type 헤더 값입니다.</p>
     */
    @JsonProperty("content_type")
    @Schema(
        description = "요청 Content-Type", 
        example = "application/json"
    )
    private String contentType;
    
    /**
     * 사용량 제한
     * 
     * <p>API 호출 제한 정보입니다. 시간당 또는 일당 호출 제한을 나타냅니다.</p>
     */
    @JsonProperty("rate_limit")
    @Schema(
        description = "API 사용량 제한 (예: 1000/hour, 10000/day)", 
        example = "1000/hour"
    )
    private String rateLimit;
    
    /**
     * 요청 예시
     * 
     * <p>API 호출 시 사용할 요청 본문의 예시입니다.
     * JSON 형태의 샘플 요청 데이터를 제공합니다.</p>
     */
    @JsonProperty("example_request")
    @Schema(
        description = "API 요청 예시 (JSON 형태)", 
        example = """
            {
              "input": "고객 문의 텍스트",
              "parameters": {
                "temperature": 0.7,
                "max_tokens": 500
              }
            }
            """
    )
    private Object exampleRequest;
    
    /**
     * 응답 예시
     * 
     * <p>API 호출 시 반환되는 응답의 예시입니다.
     * JSON 형태의 샘플 응답 데이터를 제공합니다.</p>
     */
    @JsonProperty("example_response")
    @Schema(
        description = "API 응답 예시 (JSON 형태)", 
        example = """
            {
              "output": "응답 텍스트",
              "confidence": 0.95,
              "processing_time_ms": 1250
            }
            """
    )
    private Object exampleResponse;
    
    /**
     * API 상태
     * 
     * <p>Internal API의 현재 상태입니다.
     * active, inactive, deprecated 등의 값을 가질 수 있습니다.</p>
     */
    @JsonProperty("status")
    @Schema(
        description = "Internal API 상태", 
        example = "active",
        allowableValues = {"active", "inactive", "deprecated", "maintenance"}
    )
    private String status;
    
    /**
     * 버전 정보
     * 
     * <p>Internal API의 버전 정보입니다.</p>
     */
    @JsonProperty("version")
    @Schema(
        description = "API 버전", 
        example = "v1.0"
    )
    private String version;
    
    /**
     * 문서 URL
     * 
     * <p>Internal API 사용법에 대한 상세 문서 URL입니다.</p>
     */
    @JsonProperty("documentation_url")
    @Schema(
        description = "API 문서 URL", 
        example = "https://docs.sktai.com/internal-api/few-shots",
        format = "uri"
    )
    private String documentationUrl;
    
    /**
     * API 생성 일시
     * 
     * <p>Internal API가 생성된 날짜와 시간입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "API 생성 일시 (ISO 8601)", 
        example = "2025-08-22T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 만료 일시
     * 
     * <p>API 키나 액세스 권한의 만료 일시입니다.
     * null인 경우 무제한입니다.</p>
     */
    @JsonProperty("expires_at")
    @Schema(
        description = "API 만료 일시 (ISO 8601)", 
        example = "2026-08-22T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime expiresAt;
}
