package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 하드 삭제 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 하드 삭제 응답 정보")
public class AppHardDeleteResponse {
    
    @JsonProperty("deleted_count")
    @Schema(description = "삭제된 앱 수")
    private Integer deletedCount;
    
    @JsonProperty("message")
    @Schema(description = "처리 결과 메시지")
    private String message;
    
    @JsonProperty("success")
    @Schema(description = "성공 여부")
    private Boolean success;
}
