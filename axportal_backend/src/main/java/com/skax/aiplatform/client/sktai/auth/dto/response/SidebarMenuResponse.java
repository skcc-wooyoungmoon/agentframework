package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사이드바 메뉴 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사이드바 메뉴 응답")
public class SidebarMenuResponse {
    
    @JsonProperty("role_name")
    @Schema(description = "역할 이름", example = "admin")
    private String roleName;
    
    @JsonProperty("menu_items")
    @Schema(description = "메뉴 아이템 목록")
    private Object menuItems;
    
    @JsonProperty("enabled")
    @Schema(description = "메뉴 활성화 여부", example = "true")
    private Boolean enabled;
    
    @JsonProperty("created_at")
    @Schema(description = "생성일시", example = "2025-08-22T10:30:00Z")
    private String createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정일시", example = "2025-08-22T10:30:00Z")
    private String updatedAt;
}
