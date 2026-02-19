package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Model Endpoint 응답 DTO
 * 
 * <p>SKTAI Model Endpoint의 상세 정보를 담는 응답 데이터 구조입니다.
 * 모델 서빙을 위한 API 엔드포인트 정보를 관리합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, URL, 식별자</li>
 *   <li><strong>인증 정보</strong>: 인증 키</li>
 *   <li><strong>메타데이터</strong>: 설명</li>
 *   <li><strong>시간 정보</strong>: 생성/수정 시간</li>
 * </ul>
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
    description = "SKTAI Model Endpoint 응답 정보",
    example = """
        {
          "id": "ep-123e4567-e89b-12d3-a456-426614174000",
          "url": "https://api.example.com/v1/models/completion",
          "identifier": "completion-api",
          "key": "sk-1234567890abcdef",
          "description": "텍스트 완성 API 엔드포인트",
          "created_at": "2025-09-01T10:30:00",
          "updated_at": "2025-09-01T10:30:00"
        }
        """
)
public class ModelEndpointRead {
    
    /**
     * 엔드포인트 ID
     * 
     * <p>모델 엔드포인트의 고유 식별자입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "엔드포인트 고유 식별자", 
        example = "ep-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 엔드포인트 URL
     * 
     * <p>모델 서빙을 위한 API 엔드포인트의 전체 URL입니다.</p>
     */
    @JsonProperty("url")
    @Schema(
        description = "엔드포인트 URL", 
        example = "https://api.example.com/v1/models/completion",
        maxLength = 255,
        format = "uri"
    )
    private String url;
    
    /**
     * 엔드포인트 식별자
     * 
     * <p>엔드포인트를 구분하는 고유한 식별자입니다.</p>
     */
    @JsonProperty("identifier")
    @Schema(
        description = "엔드포인트 식별자", 
        example = "completion-api",
        maxLength = 255
    )
    private String identifier;
    
    /**
     * 인증 키
     * 
     * <p>엔드포인트 접근을 위한 인증 키입니다.</p>
     */
    @JsonProperty("key")
    @Schema(
        description = "엔드포인트 인증 키", 
        example = "sk-1234567890abcdef",
        maxLength = 255
    )
    private String key;
    
    /**
     * 엔드포인트 설명
     * 
     * <p>엔드포인트의 용도와 기능을 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "엔드포인트 설명", 
        example = "텍스트 완성 API 엔드포인트"
    )
    private String description;
    
    /**
     * 생성 시간
     * 
     * <p>엔드포인트가 생성된 시간입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "생성 시간", 
        example = "2025-09-01T10:30:00"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     * 
     * <p>엔드포인트가 마지막으로 수정된 시간입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "수정 시간", 
        example = "2025-09-01T10:30:00"
    )
    private LocalDateTime updatedAt;
}
