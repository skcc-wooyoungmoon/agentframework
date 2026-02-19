package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 태그 목록 응답 DTO
 * 
 * <p>특정 Few-Shot에 연결된 태그 목록을 담는 응답 데이터 구조입니다.
 * 태그는 Few-Shot의 분류와 검색을 용이하게 합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 태그 목록 응답")
public class FewShotTagsResponse {
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
    @Schema(description = "Few-Shot 목록")
    private List<FewShotTagSummary> data;
    
    /**
     * Few-Shot 태그 요약 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 요약 정보")
    public static class FewShotTagSummary {
        @JsonProperty("tag_uuid")
        @Schema(description = "태그 UUID")
        private String tagUuid;
        
        @JsonProperty("tag")
        @Schema(description = "태그 이름")
        private String tag;
        
        @JsonProperty("uuid")
        @Schema(description = "Few-Shot UUID")
        private String fewShotUuid;
        
        @JsonProperty("version_id")
        @Schema(description = "버전 ID")
        private String versionId;
    }
}