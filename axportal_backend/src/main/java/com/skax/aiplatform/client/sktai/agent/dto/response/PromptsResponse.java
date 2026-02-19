package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompts 목록 응답 DTO
 *
 * <p>SKTAI Agent 시스템에서 Inference Prompt 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 페이징된 프롬프트 목록과 메타데이터를 포함합니다.</p>
 *
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>프롬프트 목록</strong>: 페이징된 프롬프트 기본 정보</li>
 *   <li><strong>페이징 정보</strong>: 전체 개수, 페이지 정보</li>
 *   <li><strong>메타데이터</strong>: 응답 시간, 상태 정보</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-08-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI Agent Inference Prompts 목록 응답",
        example = """
                {
                  "data": [
                    {
                      "uuid": "prompt-uuid-123",
                      "name": "Customer Support Assistant",
                      "desc": "고객 문의 응답용 AI 어시스턴트",
                      "created_at": "2025-08-15T10:30:00Z",
                      "updated_at": "2025-08-15T11:45:00Z",
                      "version": "1.2.0",
                      "tags": ["customer-support", "chatbot"]
                    }
                  ],
                  "total": 25,
                  "page": 1,
                  "size": 10
                }
                """
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromptsResponse {

    /**
     * 프롬프트 목록 데이터
     */
    @JsonProperty("data")
    @Schema(description = "프롬프트 목록")
    private List<PromptSummary> data;

    /**
     * 전체 프롬프트 개수
     */
    @JsonProperty("total")
    @Schema(description = "전체 프롬프트 개수", example = "25")
    private Integer total;

    /**
     * 현재 페이지 번호
     */
    @JsonProperty("page")
    @Schema(description = "현재 페이지 번호", example = "1")
    private Integer page;

    /**
     * 페이지 크기
     */
    @JsonProperty("size")
    @Schema(description = "페이지 크기", example = "10")
    private Integer size;

    @JsonProperty("payload")
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {

        @JsonProperty("pagination")
        private Pagination pagination;

    }

    /**
     * 프롬프트 요약 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 요약 정보")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromptSummary {

        @JsonProperty("uuid")
        @Schema(description = "프롬프트 UUID", example = "prompt-uuid-123")
        private String uuid;

        @JsonProperty("name")
        @Schema(description = "프롬프트 이름", example = "Customer Support Assistant")
        private String name;

        @JsonProperty("desc")
        @Schema(description = "프롬프트 설명", example = "고객 문의 응답용 AI 어시스턴트")
        private String description;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간", example = "2025-08-15T10:30:00Z")
        private String createdAt;

        @JsonProperty("updated_at")
        @Schema(description = "수정 시간", example = "2025-08-15T11:45:00Z")
        private String updatedAt;

//        @JsonProperty("version")
//        @Schema(description = "현재 버전", example = "1.2.0")
//        private String version;

        @JsonProperty("release_version")
        @Schema(description = "릴리즈 버전", example = "1")
        private String releaseVersion;

        @JsonProperty("latest_version")
        @Schema(description = "현재 버전", example = "1")
        private String latestVersion;

        @JsonProperty("ptype")
        @Schema(description = "ptype", example = "1.2.0")
        private String ptype;

        @JsonProperty("tags")
        @Schema(description = "태그 목록", example = "[\"customer-support\", \"chatbot\"]")
        private List<JsonNode> tags;

    }

}
