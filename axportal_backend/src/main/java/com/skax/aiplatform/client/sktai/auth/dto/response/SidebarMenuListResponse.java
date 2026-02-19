package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 사이드바 메뉴 목록 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사이드바 메뉴 목록 응답")
public class SidebarMenuListResponse {
    
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID", example = "project-123")
    private String projectId;
    
    @JsonProperty("menus")
    @Schema(description = "사이드바 메뉴 목록")
    private List<SidebarMenuResponse> menus;
    
    @JsonProperty("total")
    @Schema(description = "총 메뉴 수", example = "5")
    private Integer total;
}
