package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent App 상세 정보 래퍼 응답 DTO
 * 
 * <p>SKT AI Platform API의 data 래퍼를 포함한 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-10-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent App 상세 정보 래퍼 응답")
public class AppDetailResponse {
    
    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프")
    private Long timestamp;
    
    @JsonProperty("code")
    @Schema(description = "응답 코드")
    private Integer code;
    
    @JsonProperty("detail")
    @Schema(description = "응답 상세 메시지")
    private String detail;
    
    @JsonProperty("traceId")
    @Schema(description = "추적 ID")
    private String traceId;
    
    @JsonProperty("data")
    @Schema(description = "앱 상세 데이터")
    private AppResponse data;
    
    @JsonProperty("payload")
    @Schema(description = "추가 페이로드")
    private Object payload;
} 