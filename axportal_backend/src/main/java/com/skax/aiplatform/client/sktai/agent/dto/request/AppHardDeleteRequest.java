package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent App 하드 삭제 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 하드 삭제 요청 정보")
public class AppHardDeleteRequest {
    
    @JsonProperty("app_ids")
    @Schema(description = "영구 삭제할 App ID 목록", required = true)
    private List<String> appIds;
    
    @JsonProperty("confirmed")
    @Schema(description = "영구 삭제 확인", required = true)
    private Boolean confirmed;
}
