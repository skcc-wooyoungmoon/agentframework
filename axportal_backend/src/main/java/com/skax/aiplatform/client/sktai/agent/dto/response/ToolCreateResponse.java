package com.skax.aiplatform.client.sktai.agent.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Tool 생성 응답 DTO
 * 
 * <p>
 * SKTAI Agent 시스템에서 Tool 생성 결과를 담는 응답 데이터 구조입니다.
 * </p>
 *
 * @author gyuHeeHwang
 * @since 2025-08-25
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Tool 생성 응답")
public class ToolCreateResponse {
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
    @Schema(description = "Tool 생성 결과 정보")
    private ToolsDetail data;

    @JsonProperty("payload")
    @Schema(description = "페이로드 정보")
    private Payload payload;

    /**
     * Tool 생성 결과 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Tool 생성 결과 정보")
    public static class ToolsDetail {

        @JsonProperty("description")
        @Schema(description = "Tool 설명", example = "testtesttest")
        private String description;

        @JsonProperty("tool_type")
        @Schema(description = "Tool 타입", example = "custom_api")
        private String toolType;

        @JsonProperty("created_at")
        @Schema(description = "생성 시간")
        private String createdAt;

        @JsonProperty("created_by")
        @Schema(description = "생성자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
        private String createdBy;

        @JsonProperty("project_id")
        @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
        private String projectId;

        @JsonProperty("name")
        @Schema(description = "Tool 이름", example = "testtesttest")
        private String name;

        @JsonProperty("id")
        @Schema(description = "Tool ID", example = "cf71d95d-29c9-4131-ade4-4b88938a2a88")
        private String id;

        @JsonProperty("code")
        @Schema(description = "Tool 코드")
        private String code;

        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;

        @JsonProperty("updated_by")
        @Schema(description = "수정자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
        private String updatedBy;

        @JsonProperty("delete_flag")
        @Schema(description = "삭제 플래그", example = "false")
        private Boolean deleteFlag;

        @JsonProperty("tags")
        @Schema(description = "태그 목록")
        private List<String> tags;
    }
}
