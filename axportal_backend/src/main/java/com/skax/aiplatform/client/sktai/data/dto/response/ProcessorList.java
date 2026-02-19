package com.skax.aiplatform.client.sktai.data.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로세서 목록 응답 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로세서 목록 응답")
public class ProcessorList {
    
    @JsonProperty("data")
    @Schema(description = "프로세서 목록")
    private List<Processor> data;
    
    @JsonProperty("payload")
    @Schema(description = "페이지네이션 정보")
    private Payload payload;
}
