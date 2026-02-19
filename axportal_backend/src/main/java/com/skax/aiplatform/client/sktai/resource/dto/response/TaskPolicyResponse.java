package com.skax.aiplatform.client.sktai.resource.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task Policy Response DTO
 * 
 * <p>Task Policy 정보를 담는 응답 DTO입니다.
 * 각 태스크 타입별로 사용 가능한 리소스 사양을 정의합니다.</p>
 * 
 * @author SonMunWoo
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Task Policy 정보")
public class TaskPolicyResponse {
    
    /**
     * 태스크 타입
     * 
     * <p>태스크의 종류를 나타냅니다.
     * - finetuning: 파인튜닝 태스크
     * - serving: 서빙 태스크
     * - evaluation: 평가 태스크
     * - test: 테스트 태스크</p>
     */
    @JsonProperty("task_type")
    @Schema(description = "태스크 타입", example = "finetuning", 
            allowableValues = {"finetuning", "serving", "evaluation", "test"})
    private String taskType;
    
    /**
     * 리소스 크기
     * 
     * <p>태스크에 할당될 리소스의 크기를 나타냅니다.
     * - small: 소규모 리소스
     * - medium: 중규모 리소스
     * - large: 대규모 리소스
     * - max: 최대 리소스</p>
     */
    @Schema(description = "리소스 크기", example = "small",
            allowableValues = {"small", "medium", "large", "max"})
    private String size;
    
    /**
     * CPU 코어 수
     * 
     * <p>태스크에 할당될 CPU 코어 수입니다.
     * 0인 경우 CPU 리소스가 할당되지 않음을 의미합니다.</p>
     */
    @Schema(description = "CPU 코어 수", example = "4", minimum = "0")
    private Integer cpu;
    
    /**
     * 메모리 크기 (GB)
     * 
     * <p>태스크에 할당될 메모리 크기입니다.
     * 0인 경우 메모리 리소스가 할당되지 않음을 의미합니다.</p>
     */
    @Schema(description = "메모리 크기 (GB)", example = "8", minimum = "0")
    private Integer memory;
    
    /**
     * GPU 개수
     * 
     * <p>태스크에 할당될 GPU 개수입니다.
     * 0인 경우 GPU 리소스가 할당되지 않음을 의미합니다.</p>
     */
    @Schema(description = "GPU 개수", example = "1", minimum = "0")
    private Integer gpu;
}
