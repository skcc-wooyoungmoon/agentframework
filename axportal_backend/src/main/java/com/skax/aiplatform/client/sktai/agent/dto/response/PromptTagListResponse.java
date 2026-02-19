package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 태그 목록 응답 DTO
 * 
 * <p>모든 프롬프트 태그의 목록을 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Inference Prompt 태그 목록 응답")
public class PromptTagListResponse {

    @JsonProperty("data")
    @Schema(description = "태그 목록 배열")
    private List<String> data;

    @JsonProperty("total")
    @Schema(description = "총 태그 개수")
    private Integer total;
}
