package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 댓글 수정 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 기존 Few-Shot 댓글을 수정하기 위한 요청 데이터 구조입니다.
 * 작성된 댓글의 내용을 변경하거나 추가 정보를 업데이트할 때 사용됩니다.</p>
 * 
 * <h3>댓글 수정 특징:</h3>
 * <ul>
 *   <li><strong>기존 댓글 업데이트</strong>: 댓글 UUID를 통한 특정 댓글 수정</li>
 *   <li><strong>버전 관리</strong>: 댓글 수정 이력 추적</li>
 *   <li><strong>권한 제어</strong>: 작성자 또는 관리자만 수정 가능</li>
 *   <li><strong>마크다운 지원</strong>: 풍부한 텍스트 표현 지원</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>댓글 내용 오타 수정</li>
 *   <li>추가 피드백 정보 업데이트</li>
 *   <li>의견 변경사항 반영</li>
 *   <li>댓글 품질 개선</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotCommentUpdateRequest request = FewShotCommentUpdateRequest.builder()
 *     .content("수정된 피드백: 이 Few-Shot 예제는 정확도를 15% 향상시켰습니다.")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see FewShotCommentResponse 댓글 수정 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 댓글 수정 요청 정보",
    example = """
        {
          "content": "수정된 피드백: 이 Few-Shot 예제는 정확도를 15% 향상시켰습니다."
        }
        """
)
public class FewShotCommentUpdateRequest {
    
    /**
     * 수정할 댓글 내용
     * 
     * <p>기존 댓글을 대체할 새로운 내용입니다.
     * 마크다운 형식을 지원하며, 기존 내용을 완전히 대체합니다.</p>
     * 
     * @implNote 부분 수정이 아닌 전체 내용 교체 방식으로 동작합니다.
     * @apiNote 빈 내용으로 수정할 수 없으며, 최소 1자 이상 입력해야 합니다.
     */
    @JsonProperty("content")
    @Schema(
        description = "수정할 댓글 내용 (마크다운 지원)", 
        example = "수정된 피드백: 이 Few-Shot 예제는 정확도를 15% 향상시켰습니다.",
        required = true,
        minLength = 1,
        maxLength = 2000
    )
    private String content;
}
