package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import java.util.List;

/**
 * SKTAI Agent Few-Shots item 목록 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot item 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 페이징된 Few-Shot item 목록과 메타데이터를 포함합니다.</p>
 *
 * @author gyuHeeHwang
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shots item 목록 응답")
public class FewShotItemsResponse {

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
    private List<FewShotItemSummary> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;
    
    
    /**
     * Few-Shot 아이템 요약 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 요약 정보")
    public static class FewShotItemSummary {
        
        @JsonProperty("uuid")
        @Schema(description = "Few-Shot UUID")
        private String uuid;
        
        @JsonProperty("item_sequence")
        @Schema(description = "Few-Shot 아이템 순서")
        private Integer itemSequence;
        
        @JsonProperty("item")
        @Schema(description = "아이템 내용")
        private String item;
        
        @JsonProperty("version_id")
        @Schema(description = "버전 ID")
        private String versionId;

        @JsonProperty("item_type")
        @Schema(description = "아이템 타입")
        private String itemType;
    }
}