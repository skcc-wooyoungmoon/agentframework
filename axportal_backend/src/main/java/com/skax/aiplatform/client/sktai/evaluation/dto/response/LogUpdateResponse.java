package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 로그 업데이트 응답 DTO
 * 
 * <p>로그 상태 업데이트 결과를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그 업데이트 응답")
public class LogUpdateResponse {
    
    @JsonProperty("success")
    @Schema(description = "업데이트 성공 여부", example = "true")
    private Boolean success;
    
    @JsonProperty("updated_count")
    @Schema(description = "업데이트된 로그 수", example = "5")
    private Integer updatedCount;
    
    @JsonProperty("message")
    @Schema(description = "업데이트 결과 메시지", example = "Log status updated successfully")
    private String message;
    
    @JsonProperty("updated_at")
    @Schema(description = "업데이트 시간")
    private LocalDateTime updatedAt;
}
