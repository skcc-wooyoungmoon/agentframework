package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 사용 이력 개별 레코드 DTO
 *
 * <p>
 * SKTAI History API에서 반환되는 개별 모델 사용 이력 레코드의 구조입니다.
 * 모델 실행 세부 정보, 성능 지표, 사용자 정보를 포함합니다.
 * </p>
 *
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>요청 정보</strong>: 요청 시간, 응답 시간, 경과 시간</li>
 * <li><strong>모델 정보</strong>: 모델 ID, 이름, 타입, 서빙 정보</li>
 * <li><strong>성능 정보</strong>: 토큰 사용량, 응답 시간</li>
 * <li><strong>상태 정보</strong>: 성공/실패, 오류 메시지</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-09-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 개별 레코드", example = """
        {
          "request_time": "2025-09-30T10:00:16.791681+09:00",
          "response_time": "2025-09-30T10:00:23.337407+09:00",
          "elapsed_time": 6.545726,
          "endpoint": "https://api.openai.com/v1",
          "model_name": "gpt-4o-mini",
          "model_id": "d6b092d7-7f4b-4052-aef5-5b135e84660f",
          "model_type": "language",
          "model_serving_id": "560c81b3-cec5-4e4b-8c53-4c46d05c919b",
          "user": "admin",
          "completion_tokens": 52,
          "prompt_tokens": 60,
          "total_tokens": 112
        }
        """)
public class ModelHistoryRecord {

    /**
     * 요청 시간
     */
    @JsonProperty("request_time")
    @Schema(description = "요청 시간 (ISO 8601 형식)", example = "2025-09-30T10:00:16.791681+09:00")
    private String requestTime;

    /**
     * 응답 시간
     */
    @JsonProperty("response_time")
    @Schema(description = "응답 시간 (ISO 8601 형식)", example = "2025-09-30T10:00:23.337407+09:00")
    private String responseTime;

    /**
     * 경과 시간 (초)
     */
    @JsonProperty("elapsed_time")
    @Schema(description = "경과 시간 (초)", example = "6.545726")
    private Double elapsedTime;

    /**
     * 엔드포인트 URL
     */
    @JsonProperty("endpoint")
    @Schema(description = "엔드포인트 URL", example = "https://api.openai.com/v1")
    private String endpoint;

    /**
     * 모델 이름
     */
    @JsonProperty("model_name")
    @Schema(description = "모델 이름", example = "gpt-4o-mini")
    private String modelName;

    /**
     * 모델 식별자
     */
    @JsonProperty("model_identifier")
    @Schema(description = "모델 식별자", example = "gpt-4o-mini")
    private String modelIdentifier;

    /**
     * 모델 ID
     */
    @JsonProperty("model_id")
    @Schema(description = "모델 ID", example = "d6b092d7-7f4b-4052-aef5-5b135e84660f")
    private String modelId;

    /**
     * 모델 타입
     */
    @JsonProperty("model_type")
    @Schema(description = "모델 타입", example = "language")
    private String modelType;

    /**
     * 모델 서빙 ID
     */
    @JsonProperty("model_serving_id")
    @Schema(description = "모델 서빙 ID", example = "560c81b3-cec5-4e4b-8c53-4c46d05c919b")
    private String modelServingId;

    /**
     * 모델 서빙 이름
     */
    @JsonProperty("model_serving_name")
    @Schema(description = "모델 서빙 이름", example = "gpt-4o-mini")
    private String modelServingName;

    /**
     * 객체 타입
     */
    @JsonProperty("object_type")
    @Schema(description = "객체 타입", example = "chat")
    private String objectType;

    /**
     * API 키
     */
    @JsonProperty("api_key")
    @Schema(description = "API 키", example = "sk-287798228cf7e19d9a549f52ef9bd943")
    private String apiKey;

    /**
     * 모델 키
     */
    @JsonProperty("model_key")
    @Schema(description = "모델 키", example = "sk-proj-...")
    private String modelKey;

    /**
     * 입력 JSON
     */
    @JsonProperty("input_json")
    @Schema(description = "입력 JSON", example = "{\"messages\":[...],\"model\":\"gpt-4o-mini\"}")
    private String inputJson;

    /**
     * 출력 JSON
     */
    @JsonProperty("output_json")
    @Schema(description = "출력 JSON", example = "data: {...}")
    private String outputJson;

    /**
     * 완성 토큰 수
     */
    @JsonProperty("completion_tokens")
    @Schema(description = "완성 토큰 수", example = "52")
    private Integer completionTokens;

    /**
     * 프롬프트 토큰 수
     */
    @JsonProperty("prompt_tokens")
    @Schema(description = "프롬프트 토큰 수", example = "60")
    private Integer promptTokens;

    /**
     * 총 토큰 수
     */
    @JsonProperty("total_tokens")
    @Schema(description = "총 토큰 수", example = "112")
    private Integer totalTokens;

    /**
     * 프로젝트 ID
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
    private String projectId;

    /**
     * 사용자 정보
     */
    @JsonProperty("user")
    @Schema(description = "사용자 식별자", example = "admin")
    private String user;

    /**
     * 트랜잭션 ID
     */
    @JsonProperty("transaction_id")
    @Schema(description = "트랜잭션 ID", example = "c6ea62d4-8c07-4012-ba2d-8cd55a25e09d")
    private String transactionId;

    /**
     * 앱 ID
     */
    @JsonProperty("app_id")
    @Schema(description = "앱 ID", example = "aip-agent-playground")
    private String appId;

    /**
     * 에이전트 앱 서빙 ID
     */
    @JsonProperty("agent_app_serving_id")
    @Schema(description = "에이전트 앱 서빙 ID", example = "")
    private String agentAppServingId;

    /**
     * 회사 정보
     */
    @JsonProperty("company")
    @Schema(description = "회사 정보", example = "")
    private String company;

    /**
     * 부서 정보
     */
    @JsonProperty("department")
    @Schema(description = "부서 정보", example = "")
    private String department;

    /**
     * 채팅 ID
     */
    @JsonProperty("chat_id")
    @Schema(description = "채팅 ID", example = "")
    private String chatId;

    /**
     * error message
     */
    @JsonProperty("error_message")
    @Schema(description = "error message", example = "500: Request timed out.")
    private String errorMessage;

    /**
     * error code
     */
    @JsonProperty("error_code")
    @Schema(description = "error code", example = "500")
    private Integer errorCode;
}