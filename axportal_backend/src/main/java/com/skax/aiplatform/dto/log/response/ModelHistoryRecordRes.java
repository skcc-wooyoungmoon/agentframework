package com.skax.aiplatform.dto.log.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 사용 이력 개별 레코드 응답 DTO (카멜케이스)
 *
 * <p>
 * 개별 모델 사용 이력 레코드의 구조입니다.
 * 모델 실행 세부 정보, 성능 지표, 사용자 정보를 포함합니다.
 * </p>
 *
 * @author System
 * @version 1.0.0
 * @since 2025-01-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 개별 레코드", example = """
        {
          "requestId": "req-123-456-789",
          "modelId": "gpt-4",
          "user": "user@example.com",
          "requestTime": "2025-09-24T10:30:00Z",
          "responseTime": 1200,
          "tokenCount": 150,
          "status": "success"
        }
        """)
public class ModelHistoryRecordRes {

    /**
     * 요청 고유 식별자
     */
    @Schema(description = "요청 고유 식별자", example = "req-123-456-789")
    private String requestId;

    /**
     * 모델 식별자
     */
    @Schema(description = "모델 식별자", example = "gpt-4")
    private String modelId;

    /**
     * 모델 타입
     */
    @Schema(description = "모델 타입", example = "chat")
    private String modelType;

    /**
     * 프로젝트 식별자
     */
    @Schema(description = "프로젝트 식별자", example = "project-123")
    private String projectId;

    /**
     * 애플리케이션 식별자
     */
    @Schema(description = "애플리케이션 식별자", example = "app-456")
    private String appId;

    /**
     * 사용자 정보
     */
    @Schema(description = "사용자 식별자", example = "user@example.com")
    private String user;

    /**
     * 요청 시간
     */
    @Schema(description = "요청 시간 (ISO 8601 형식)", example = "2025-09-24T10:30:00Z")
    private String requestTime;

    /**
     * 응답 시간 (ISO 8601 형식)
     */
    @Schema(description = "응답 시간 (ISO 8601 형식)", example = "2025-09-30T13:03:18.222511+09:00")
    private String responseTime;

    /**
     * 토큰 사용량
     */
    @Schema(description = "토큰 사용량", example = "150")
    private Integer tokenCount;

    /**
     * 요청 상태
     */
    @Schema(description = "요청 상태", example = "success", allowableValues = {"success", "failed", "timeout"})
    private String status;

    /**
     * 회사 정보
     */
    @Schema(description = "회사 정보", example = "SKTelecom")
    private String company;

    /**
     * 부서 정보
     */
    @Schema(description = "부서 정보", example = "AI Platform")
    private String department;

    /**
     * API 키
     */
    @Schema(description = "API 키", example = "ak-123456")
    private String apiKey;

    /**
     * 오류 메시지 (실패 시)
     */
    @Schema(description = "오류 메시지 (실패 시)", example = "500: Request timed out.")
    private String errorMessage;

    /**
     * 오류 코드 (실패 시)
     */
    @Schema(description = "error code", example = "500")
    private Integer errorCode;

    /**
     * 경과 시간 (초)
     */
    @Schema(description = "경과 시간 (초)", example = "6.545726")
    private Double elapsedTime;

    /**
     * 엔드포인트 URL
     */
    @Schema(description = "엔드포인트 URL", example = "https://api.openai.com/v1")
    private String endpoint;

    /**
     * 모델 이름
     */
    @Schema(description = "모델 이름", example = "gpt-4o-mini")
    private String modelName;

    /**
     * 모델 식별자
     */
    @Schema(description = "모델 식별자", example = "gpt-4o-mini")
    private String modelIdentifier;

    /**
     * 모델 서빙 ID
     */
    @Schema(description = "모델 서빙 ID", example = "560c81b3-cec5-4e4b-8c53-4c46d05c919b")
    private String modelServingId;

    /**
     * 모델 서빙 이름
     */
    @Schema(description = "모델 서빙 이름", example = "gpt-4o-mini")
    private String modelServingName;

    /**
     * 객체 타입
     */
    @Schema(description = "객체 타입", example = "chat")
    private String objectType;

    /**
     * 모델 키
     */
    @Schema(description = "모델 키", example = "sk-proj-...")
    private String modelKey;

    /**
     * 입력 JSON
     */
    @Schema(description = "입력 JSON", example = "{\"messages\":[...],\"model\":\"gpt-4o-mini\"}")
    private String inputJson;

    /**
     * 출력 JSON
     */
    @Schema(description = "출력 JSON", example = "data: {...}")
    private String outputJson;

    /**
     * 완성 토큰 수
     */
    @Schema(description = "완성 토큰 수", example = "52")
    private Integer completionTokens;

    /**
     * 프롬프트 토큰 수
     */
    @Schema(description = "프롬프트 토큰 수", example = "60")
    private Integer promptTokens;

    /**
     * 총 토큰 수
     */
    @Schema(description = "총 토큰 수", example = "112")
    private Integer totalTokens;

    /**
     * 트랜잭션 ID
     */
    @Schema(description = "트랜잭션 ID", example = "c6ea62d4-8c07-4012-ba2d-8cd55a25e09d")
    private String transactionId;

    /**
     * 에이전트 앱 서빙 ID
     */
    @Schema(description = "에이전트 앱 서빙 ID", example = "")
    private String agentAppServingId;

    /**
     * 채팅 ID
     */
    @Schema(description = "채팅 ID", example = "")
    private String chatId;
}
