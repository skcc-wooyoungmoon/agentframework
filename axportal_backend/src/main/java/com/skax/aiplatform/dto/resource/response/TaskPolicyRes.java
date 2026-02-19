package com.skax.aiplatform.dto.resource.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task Policy Response DTO
 * 
 * <p>Task Policy 정보를 담는 내부 응답 DTO입니다.</p>
 * 
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task Policy 정보")
public class TaskPolicyRes {
    
    /**
     * 태스크 타입
     */
    @JsonProperty("task_type")
    @Schema(description = "태스크 타입", example = "finetuning")
    private String taskType;
    
    /**
     * 리소스 크기
     */
    @Schema(description = "리소스 크기", example = "small")
    private String size;
    
    /**
     * CPU 코어 수
     */
    @Schema(description = "CPU 코어 수", example = "4")
    private Integer cpu;
    
    /**
     * 메모리 크기 (GB)
     */
    @Schema(description = "메모리 크기 (GB)", example = "8")
    private Integer memory;
    
    /**
     * GPU 개수
     */
    @Schema(description = "GPU 개수", example = "1")
    private Integer gpu;
}
