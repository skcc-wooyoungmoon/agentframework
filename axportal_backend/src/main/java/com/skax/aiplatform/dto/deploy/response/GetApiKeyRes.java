package com.skax.aiplatform.dto.deploy.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.skax.aiplatform.dto.deploy.common.ApiKeyQuota;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API Key 조회 응답")
public class GetApiKeyRes {
    
    /**
     * @deprecated 사용하지 않음
     */
    private String id;

    @Schema(description = "API Key 이름", example = "API Key 이름")
    private String name;

    @Schema(description = "프로젝트 이름", example = "프로젝트 이름")
    private String projectName;

    @Schema(description = "API Key 타입", example = "API Key 타입")
    private String type;

    @Schema(description = "권한", example = "권한")
    private String permission;

    @Schema(description = "생성일시", example = "2025-01-01 00:00:00")
    private String createdAt;

    @Schema(description = "할당량", example = "할당량")
    private ApiKeyQuota quota; // 할당량

    @Schema(description = "소유자 정보")
    private BelongsTo belongsTo; // 소유자 정보

    @Schema(description = "API Key", example = "API Key")
    private String apiKey;

    @Schema(description = "사용량", example = "사용량")
    private Integer usedCount;

    @Schema(description = "만료여부(사용가능)", example = "true")
    private boolean expired; // 만료일시 기준으로 지났을 경우 true, 아니면 false

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "소유자 정보")
    public static class BelongsTo {
        @Schema(description = "소유자 ID", example = "소유자 ID")
        private String id;
        @Schema(description = "소유자 이름", example = "소유자 이름")
        private String name;
        @Schema(description = "소유자 부서", example = "소유자 부서")
        private String department;
    }
}
