package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; 
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.skax.aiplatform.client.sktai.common.dto.Payload;

/**
 * SKTAI Agent Few-Shot 생성 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 새로운 Few-Shot 생성 결과를 담는 응답 데이터 구조입니다.</p>
 *
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 생성 응답")
public class FewShotCreateResponse {
    
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
    @Schema(description = "Few-Shot 아이템 목록")
    private FewShotUuid data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

     /**
     * 생성된 Few-Shot UUID
     */
    @Getter     
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder    
    @Schema(description = "생성된 Few-Shot UUID")
    public static class FewShotUuid {
        
        @JsonProperty("few_shot_uuid")
        @Schema(description = "생성된 Few-Shot UUID")
        private String fewShotUuid;
    }
}