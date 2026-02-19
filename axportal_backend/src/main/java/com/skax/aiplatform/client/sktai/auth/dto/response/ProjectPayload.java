package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 정보 DTO
 * 
 * <p>사용자가 접근 가능한 프로젝트 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI 프로젝트 정보")
public class ProjectPayload {
    
    /**
     * 프로젝트 ID
     */
    @JsonProperty("id")
    @Schema(description = "프로젝트 ID", example = "proj_123456", required = true)
    private String id;
    
    /**
     * 프로젝트 이름
     */
    @JsonProperty("name")
    @Schema(description = "프로젝트 이름", example = "My Project", required = true)
    private String name;
}
