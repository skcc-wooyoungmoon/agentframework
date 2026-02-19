package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Few-Shot 복사 요청 DTO
 * 
 * <p>기존 Few-Shot Learning 세트를 복사하여 새로운 Few-Shot을 생성하기 위한 요청 데이터 구조입니다.
 * 원본 Few-Shot의 모든 예제와 구조를 유지하면서 새로운 이름으로 복사본을 생성합니다.</p>
 * 
 * <h3>복사 기능 특징:</h3>
 * <ul>
 *   <li><strong>완전 복사</strong>: 예제, 태그 등 모든 내용 복사</li>
 *   <li><strong>독립성</strong>: 복사본은 원본과 독립적으로 관리</li>
 *   <li><strong>커스터마이징</strong>: 복사 시 새로운 이름 지정 가능</li>
 *   <li><strong>버전 관리</strong>: 복사본도 독립적인 버전 관리</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>기존 Few-Shot을 템플릿으로 사용하여 새로운 도메인 적용</li>
 *   <li>언어별 Few-Shot 생성 (한국어 → 영어 버전)</li>
 *   <li>Few-Shot 백업 및 실험용 복사본 생성</li>
 *   <li>팀 간 Few-Shot 공유 및 재사용</li>
 *   <li>성능 비교를 위한 변형 버전 생성</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotCopyRequest request = FewShotCopyRequest.builder()
 *     .name("Customer Sentiment Analysis - English")
 *     .build();
 * 
 * // 기존 Few-Shot "korean-sentiment-uuid"를 복사하여 영어 버전으로 생성
 * FewShotCreateResponse response = fewShotsClient.copyFewShot("korean-sentiment-uuid", request);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see FewShotCreateRequest Few-Shot 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 복사 요청 정보",
    example = """
        {
          "name": "Customer Sentiment Analysis - English"
        }
        """
)
public class FewShotCopyRequest {
    
    /**
     * 복사본 Few-Shot 이름
     * 
     * <p>복사하여 생성할 새로운 Few-Shot의 이름입니다.
     * 원본 Few-Shot과는 다른 고유한 이름이어야 하며, 프로젝트 내에서 중복될 수 없습니다.</p>
     * 
     * <h4>명명 규칙:</h4>
     * <ul>
     *   <li>원본과 구분되는 명확한 이름 사용</li>
     *   <li>용도나 버전을 나타내는 접미사 추가 권장 (예: "- English", "- V2", "- Enhanced")</li>
     *   <li>프로젝트 내 고유성 보장</li>
     * </ul>
     * 
     * <h4>명명 예시:</h4>
     * <ul>
     *   <li>"Customer Sentiment Analysis - English" (언어별 버전)</li>
     *   <li>"Product Review Classification - V2" (개선 버전)</li>
     *   <li>"FAQ Response Generation - Backup" (백업 버전)</li>
     *   <li>"Code Generation - JavaScript" (언어별 특화)</li>
     * </ul>
     * 
     * @implNote 복사본 생성 후에도 이름 변경이 가능하지만, 참조 관계를 고려해야 합니다.
     * @apiNote 원본 Few-Shot의 다른 모든 속성(예제, 태그)은 자동으로 복사됩니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "복사본 Few-Shot의 새로운 이름 (프로젝트 내 중복 불가)", 
        example = "Customer Sentiment Analysis - English",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String name;
}
