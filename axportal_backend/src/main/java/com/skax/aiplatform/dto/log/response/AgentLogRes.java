package com.skax.aiplatform.dto.log.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent Log 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agent Log 응답")
public class AgentLogRes {

    @Schema(description = "요청 시간")
    private String requestTime;

    @Schema(description = "응답 시간")
    private String responseTime;

    @Schema(description = "처리 시간 (초)")
    private Double elapsedTime;

    @Schema(description = "함수 타입 (invoke, stream)")
    private String funcType;

    @Schema(description = "서빙 타입 (shared, dedicated)")
    private String servingType;

    @Schema(description = "그래프 설정 경로")
    private String graphConfigPath;

    @Schema(description = "엔드포인트 URL")
    private String endpoint;

    @Schema(description = "API 키")
    private String apiKey;

    @Schema(description = "앱 ID")
    private String appId;

    @Schema(description = "Agent 앱 버전")
    private String agentAppVersion;

    @Schema(description = "Agent 앱 ID")
    private String agentAppId;

    @Schema(description = "Agent 앱 이름")
    private String agentAppName;

    @Schema(description = "Agent 앱 서빙 ID")
    private String agentAppServingId;

    @Schema(description = "Agent 앱 서빙 이름")
    private String agentAppServingName;

    @Schema(description = "회사")
    private String company;

    @Schema(description = "부서")
    private String department;

    @Schema(description = "사용자")
    private String user;

    @Schema(description = "채팅 ID")
    private String chatId;

    @Schema(description = "트랜잭션 ID")
    private String transactionId;

    @Schema(description = "완료 토큰 수")
    private Integer completionTokens;

    @Schema(description = "프롬프트 토큰 수")
    private Integer promptTokens;

    @Schema(description = "전체 토큰 수")
    private Integer totalTokens;

    @Schema(description = "프로젝트 ID")
    private String projectId;

    @Schema(description = "입력 JSON")
    private String inputJson;

    @Schema(description = "출력 JSON")
    private String outputJson;

    @Schema(description = "에러 코드")
    private String errorCode;

    @Schema(description = "에러 메시지")
    private String errorMessage;
}
