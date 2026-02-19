package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사이드바 메뉴 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사이드바 메뉴 요청")
public class SidebarMenuRequest {
    
    @JsonProperty("menu_items")
    @Schema(description = "메뉴 아이템 목록")
    private Object menuItems;
    
    @JsonProperty("enabled")
    @Schema(description = "메뉴 활성화 여부", example = "true")
    private Boolean enabled;
}
