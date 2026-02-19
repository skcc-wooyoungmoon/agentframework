package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Model Endpoint 생성 요청 DTO
 * 
 * <p>SKTAI Model의 새로운 엔드포인트를 생성할 때 사용하는 요청 데이터 구조입니다.
 * 모델 서빙을 위한 API 엔드포인트를 관리합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>url</strong>: 엔드포인트 URL</li>
 *   <li><strong>identifier</strong>: 엔드포인트 식별자</li>
 *   <li><strong>key</strong>: 인증 키</li>
 * </ul>
 * 
 * <h3>선택 정보:</h3>
 * <ul>
 *   <li><strong>description</strong>: 엔드포인트 설명</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelEndpointCreate request = ModelEndpointCreate.builder()
 *     .url("https://api.example.com/v1/models/completion")
 *     .identifier("completion-api")
 *     .key("sk-1234567890abcdef")
 *     .description("텍스트 완성 API 엔드포인트")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Endpoint 생성 요청 정보",
    example = """
        {
          "url": "https://api.example.com/v1/models/completion",
          "identifier": "completion-api",
          "key": "sk-1234567890abcdef",
          "description": "텍스트 완성 API 엔드포인트"
        }
        """
)
public class ModelEndpointCreate {
    
    /**
     * 엔드포인트 URL
     * 
     * <p>모델 서빙을 위한 API 엔드포인트의 전체 URL입니다.
     * HTTPS 프로토콜을 사용하는 것을 권장합니다.</p>
     * 
     * @apiNote 유효한 URL 형식이어야 하며, 접근 가능해야 합니다.
     */
    @JsonProperty("url")
    @Schema(
        description = "엔드포인트 URL (HTTPS 권장)", 
        example = "https://api.example.com/v1/models/completion",
        required = true,
        maxLength = 255,
        format = "uri"
    )
    private String url;
    
    /**
     * 엔드포인트 식별자
     * 
     * <p>엔드포인트를 구분하는 고유한 식별자입니다.
     * 시스템 내에서 엔드포인트를 참조할 때 사용됩니다.</p>
     * 
     * @implNote 의미있고 구분 가능한 식별자를 사용하는 것이 좋습니다.
     */
    @JsonProperty("identifier")
    @Schema(
        description = "엔드포인트 고유 식별자", 
        example = "completion-api",
        required = true,
        maxLength = 255
    )
    private String identifier;
    
    /**
     * 인증 키
     * 
     * <p>엔드포인트 접근을 위한 인증 키입니다.
     * API 키, 토큰 등의 인증 정보를 포함합니다.</p>
     * 
     * @apiNote 보안을 위해 안전하게 관리되어야 하는 중요한 정보입니다.
     */
    @JsonProperty("key")
    @Schema(
        description = "엔드포인트 인증 키", 
        example = "sk-1234567890abcdef",
        required = true,
        maxLength = 255
    )
    private String key;
    
    /**
     * 엔드포인트 설명
     * 
     * <p>엔드포인트의 용도와 기능을 설명하는 텍스트입니다.
     * 관리와 문서화를 위해 유용한 정보입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "엔드포인트 설명 (용도와 기능)", 
        example = "텍스트 완성 API 엔드포인트",
        maxLength = 1000
    )
    private String description;
}
