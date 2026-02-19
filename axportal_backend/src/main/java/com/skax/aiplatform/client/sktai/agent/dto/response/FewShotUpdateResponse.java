package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 수정/삭제 응답 DTO
 * 
 * <p>Few-Shot 수정 또는 삭제 작업 결과를 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 수정/삭제 응답")
public class FewShotUpdateResponse {
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
    private Result data;
    
    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Object payload;

     /**
     * 생성된 Few-Shot UUID
     */
    @Getter     
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder    
    @Schema(description = "생성된 Few-Shot UUID")
    public static class Result {
        
        @JsonProperty("result")
        @Schema(description = "수정 결과")
        private boolean result;
    }
}