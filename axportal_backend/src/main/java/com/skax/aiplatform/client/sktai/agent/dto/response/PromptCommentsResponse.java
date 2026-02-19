package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Inference Prompt 댓글 목록 응답 DTO
 * 
 * <p>특정 버전의 프롬프트 댓글 목록을 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Inference Prompt 댓글 목록 응답")
public class PromptCommentsResponse {
    
    @JsonProperty("version_id")
    @Schema(description = "버전 ID")
    private String versionId;
    
    @JsonProperty("comments")
    @Schema(description = "댓글 목록")
    private List<PromptComment> comments;
    
    @JsonProperty("total")
    @Schema(description = "총 댓글 개수")
    private Integer total;
    
    @JsonProperty("page")
    @Schema(description = "현재 페이지")
    private Integer page;
    
    @JsonProperty("size")
    @Schema(description = "페이지 크기")
    private Integer size;
    
    /**
     * 프롬프트 댓글 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프롬프트 댓글 정보")
    public static class PromptComment {
        
        @JsonProperty("comment_id")
        @Schema(description = "댓글 ID")
        private String commentId;
        
        @JsonProperty("comment")
        @Schema(description = "댓글 내용")
        private String comment;
        
        @JsonProperty("author")
        @Schema(description = "작성자")
        private String author;
        
        @JsonProperty("created_at")
        @Schema(description = "작성 시간")
        private String createdAt;
        
        @JsonProperty("updated_at")
        @Schema(description = "수정 시간")
        private String updatedAt;
    }
}
