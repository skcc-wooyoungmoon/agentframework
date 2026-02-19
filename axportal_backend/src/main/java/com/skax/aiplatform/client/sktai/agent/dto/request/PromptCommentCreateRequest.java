package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Inference Prompt 댓글 생성 요청 DTO
 * 
 * <p>특정 버전의 Inference Prompt에 댓글을 생성하기 위한 요청 데이터 구조입니다.
 * 프롬프트에 대한 피드백, 개선 제안, 사용 경험 등을 공유할 수 있습니다.</p>
 * 
 * <h3>댓글 시스템 특징:</h3>
 * <ul>
 *   <li><strong>버전별 댓글</strong>: 특정 프롬프트 버전에 대한 댓글</li>
 *   <li><strong>협업 지원</strong>: 팀원 간 프롬프트 피드백 공유</li>
 *   <li><strong>개선 추적</strong>: 프롬프트 개선 히스토리 관리</li>
 *   <li><strong>사용자 식별</strong>: 댓글 작성자 정보 자동 기록</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>프롬프트 성능에 대한 피드백 제공</li>
 *   <li>프롬프트 개선 제안 및 아이디어 공유</li>
 *   <li>프롬프트 사용 경험 및 결과 공유</li>
 *   <li>프롬프트 리뷰 및 승인 프로세스 지원</li>
 *   <li>버그 리포트 및 이슈 트래킹</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PromptCommentCreateRequest request = PromptCommentCreateRequest.builder()
 *     .comment("이 프롬프트는 고객 만족도가 높습니다. 다만 응답 시간이 조금 길어서 메시지를 단순화하면 좋을 것 같습니다.")
 *     .build();
 * 
 * // 프롬프트 버전 "version-123"에 댓글 생성
 * PromptCommentResponse response = promptsClient.createInferencePromptComment("version-123", request);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Inference Prompt 댓글 생성 요청 정보",
    example = """
        {
          "comment": "이 프롬프트는 고객 만족도가 높습니다. 다만 응답 시간이 조금 길어서 메시지를 단순화하면 좋을 것 같습니다."
        }
        """
)
public class PromptCommentCreateRequest {
    
    /**
     * 댓글 내용
     * 
     * <p>프롬프트에 대한 피드백, 제안, 또는 의견을 담은 댓글 내용입니다.
     * 건설적이고 구체적인 피드백을 작성하는 것이 좋습니다.</p>
     * 
     * <h4>좋은 댓글 작성 가이드:</h4>
     * <ul>
     *   <li><strong>구체성</strong>: 구체적인 개선점이나 장점 명시</li>
     *   <li><strong>건설성</strong>: 비판보다는 개선 방향 제시</li>
     *   <li><strong>근거</strong>: 의견의 근거나 경험 제시</li>
     *   <li><strong>명확성</strong>: 이해하기 쉬운 명확한 표현</li>
     * </ul>
     * 
     * <h4>댓글 예시:</h4>
     * <ul>
     *   <li>"고객 응답률이 20% 향상되었습니다. 감정 표현이 더 자연스러워졌어요."</li>
     *   <li>"시스템 메시지에 톤앤매너 가이드를 추가하면 일관성이 더 좋아질 것 같습니다."</li>
     *   <li>"변수 검증 규칙이 너무 엄격해서 사용자 편의성이 떨어집니다."</li>
     * </ul>
     * 
     * @implNote 댓글 작성 후 수정이나 삭제가 제한될 수 있으므로 신중하게 작성해야 합니다.
     * @apiNote 댓글 작성자 정보는 시스템에서 자동으로 기록됩니다.
     */
    @JsonProperty("comment")
    @Schema(
        description = "프롬프트에 대한 댓글 내용 (피드백, 제안, 의견)", 
        example = "이 프롬프트는 고객 만족도가 높습니다. 다만 응답 시간이 조금 길어서 메시지를 단순화하면 좋을 것 같습니다.",
        required = true,
        minLength = 10,
        maxLength = 2000
    )
    private String comment;
}
