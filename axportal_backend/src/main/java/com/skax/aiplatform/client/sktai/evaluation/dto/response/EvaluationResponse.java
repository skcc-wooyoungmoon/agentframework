package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI 평가 응답 DTO
 * 
 * <p>개별 평가 정보를 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 응답")
public class EvaluationResponse {
    
    @JsonProperty("id")
    @Schema(description = "평가 ID", example = "1")
    private Integer id;
    
    @JsonProperty("name")
    @Schema(description = "평가명", example = "GPT-4 Performance Test")
    private String name;
    
    @JsonProperty("description")
    @Schema(description = "평가 설명", example = "GPT-4 모델의 성능 평가")
    private String description;
    
    @JsonProperty("type")
    @Schema(description = "평가 타입", example = "benchmark")
    private String type;
    
    @JsonProperty("status")
    @Schema(description = "평가 상태", example = "completed")
    private String status;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
}
