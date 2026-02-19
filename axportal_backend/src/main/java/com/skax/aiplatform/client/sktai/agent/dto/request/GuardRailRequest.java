package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI GuardRail 생성/수정 요청 DTO
 * 
 * @author sonmunwoo
 * @since 2025-10-13
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가드레일 생성/수정 요청")
public class GuardRailRequest {

    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
    private String projectId;

    @JsonProperty("name")
    @Schema(description = "가드레일 이름", example = "개인정보 보호")
    private String name;

    @JsonProperty("desc")
    @Schema(description = "설명", example = "개인정보 및 민감정보 검증")
    private String desc;

    @JsonProperty("type")
    @Schema(description = "가드레일 타입 (INPUT/OUTPUT/BOTH)", example = "INPUT")
    private String type;

    @JsonProperty("config")
    @Schema(description = "가드레일 설정 (JSON)")
    private String config;
}

