package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * SKTAI Agent Inference Prompt 태그 필터링 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "SKTAI Agent Inference Prompt 태그 필터링 응답",
        example = """
                {
                  "timestamp": 1756251147997,
                  "code": 1,
                  "detail": "성공",
                  "traceId": null,
                  "data": [
                    {
                      "name": "test238",
                      "ptype": 1,
                      "uuid": "d73cae65-2e18-4750-8639-0efe4dbb5827",
                      "delete_flag": false,
                      "created_at": "2025-08-25T06:02:10.437016Z",
                      "release_version": 1,
                      "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
                      "tags": "dev,prd"
                    }
                  ],
                  "payload": null
                }
                """)
public class PromptFilterByTagsResponse {

    @Schema(description = "응답 생성 시각 (epoch millis)")
    private Long timestamp;

    @Schema(description = "응답 코드 (1=성공 등)")
    private Integer code;

    @Schema(description = "응답 상세 메시지")
    private String detail;

    @Schema(description = "추적 ID")
    private String traceId;

    @Schema(description = "태그로 필터링된 프롬프트 목록")
    private List<Item> data;

    @Schema(description = "추가 페이로드(없으면 null)")
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "프롬프트 간략 정보")
    public static class Item {

        @Schema(description = "프롬프트 이름")
        private String name;

        @Schema(description = "프롬프트 타입(예: 1=채팅)")
        private Integer ptype;

        @Schema(description = "프롬프트 UUID")
        private String uuid;

        @JsonProperty("delete_flag")
        @Schema(description = "삭제 여부")
        private Boolean deleteFlag;

        @JsonProperty("created_at")
        @Schema(description = "생성 일시 (ISO8601)")
        private LocalDateTime createdAt;

        @JsonProperty("release_version")
        @Schema(description = "릴리즈 버전")
        private Integer releaseVersion;

        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @Schema(description = "쉼표 구분 태그 문자열 (예: \"dev,prd\")")
        private String tags;

        /**
         * 편의 메서드: 태그 리스트로 변환
         */
        @Schema(hidden = true)
        public List<String> getTagList() {
            if (tags == null || tags.isBlank()) {
                return Collections.emptyList();
            }

            return Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

    }

}
