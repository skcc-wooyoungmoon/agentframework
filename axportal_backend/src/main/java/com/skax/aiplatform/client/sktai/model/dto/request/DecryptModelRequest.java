package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Private Model 복호화 요청 DTO
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Private Model 복호화 요청 정보")
public class DecryptModelRequest {
    
    @JsonProperty("target_uuid")
    @Schema(description = "복호화를 위한 Target UUID", required = true, format = "uuid")
    private String targetUuid;
}
