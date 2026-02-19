package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 댓글 생성 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot에 대한 댓글을 생성하기 위한 요청 데이터 구조입니다.
 * Few-Shot의 특정 버전에 대한 피드백, 리뷰, 개선사항 등을 댓글로 남길 때 사용됩니다.</p>
 * 
 * <h3>Few-Shot 댓글 특징:</h3>
 * <ul>
 *   <li><strong>버전별 댓글</strong>: 특정 Few-Shot 버전에 대한 댓글</li>
 *   <li><strong>협업 도구</strong>: 팀원 간의 피드백 및 의견 공유</li>
 *   <li><strong>개선 추적</strong>: Few-Shot 품질 개선을 위한 의견 수집</li>
 *   <li><strong>마크다운 지원</strong>: 풍부한 텍스트 표현 지원</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Few-Shot 예제의 품질에 대한 피드백</li>
 *   <li>개선 제안 및 수정 사항 제안</li>
 *   <li>사용 경험 및 결과 공유</li>
 *   <li>버전별 변경사항에 대한 리뷰</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotCommentCreateRequest request = FewShotCommentCreateRequest.builder()
 *     .content("이 Few-Shot 예제가 매우 유용합니다. 성능이 향상되었네요.")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see FewShotCommentResponse 댓글 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 댓글 생성 요청 정보",
    example = """
        {
          "content": "이 Few-Shot 예제가 매우 유용합니다. 성능이 향상되었네요."
        }
        """
)
public class FewShotCommentCreateRequest {
    
    /**
     * 댓글 내용
     * 
     * <p>Few-Shot에 대한 댓글 내용입니다.
     * 마크다운 형식을 지원하며, Few-Shot에 대한 피드백이나 의견을 포함할 수 있습니다.</p>
     * 
     * @implNote 댓글 내용은 HTML 태그가 자동으로 이스케이프되어 보안이 강화됩니다.
     * @apiNote 빈 내용은 허용되지 않으며, 최소 1자 이상 입력해야 합니다.
     */
    @JsonProperty("content")
    @Schema(
        description = "댓글 내용 (마크다운 지원)", 
        example = "이 Few-Shot 예제가 매우 유용합니다. 성능이 향상되었네요.",
        required = true,
        minLength = 1,
        maxLength = 2000
    )
    private String content;
}
