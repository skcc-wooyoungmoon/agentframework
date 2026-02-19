package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Agent Few-Shot 댓글 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot 댓글 관련 작업의 응답 데이터 구조입니다.
 * 댓글 생성, 조회, 수정 등의 작업 결과를 나타내며, 댓글의 상세 정보를 포함합니다.</p>
 * 
 * <h3>댓글 정보 포함 항목:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: UUID, 내용, 작성자 정보</li>
 *   <li><strong>시간 정보</strong>: 생성일시, 수정일시</li>
 *   <li><strong>메타데이터</strong>: 댓글 상태, 버전 정보</li>
 *   <li><strong>연관 정보</strong>: Few-Shot 버전과의 관계</li>
 * </ul>
 * 
 * <h3>응답 시나리오:</h3>
 * <ul>
 *   <li><strong>댓글 생성</strong>: 새로 생성된 댓글 정보 반환</li>
 *   <li><strong>댓글 조회</strong>: 기존 댓글 상세 정보 반환</li>
 *   <li><strong>댓글 수정</strong>: 수정된 댓글 정보 반환</li>
 *   <li><strong>댓글 목록</strong>: 여러 댓글의 배열로 사용</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotCommentResponse comment = fewShotClient.createComment(versionId, request);
 * String commentId = comment.getCommentUuid();
 * String content = comment.getContent();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see FewShotCommentCreateRequest 댓글 생성 요청
 * @see FewShotCommentUpdateRequest 댓글 수정 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 댓글 응답 정보",
    example = """
        {
          "comment_uuid": "comment-123e4567-e89b-12d3-a456-426614174000",
          "version_id": "version-456",
          "content": "이 Few-Shot 예제가 매우 유용합니다. 성능이 향상되었네요.",
          "author_id": "user-789",
          "author_name": "홍길동",
          "created_at": "2025-08-22T10:30:00Z",
          "updated_at": "2025-08-22T10:30:00Z"
        }
        """
)
public class FewShotCommentResponse {
    
    /**
     * 댓글 고유 식별자
     * 
     * <p>댓글의 고유한 UUID입니다.
     * 댓글 수정, 삭제 등의 작업 시 참조 키로 사용됩니다.</p>
     * 
     * @implNote UUID v4 형식으로 생성되며, 전역적으로 고유합니다.
     */
    @JsonProperty("comment_uuid")
    @Schema(
        description = "댓글 고유 식별자 (UUID)", 
        example = "comment-123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String commentUuid;
    
    /**
     * Few-Shot 버전 식별자
     * 
     * <p>댓글이 속한 Few-Shot 버전의 식별자입니다.
     * 특정 버전에 대한 댓글임을 나타냅니다.</p>
     */
    @JsonProperty("version_id")
    @Schema(
        description = "댓글이 속한 Few-Shot 버전 ID", 
        example = "version-456"
    )
    private String versionId;
    
    /**
     * 댓글 내용
     * 
     * <p>댓글의 실제 텍스트 내용입니다.
     * 마크다운 형식을 지원하며, Few-Shot에 대한 피드백이나 의견을 포함합니다.</p>
     */
    @JsonProperty("content")
    @Schema(
        description = "댓글 내용 (마크다운 지원)", 
        example = "이 Few-Shot 예제가 매우 유용합니다. 성능이 향상되었네요."
    )
    private String content;
    
    /**
     * 작성자 식별자
     * 
     * <p>댓글을 작성한 사용자의 식별자입니다.</p>
     */
    @JsonProperty("author_id")
    @Schema(
        description = "댓글 작성자 ID", 
        example = "user-789"
    )
    private String authorId;
    
    /**
     * 작성자 이름
     * 
     * <p>댓글을 작성한 사용자의 표시명입니다.
     * UI에서 댓글 작성자를 표시할 때 사용됩니다.</p>
     */
    @JsonProperty("author_name")
    @Schema(
        description = "댓글 작성자 이름", 
        example = "홍길동"
    )
    private String authorName;
    
    /**
     * 댓글 생성 일시
     * 
     * <p>댓글이 최초 생성된 날짜와 시간입니다.
     * ISO 8601 형식으로 제공됩니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "댓글 생성 일시 (ISO 8601)", 
        example = "2025-08-22T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 댓글 수정 일시
     * 
     * <p>댓글이 마지막으로 수정된 날짜와 시간입니다.
     * 수정되지 않은 경우 생성 일시와 동일합니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "댓글 수정 일시 (ISO 8601)", 
        example = "2025-08-22T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
}
