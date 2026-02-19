package com.skax.aiplatform.client.sktai.evaluation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI 모델 벤치마크 로그 목록 응답 DTO
 * 
 * <p>모델 벤치마크 실행 로그들의 페이징된 목록을 담는 응답 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 벤치마크 로그 목록 응답")
public class ModelBenchmarkLogsResponse {
    
    @JsonProperty("total")
    @Schema(description = "전체 로그 수", example = "500")
    private Integer total;
    
    @JsonProperty("page")
    @Schema(description = "현재 페이지", example = "1")
    private Integer page;
    
    @JsonProperty("size")
    @Schema(description = "페이지 크기", example = "20")
    private Integer size;
    
    @JsonProperty("logs")
    @Schema(description = "로그 목록")
    private List<BenchmarkLogEntry> logs;
    
    /**
     * 개별 로그 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "벤치마크 로그 항목")
    public static class BenchmarkLogEntry {
        
        @JsonProperty("id")
        @Schema(description = "로그 ID", example = "1")
        private Integer id;
        
        @JsonProperty("benchmark_task_id")
        @Schema(description = "벤치마크 태스크 ID", example = "task-123")
        private String benchmarkTaskId;
        
        @JsonProperty("level")
        @Schema(description = "로그 레벨", example = "INFO")
        private String level;
        
        @JsonProperty("message")
        @Schema(description = "로그 메시지", example = "Starting model evaluation...")
        private String message;
        
        @JsonProperty("timestamp")
        @Schema(description = "로그 시간")
        private LocalDateTime timestamp;
    }
}
