package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 태그 목록 응답 DTO
 *
 * 실제 응답(envelope + data[]) 구조 반영
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(
        description = "SKTAI Agent Inference Prompt 태그 목록 응답",
        example = """
        {
          "timestamp": 1756235331601,
          "code": 1,
          "detail": "성공",
          "traceId": null,
          "data": [
            {
              "uuid": "d73cae65-2e18-4750-8639-0efe4dbb5827",
              "version_id": "569e0645-52d7-4f24-93fb-38374bbeedb7",
              "tag_uuid": "ac558811-6e11-42eb-88ed-80433de349e9",
              "tag": "dev"
            },
            {
              "uuid": "d73cae65-2e18-4750-8639-0efe4dbb5827",
              "version_id": "569e0645-52d7-4f24-93fb-38374bbeedb7",
              "tag_uuid": "029ef3f6-96b8-4a32-8973-534d18395929",
              "tag": "prd"
            }
          ],
          "payload": null
        }
        """
)
public class PromptTagsResponse {

    @Schema(description = "응답 시각(UTC epoch millis)")
    private Long timestamp;

    @Schema(description = "응답 코드(예: 1=성공)")
    private Integer code;

    @Schema(description = "상세 메시지")
    private String detail;

    @Schema(description = "트레이스 ID")
    private String traceId;

    @Schema(description = "태그 데이터 목록")
    private List<PromptTag> data;

    @Schema(description = "추가 페이로드")
    private JsonNode payload;

    /**
     * 단일 태그 아이템 (data 배열 원소)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "프롬프트 태그")
    public static class PromptTag {

        @Schema(description = "프롬프트 UUID")
        private String uuid;

        @JsonProperty("version_id")
        @Schema(description = "버전 UUID")
        private String versionId;

        @JsonProperty("tag_uuid")
        @Schema(description = "태그 UUID")
        private String tagUuid;

        @Schema(description = "태그명")
        private String tag;
    }
}
