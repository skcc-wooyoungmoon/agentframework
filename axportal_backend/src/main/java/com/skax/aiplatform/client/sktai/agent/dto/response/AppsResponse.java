package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

/**
 * SKTAI Agent Apps 목록 응답 DTO
 * 
 * <p>Agent 애플리케이션 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 페이징 정보와 함께 애플리케이션 목록을 제공합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Apps 목록 응답")
public class AppsResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;
    
    @JsonProperty("data")
    @Schema(description = "Agent 애플리케이션 목록")
    private List<AppResponse> data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
}
