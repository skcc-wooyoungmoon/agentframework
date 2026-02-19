package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Doc Intelligence 이력 레코드 DTO
 * 
 * <p>SKTAI History API의 Doc Intelligence 관련 이력 데이터 개별 레코드 구조입니다.
 * 문서 지능형 분석 서비스의 사용 이력과 성능 정보를 포함합니다.</p>
 * 
 * <h3>주요 정보:</h3>
 * <ul>
 *   <li><strong>요청 정보</strong>: 요청 ID, 사용자, 프로젝트</li>
 *   <li><strong>도구 정보</strong>: 사용된 도구와 버전</li>
 *   <li><strong>처리 정보</strong>: 처리 시간, 상태, 결과</li>
 *   <li><strong>문서 정보</strong>: 처리된 문서 메타데이터</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Doc Intelligence 이력 레코드",
    example = """
        {
          "id": "doc-intel-123",
          "request_id": "req-456",
          "user": "user@example.com",
          "project_id": "project-789",
          "tool_id": "ocr-tool",
          "tool_version": "v2.1.0",
          "status": "completed",
          "processing_time": 45,
          "accuracy_score": 94.5,
          "processed_at": "2025-09-24T10:30:00Z"
        }
        """
)
public class DocIntelligenceHistoryRecord {
    
    /**
     * 레코드 식별자
     */
    @JsonProperty("id")
    @Schema(description = "레코드 고유 식별자", example = "doc-intel-123")
    private String id;
    
    /**
     * 요청 식별자
     */
    @JsonProperty("request_id")
    @Schema(description = "요청 고유 식별자", example = "req-456")
    private String requestId;
    
    /**
     * 세션 식별자
     */
    @JsonProperty("session_id")
    @Schema(description = "세션 식별자", example = "session-789")
    private String sessionId;
    
    /**
     * 사용자 정보
     */
    @JsonProperty("user")
    @Schema(description = "사용자 식별자", example = "user@example.com")
    private String user;
    
    /**
     * 프로젝트 식별자
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 식별자", example = "project-789")
    private String projectId;
    
    /**
     * 애플리케이션 식별자
     */
    @JsonProperty("app_id")
    @Schema(description = "애플리케이션 식별자", example = "doc-app-456")
    private String appId;
    
    /**
     * 도구 식별자
     */
    @JsonProperty("tool_id")
    @Schema(description = "사용된 도구 식별자", example = "ocr-tool")
    private String toolId;
    
    /**
     * 도구 버전
     */
    @JsonProperty("tool_version")
    @Schema(description = "사용된 도구 버전", example = "v2.1.0")
    private String toolVersion;
    
    /**
     * 도구 타입
     */
    @JsonProperty("tool_type")
    @Schema(description = "도구 타입", example = "ocr")
    private String toolType;
    
    /**
     * 처리 상태
     */
    @JsonProperty("status")
    @Schema(description = "처리 상태", example = "completed", allowableValues = {"pending", "processing", "completed", "failed", "cancelled"})
    private String status;
    
    /**
     * 처리 시작 시간
     */
    @JsonProperty("started_at")
    @Schema(description = "처리 시작 시간 (ISO 8601)", example = "2025-09-24T10:25:00Z")
    private String startedAt;
    
    /**
     * 처리 완료 시간
     */
    @JsonProperty("processed_at")
    @Schema(description = "처리 완료 시간 (ISO 8601)", example = "2025-09-24T10:30:00Z")
    private String processedAt;
    
    /**
     * 처리 시간 (초)
     */
    @JsonProperty("processing_time")
    @Schema(description = "처리 시간 (초)", example = "45")
    private Integer processingTime;
    
    /**
     * 문서 정보
     */
    @JsonProperty("document_id")
    @Schema(description = "처리된 문서 식별자", example = "doc-123")
    private String documentId;
    
    /**
     * 문서 이름
     */
    @JsonProperty("document_name")
    @Schema(description = "처리된 문서 이름", example = "contract.pdf")
    private String documentName;
    
    /**
     * 문서 타입
     */
    @JsonProperty("document_type")
    @Schema(description = "문서 타입", example = "pdf")
    private String documentType;
    
    /**
     * 문서 크기 (바이트)
     */
    @JsonProperty("document_size")
    @Schema(description = "문서 크기 (바이트)", example = "1048576")
    private Long documentSize;
    
    /**
     * 페이지 수
     */
    @JsonProperty("page_count")
    @Schema(description = "문서 페이지 수", example = "10")
    private Integer pageCount;
    
    /**
     * 성능 및 품질 정보
     */
    @JsonProperty("accuracy_score")
    @Schema(description = "정확도 점수 (0-100)", example = "94.5")
    private Double accuracyScore;
    
    /**
     * 신뢰도 점수
     */
    @JsonProperty("confidence_score")
    @Schema(description = "신뢰도 점수 (0-100)", example = "92.3")
    private Double confidenceScore;
    
    /**
     * 추출된 텍스트 길이
     */
    @JsonProperty("extracted_text_length")
    @Schema(description = "추출된 텍스트 길이", example = "5000")
    private Integer extractedTextLength;
    
    /**
     * 추출된 엔티티 수
     */
    @JsonProperty("extracted_entities_count")
    @Schema(description = "추출된 엔티티 수", example = "25")
    private Integer extractedEntitiesCount;
    
    /**
     * 비용 정보
     */
    @JsonProperty("cost")
    @Schema(description = "처리 비용", example = "0.05")
    private Double cost;
    
    /**
     * 비용 단위
     */
    @JsonProperty("cost_unit")
    @Schema(description = "비용 단위", example = "USD")
    private String costUnit;
    
    /**
     * 에러 정보
     */
    @JsonProperty("error_code")
    @Schema(description = "에러 코드 (실패 시)", example = "DOC_001")
    private String errorCode;
    
    /**
     * 에러 메시지
     */
    @JsonProperty("error_message")
    @Schema(description = "에러 메시지 (실패 시)", example = "Document format not supported")
    private String errorMessage;
    
    /**
     * 메타데이터
     */
    @JsonProperty("metadata")
    @Schema(description = "추가 메타데이터", example = "{\"language\": \"ko\", \"format\": \"A4\"}")
    private String metadata;
    
    /**
     * 회사 정보
     */
    @JsonProperty("company")
    @Schema(description = "회사 정보", example = "SKTelecom")
    private String company;
    
    /**
     * 부서 정보
     */
    @JsonProperty("department")
    @Schema(description = "부서 정보", example = "AI Platform")
    private String department;
    
    /**
     * API 키
     */
    @JsonProperty("api_key")
    @Schema(description = "사용된 API 키", example = "ak-123456")
    private String apiKey;
    
    /**
     * 생성 시간
     */
    @JsonProperty("created_at")
    @Schema(description = "레코드 생성 시간 (ISO 8601)", example = "2025-09-24T10:20:00Z")
    private String createdAt;
    
    /**
     * 수정 시간
     */
    @JsonProperty("updated_at")
    @Schema(description = "레코드 수정 시간 (ISO 8601)", example = "2025-09-24T10:30:00Z")
    private String updatedAt;
}