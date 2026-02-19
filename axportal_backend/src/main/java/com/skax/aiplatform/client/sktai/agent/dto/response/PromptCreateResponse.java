package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Inference Prompt 생성 응답 DTO
 *
 * <p>실제 API 응답 구조에 맞춘 표준 래퍼 형식의 응답입니다.
 * 응답 메타데이터(timestamp, code, detail, traceId)와 data, payload를 포함합니다.</p>
 *
 * <h3>data</h3>
 * <ul>
 *   <li>prompt_uuid: 생성된 프롬프트의 UUID</li>
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
        description = "SKTAI Agent Inference Prompt 생성 응답",
        example = """
                {
                  "timestamp": "2025-10-31T15:25:56.169Z",
                  "code": 0,
                  "detail": "string",
                  "traceId": "string",
                  "data": {
                    "prompt_uuid": "4ff2dab7-bffe-414d-88a5-1826b9fea8df"
                  },
                  "payload": {
                    "pagination": {
                      "page": 0,
                      "first_page_url": "string",
                      "from_": 0,
                      "last_page": 0,
                      "links": [
                        {
                          "url": "string",
                          "label": "string",
                          "active": true,
                          "page": 0
                        }
                      ],
                      "next_page_url": "string",
                      "items_per_page": 0,
                      "prev_page_url": "string",
                      "to": 0,
                      "total": 0
                    }
                  }
                }
                """
)
public class PromptCreateResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 생성 시각(ISO 8601)", example = "2025-10-31T15:25:56.169Z")
    private String timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드", example = "0")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "상세 메시지", example = "string")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID", example = "string")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "생성 결과 데이터")
    private PromptCreateBody data;

    @JsonProperty("payload")
    @Schema(description = "추가 메타데이터(페이징 등)")
    private Payload payload;

    /**
     * backward compatibility helper
     * 기존 코드에서 createResponse.getPromptUuid() 호출을 지원합니다.
     */
    public String getPromptUuid() {
        return (data != null) ? data.getPromptUuid() : null;
    }

}
