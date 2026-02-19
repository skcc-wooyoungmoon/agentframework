package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 권한 리소스 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "권한 리소스 요청")
public class PermissionResourceRequest {
    
    @JsonProperty("resources")
    @Schema(description = "권한 리소스 목록")
    private Object resources;
    
    @JsonProperty("operation")
    @Schema(description = "작업 타입", example = "append")
    private String operation;
}
