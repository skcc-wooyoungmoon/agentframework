package com.skax.aiplatform.client.sktai.agent.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 안전 필터 옵션 DTO
 * 
 * <p>Agent App 생성 시 안전 필터 설정을 위한 옵션입니다.
 * 모든 필드는 선택사항입니다.</p>
 * 
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "안전 필터 옵션 (모든 필드 선택사항)")
public class SafetyFilterOptions {
    
    /**
     * 입력 안전 필터 적용 여부 (선택사항)
     */
    @JsonProperty("safety_filter_input")
    @Schema(description = "입력 안전 필터 적용 여부 (선택사항)", example = "false", required = false)
    private Boolean safetyFilterInput;
    
    /**
     * 출력 안전 필터 적용 여부 (선택사항)
     */
    @JsonProperty("safety_filter_output")
    @Schema(description = "출력 안전 필터 적용 여부 (선택사항)", example = "false", required = false)
    private Boolean safetyFilterOutput;
    
    /**
     * 입력 안전 필터 그룹 목록 (선택사항)
     */
    @JsonProperty("safety_filter_input_groups")
    @Schema(description = "입력 안전 필터 그룹 목록 (선택사항)", example = "[\"808272c8-4b48-46fe-9341-a6dfd7d5c998\"]", required = false)
    private List<String> safetyFilterInputGroups;
    
    /**
     * 출력 안전 필터 그룹 목록 (선택사항)
     */
    @JsonProperty("safety_filter_output_groups")
    @Schema(description = "출력 안전 필터 그룹 목록 (선택사항)", example = "[\"639a30f3-6d42-45f4-abb0-802c0274ffcd\", \"4eda6e94-0745-44e8-97ac-3b6f03b77766\"]", required = false)
    private List<String> safetyFilterOutputGroups;
}

