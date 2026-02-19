package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;

import java.util.List;

/**
 * SKTAI Agent Graphs 목록 응답 DTO
 * 
 * <p>Agent 그래프 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 페이징 정보와 함께 그래프 목록을 제공합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Graphs 목록 응답")
public class GraphsResponse {
    
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
    @Schema(description = "Agent 그래프 목록")
    private List<GraphResponse> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이징 정보")
    private Payload payload;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "페이징 정보")
    public static class Payload {
        
        @JsonProperty("pagination")
        @Schema(description = "페이징 정보")
        private Pagination pagination;
    }
}
