package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 메시지 목록 응답 DTO
 * 
 * <p>특정 버전의 프롬프트 메시지 목록을 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Inference Prompt 메시지 목록 응답",
        example = """
                {
                    "timestamp": 1756226572288,
                    "code": 1,
                    "detail": "성공",
                    "traceId": null,
                    "data": [
                        {
                            "uuid": "b7776afe-c266-44b1-8c42-6a15a0d4e988",
                            "version_id": "7865e1e3-42b2-448b-93b8-ff7515ff6f3e",
                            "mtype": 1,
                            "message": "Goal\\nProvide accurate, concise, and clear answers in Korean using the Hyde process.\\n\\nHyde Process\\n1. Hypothesize: First, internally generate an ideal, detailed hypothetical answer that fully addresses the user's intent.\\n\\n2. Formulate Search Query: Use the entire hypothetical answer as a single search query. This method finds the most contextually relevant documents.\\n\\n3. Synthesize: Use the search results to verify, correct, and refine the initial hypothesis into a final answer, following the guidelines below.\\n\\nFinal Answer Guidelines\\nAccurate: Based on reliable information.\\nConcise: Summarize key points, omit filler.\\nClear: Use simple language.\\nToken Limit: Max 500 tokens.\\n\\nOutput Format\\n{\\"queries\\": [\\"The complete hypothetical answer written out as a single string to be used as the search query.\\"]}\\n\\n※ If no search is needed (e.g., for conversational input like \\"lol\\", \\"thanks\\"):\\n{\\"queries\\": []}\\n",
                            "sequence": 187
                        },
                        {
                            "uuid": "b7776afe-c266-44b1-8c42-6a15a0d4e988",
                            "version_id": "7865e1e3-42b2-448b-93b8-ff7515ff6f3e",
                            "mtype": 2,
                            "message": "<question>\\n{{query}}\\n</question>\\n",
                            "sequence": 188
                        }
                    ],
                    "payload": null
                }
        """)
public class PromptMessagesResponse {

    @Schema(description = "응답 시각(UTC epoch millis)")
    private Long timestamp;

    @Schema(description = "응답 코드(예: 1=성공)")
    private Integer code;

    @Schema(description = "상세 메시지")
    private String detail;

    @Schema(description = "트레이스 ID")
    private String traceId;

    @Schema(description = "메시지 데이터 목록")
    private List<PromptMessage> data;

    @Schema(description = "추가 페이로드")
    private JsonNode payload;
    
    /**
     * 프롬프트 메시지 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 메시지 정보")
    public static class PromptMessage {
        @Schema(description = "프롬프트 UUID")
        private String uuid;

        @JsonProperty("version_id")
        @Schema(description = "버전 UUID")
        private String versionId;

        @Schema(description = "메시지 타입 (예: 1:system, 2:user)")
        private Integer mtype;

        @Schema(description = "메시지 내용")
        private String message;

        @Schema(description = "메시지 순서(서버 sequence)")
        private Integer sequence;
    }
}
