package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 태그 필터링 결과 응답 DTO
 * 
 * <p>특정 태그로 필터링된 Few-Shot 목록을 담는 응답 데이터 구조입니다.
 * 태그 기반 검색 및 분류 기능을 지원합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 태그 필터링 결과 응답")
public class FewShotFilterByTagsResponse {
    
    @JsonProperty("filter_tags")
    @Schema(description = "필터링에 사용된 태그 목록")
    private List<String> filterTags;
    
    @JsonProperty("few_shots")
    @Schema(description = "필터링된 Few-Shot 목록")
    private List<FewShotResponse> fewShots;
    
    @JsonProperty("total_count")
    @Schema(description = "필터링된 Few-Shot 총 개수")
    private Integer totalCount;
    
    @JsonProperty("page")
    @Schema(description = "현재 페이지 번호")
    private Integer page;
    
    @JsonProperty("size")
    @Schema(description = "페이지 크기")
    private Integer size;
}
