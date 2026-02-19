package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 버전 정보 응답 DTO
 * 
 * <p>특정 Few-Shot의 특정 버전 정보를 담는 응답 데이터 구조입니다.</p>
 *
 * @author gyuHeeHwang
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 버전 정보 응답")
public class FewShotVersionResponse {

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
    @Schema(description = "Few-Shot 버전 정보")
    private FewShotVersionDetail data;

    // @JsonProperty("payload")
    // @Schema(description = "페이로드 정보")
    // private Object payload;

    /**
     * Few-Shot 버전 상세 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 버전 상세 정보")
    public static class FewShotVersionDetail {
        
        @JsonProperty("version")
        @Schema(description = "버전")
        private Integer version;

        @JsonProperty("release")
        @Schema(description = "릴리즈 여부")
        private Boolean release;

        @JsonProperty("delete_flag")
        @Schema(description = "삭제 여부")
        private Boolean deleteFlag;
        
        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;
        
        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;
        
        @JsonProperty("version_id")
        @Schema(description = "버전 ID")
        private String versionId;
        
        @JsonProperty("uuid")
        @Schema(description = "Few-Shot UUID")
        private String uuid;
    }
}