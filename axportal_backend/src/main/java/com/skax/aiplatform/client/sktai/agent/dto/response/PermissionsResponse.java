package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Permissions 목록 응답 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Permissions 목록 응답")
public class PermissionsResponse {
    
    @JsonProperty("permissions")
    @Schema(description = "Agent 권한 목록")
    private List<PermissionResponse> permissions;
    
    @JsonProperty("total_count")
    @Schema(description = "전체 권한 수")
    private Integer totalCount;
    
    @JsonProperty("page")
    @Schema(description = "현재 페이지 번호")
    private Integer page;
    
    @JsonProperty("size")
    @Schema(description = "페이지 크기")
    private Integer size;
    
    @JsonProperty("total_pages")
    @Schema(description = "전체 페이지 수")
    private Integer totalPages;
}
