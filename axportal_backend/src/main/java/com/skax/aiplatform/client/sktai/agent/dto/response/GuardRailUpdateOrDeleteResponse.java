package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI GuardRail 수정/삭제 응답 DTO
 * 
 * @author sonmunwoo
 * @since 2025-10-13
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 수정/삭제 응답")
public class GuardRailUpdateOrDeleteResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프", example = "1760319098878")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드", example = "1")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지", example = "성공")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "처리된 가드레일 UUID")
    private String data;
}

