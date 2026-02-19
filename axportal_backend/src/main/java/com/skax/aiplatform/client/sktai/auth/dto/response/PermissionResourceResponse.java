package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 권한 리소스 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "권한 리소스 응답")
public class PermissionResourceResponse {
    
    @JsonProperty("success")
    @Schema(description = "성공 여부", example = "true")
    private Boolean success;
    
    @JsonProperty("message")
    @Schema(description = "응답 메시지", example = "권한 리소스가 성공적으로 처리되었습니다.")
    private String message;
    
    @JsonProperty("resources")
    @Schema(description = "권한 리소스 목록")
    private Object resources;
    
    @JsonProperty("total")
    @Schema(description = "총 리소스 수", example = "25")
    private Integer total;
}
