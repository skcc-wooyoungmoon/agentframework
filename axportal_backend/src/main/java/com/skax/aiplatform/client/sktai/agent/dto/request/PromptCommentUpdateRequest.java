package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent 프롬프트 댓글 수정 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 기존 프롬프트 댓글을 수정하기 위한 요청 데이터 구조입니다.
 * 댓글 작성자나 권한이 있는 관리자가 댓글 내용을 수정할 때 사용됩니다.</p>
 * 
 * <h3>댓글 수정 특징:</h3>
 * <ul>
 *   <li><strong>권한 기반</strong>: 댓글 작성자 또는 관리자만 수정 가능</li>
 *   <li><strong>버전 관리</strong>: 수정 이력이 자동으로 기록됩니다</li>
 *   <li><strong>실시간 반영</strong>: 수정된 댓글이 즉시 반영됩니다</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>댓글 내용 오타 수정</li>
 *   <li>추가 정보나 설명 보완</li>
 *   <li>부적절한 내용 수정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PromptCommentUpdateRequest request = PromptCommentUpdateRequest.builder()
 *     .content("수정된 댓글 내용입니다.")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see PromptCommentResponse 댓글 수정 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent 프롬프트 댓글 수정 요청 정보",
    example = """
        {
          "content": "수정된 댓글 내용입니다."
        }
        """
)
public class PromptCommentUpdateRequest {
    
    /**
     * 수정할 댓글 내용
     * 
     * <p>새로 변경할 댓글의 내용입니다.
     * 마크다운 형식을 지원하며, 프롬프트에 대한 피드백이나 설명을 포함할 수 있습니다.</p>
     * 
     * @implNote 댓글 내용은 HTML 태그가 자동으로 이스케이프되어 보안이 강화됩니다.
     * @apiNote 빈 내용은 허용되지 않으며, 최소 1자 이상 입력해야 합니다.
     */
    @JsonProperty("content")
    @Schema(
        description = "수정할 댓글 내용 (마크다운 지원)", 
        example = "수정된 댓글 내용입니다.",
        required = true,
        minLength = 1,
        maxLength = 2000
    )
    private String content;
}
