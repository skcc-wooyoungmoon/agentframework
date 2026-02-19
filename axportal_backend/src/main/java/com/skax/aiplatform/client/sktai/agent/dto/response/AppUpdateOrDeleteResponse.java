package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 수정/삭제 응답 DTO
 * 
 * <p>Agent 애플리케이션 수정 또는 삭제 작업의 결과를 담는 응답 데이터 구조입니다.
 * 작업 성공 여부와 관련 정보를 제공합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 수정/삭제 결과 응답")
public class AppUpdateOrDeleteResponse {
    
    @JsonProperty("app_uuid")
    @Schema(description = "처리된 애플리케이션의 고유 식별자")
    private String appUuid;
    
    @JsonProperty("success")
    @Schema(description = "작업 성공 여부")
    private Boolean success;
    
    @JsonProperty("message")
    @Schema(description = "작업 결과 메시지")
    private String message;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간 (수정 작업의 경우)")
    private String updatedAt;
}
