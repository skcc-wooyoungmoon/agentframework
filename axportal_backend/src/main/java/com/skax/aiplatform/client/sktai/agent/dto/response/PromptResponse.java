package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Agent Inference Prompt 상세 정보 응답 DTO
 *
 * <p>특정 Inference Prompt의 상세 정보를 담는 응답 데이터 구조입니다.
 * 프롬프트의 모든 정보와 현재 활성 버전의 내용을 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-08-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Schema(
        description = "SKTAI Agent Inference Prompt 상세 정보 응답",
        example = """
                        {
                          "timestamp": 1756172853539,
                          "code": 1,
                          "detail": "성공",
                          "traceId": null,
                          "data": {
                            "uuid": "f397ee44-780a-4a20-b3a1-86b89f4d45e3",
                            "name": "test",
                            "desc": "테스트 프롬프트 설명",
                            "ptype": 1,
                            "delete_flag": false,
                            "created_at": "2025-08-26T04:46:14.648658Z",
                            "release_version": 1,
                            "project_id": "11026e85-edfa-4789-afb9-83f6eff7ce14",
                            "tags": "fff"
                          },
                          "payload": null
                        }
                """
)
public class PromptResponse {

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("code")
    private String code;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("traceId")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "프롬프트 데이터")
    private PromptData data;

    @JsonProperty("payload")
    private Object payload;

    /**
     * 프롬프트 데이터 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 요약 정보")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromptData {

        @JsonProperty("uuid")
        @Schema(description = "프롬프트 UUID", example = "prompt-uuid-123")
        private String uuid;

        @JsonProperty("name")
        @Schema(description = "프롬프트 이름", example = "Customer Support Assistant")
        private String name;

        @JsonProperty("desc")
        @Schema(description = "프롬프트 설명", example = "고객 문의 응답용 AI 어시스턴트")
        private String description;

        @JsonProperty("ptype")
        @Schema(description = "프롬프트 유형", example = "1")
        private String ptype;

        @JsonProperty("delete_flag")
        @Schema(description = "삭제 여부", example = "false")
        private String deleteFlag;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;

        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;

        @JsonProperty("release_version")
        @Schema(description = "배포 버전", example = "1")
        private String releaseVersion;

        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @JsonProperty("tags")
        @Schema(description = "태그")
        @JsonFormat(with = com.fasterxml.jackson.annotation.JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<JsonNode> tags;

    }

}
