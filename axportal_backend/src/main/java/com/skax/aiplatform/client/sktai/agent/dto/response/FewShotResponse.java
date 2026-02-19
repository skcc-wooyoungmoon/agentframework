package com.skax.aiplatform.client.sktai.agent.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 상세 정보 응답 DTO
 * 
 * <p>
 * 특정 Few-Shot의 상세 정보를 담는 응답 데이터 구조입니다.
 * </p>
 *
 * @author gyuHeeHwang
 * @since 2025-08-19
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 상세 정보 응답")
public class FewShotResponse {

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
    @Schema(description = "Few-Shot 상세 정보")
    private FewShotDetail data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    /**
     * Few-Shot 상세 정보
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Few-Shot 상세 정보")
    public static class FewShotDetail {

        @JsonProperty("uuid")
        @Schema(description = "Few-Shot UUID")
        private String uuid;

        @JsonProperty("name")
        @Schema(description = "Few-Shot 이름")
        private String name;

        @JsonProperty("dependency")
        @Schema(description = "의존성 목록")
        private List<String> dependency;

        @JsonProperty("release_version")
        @Schema(description = "릴리즈 버전")
        private Integer releaseVersion;

        @JsonProperty("latest_version")
        @Schema(description = "최신 버전")
        private Integer latestVersion;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;

        @JsonProperty("created_by")
        @Schema(description = "생성자")
        private String createdBy;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<String> tags;

        @JsonProperty("hit_rate")
        @Schema(description = "히트율")
        private Double hitRate;
    }
}
