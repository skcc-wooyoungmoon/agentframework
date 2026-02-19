package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Inference Prompt 댓글 생성 응답 DTO
 * 
 * <p>새로 생성된 프롬프트 댓글 정보를 담는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Agent Inference Prompt 댓글 생성 응답")
public class PromptCommentResponse {
    
    @JsonProperty("comment_id")
    @Schema(description = "생성된 댓글 ID")
    private String commentId;
    
    @JsonProperty("version_id")
    @Schema(description = "프롬프트 버전 ID")
    private String versionId;
    
    @JsonProperty("comment")
    @Schema(description = "댓글 내용")
    private String comment;
    
    @JsonProperty("author")
    @Schema(description = "작성자")
    private String author;
    
    @JsonProperty("created_at")
    @Schema(description = "작성 시간")
    private String createdAt;
    
    @JsonProperty("message")
    @Schema(description = "결과 메시지")
    private String message;
}
