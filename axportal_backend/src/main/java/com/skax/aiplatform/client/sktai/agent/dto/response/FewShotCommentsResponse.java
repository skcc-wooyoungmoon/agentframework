package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 댓글 목록 응답 DTO
 * 
 * <p>특정 Few-Shot에 대한 댓글 목록을 담는 응답 데이터 구조입니다.
 * 사용자 간의 협업과 피드백을 지원합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Few-Shot 댓글 목록 응답")
public class FewShotCommentsResponse {
    
    @JsonProperty("few_shot_uuid")
    @Schema(description = "Few-Shot UUID")
    private String fewShotUuid;
    
    @JsonProperty("comments")
    @Schema(description = "댓글 목록")
    private List<Object> comments;
    
    @JsonProperty("total_count")
    @Schema(description = "전체 댓글 수")
    private Integer totalCount;
    
    @JsonProperty("page")
    @Schema(description = "현재 페이지 번호")
    private Integer page;
    
    @JsonProperty("size")
    @Schema(description = "페이지 크기")
    private Integer size;
}
