package com.skax.aiplatform.client.sktai.resource.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Task Quota Response DTO
 * 
 * <p>
 * 태스크 할당량 정보를 담는 응답 DTO입니다.
 * 태스크의 총 할당량과 현재 사용량을 포함합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "태스크 할당량 정보")
public class TaskQuota {

    /**
     * 총 할당량
     * 
     * <p>
     * 태스크에 할당된 총 할당량입니다.
     * </p>
     */
    @Schema(description = "총 할당량", example = "2")
    private Integer quota;

    /**
     * 사용량
     * 
     * <p>
     * 현재 사용 중인 할당량입니다.
     * </p>
     */
    @Schema(description = "사용량", example = "0")
    private Integer used;
}
