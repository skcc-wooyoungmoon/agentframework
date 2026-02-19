package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Model Language DTO
 * 
 * <p>모델이 지원하는 언어 정보를 나타내는 DTO입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 지원 언어 정보")
public class ModelLanguage {
    
    @JsonProperty("name")
    @Schema(description = "언어명", example = "Korean", maxLength = 64, required = true)
    private String name;
    
    @JsonProperty("id")
    @Schema(description = "언어 ID")
    private Integer id;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 일시", format = "date-time")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 일시", format = "date-time")
    private LocalDateTime updatedAt;
}
