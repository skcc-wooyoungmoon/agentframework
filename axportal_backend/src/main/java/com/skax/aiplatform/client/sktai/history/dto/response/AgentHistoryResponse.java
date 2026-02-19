package com.skax.aiplatform.client.sktai.history.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent History 목록 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent History 목록 응답")
public class AgentHistoryResponse {

    @JsonProperty("data")
    @Schema(description = "Agent History 목록")
    private List<AgentHistoryRecord> data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    /**
     * AgentHistoryRecord 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "벡터 데이터베이스 정보")
    public static class AgentHistoryRecord {

        @JsonProperty("request_time")
        @Schema(description = "요청 시간")
        private String requestTime;

        @JsonProperty("response_time")
        @Schema(description = "응답 시간")
        private String responseTime;

        @JsonProperty("elapsed_time")
        @Schema(description = "처리 시간 (초)")
        private Double elapsedTime;

        @JsonProperty("func_type")
        @Schema(description = "함수 타입 (invoke, stream)")
        private String funcType;

        @JsonProperty("serving_type")
        @Schema(description = "서빙 타입 (shared, dedicated)")
        private String servingType;

        @JsonProperty("graph_config_path")
        @Schema(description = "그래프 설정 경로")
        private String graphConfigPath;

        @JsonProperty("endpoint")
        @Schema(description = "엔드포인트 URL")
        private String endpoint;

        @JsonProperty("api_key")
        @Schema(description = "API 키")
        private String apiKey;

        @JsonProperty("app_id")
        @Schema(description = "앱 ID")
        private String appId;

        @JsonProperty("agent_app_version")
        @Schema(description = "Agent 앱 버전")
        private String agentAppVersion;

        @JsonProperty("agent_app_id")
        @Schema(description = "Agent 앱 ID")
        private String agentAppId;

        @JsonProperty("agent_app_name")
        @Schema(description = "Agent 앱 이름")
        private String agentAppName;

        @JsonProperty("agent_app_serving_id")
        @Schema(description = "Agent 앱 서빙 ID")
        private String agentAppServingId;

        @JsonProperty("agent_app_serving_name")
        @Schema(description = "Agent 앱 서빙 이름")
        private String agentAppServingName;

        @JsonProperty("company")
        @Schema(description = "회사")
        private String company;

        @JsonProperty("department")
        @Schema(description = "부서")
        private String department;

        @JsonProperty("user")
        @Schema(description = "사용자")
        private String user;

        @JsonProperty("chat_id")
        @Schema(description = "채팅 ID")
        private String chatId;

        @JsonProperty("transaction_id")
        @Schema(description = "트랜잭션 ID")
        private String transactionId;

        @JsonProperty("completion_tokens")
        @Schema(description = "완료 토큰 수")
        private Integer completionTokens;

        @JsonProperty("prompt_tokens")
        @Schema(description = "프롬프트 토큰 수")
        private Integer promptTokens;

        @JsonProperty("total_tokens")
        @Schema(description = "전체 토큰 수")
        private Integer totalTokens;

        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @JsonProperty("input_json")
        @Schema(description = "입력 JSON")
        private String inputJson;

        @JsonProperty("output_json")
        @Schema(description = "출력 JSON")
        private String outputJson;

        @JsonProperty("error_code")
        @Schema(description = "에러 코드")
        private Integer errorCode;

        @JsonProperty("error_message")
        @Schema(description = "에러 메시지")
        private String errorMessage;
    }
}