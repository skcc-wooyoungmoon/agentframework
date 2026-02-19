package com.skax.aiplatform.client.ione.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 등록 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRegistResponse {
    
    @JsonProperty("infWorkStatus")
    @Schema(description = "작업 상태")  
    private String infWorkStatus;

    @JsonProperty("infWorkMsg")
    @Schema(description = "작업 메시지")
    private String infWorkMsg;
    
    @JsonProperty("infWorkSeq")
    @Schema(description = "작업 순번")
    private String infWorkSeq;
}