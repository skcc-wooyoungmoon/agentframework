package com.skax.aiplatform.client.sktai.safetyfilter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SafetyFilter 그룹 키워드 업데이트 요청 DTO
 *
 * <p>SafetyFilter 그룹의 모든 불용어(stopword)를 새로운 목록으로 교체하는 요청입니다.
 * 기존의 모든 불용어를 삭제하고 제공된 목록으로 완전히 대체합니다.</p>
 *
 * <h3>동작 방식:</h3>
 * <ul>
 *   <li><strong>완전 교체</strong>: 기존 불용어를 모두 삭제하고 새 목록으로 교체</li>
 *   <li><strong>원자적 처리</strong>: 모든 변경이 하나의 트랜잭션으로 처리</li>
 *   <li><strong>일괄 처리</strong>: 여러 불용어를 한 번에 처리</li>
 * </ul>
 *
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>기존 불용어가 모두 삭제됩니다</li>
 *   <li>빈 배열을 제공하면 모든 불용어가 삭제됩니다</li>
 *   <li>중복된 불용어는 자동으로 제거됩니다</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * SafetyFilterGroupKeywordsUpdateInput request = SafetyFilterGroupKeywordsUpdateInput.builder()
 *     .stopwords(List.of("욕설1", "욕설2", "비속어1"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupStopWordsCreateReq 불용어 추가용 DTO
 * @see SafetyFilterGroupStopwordsDelete 불용어 삭제용 DTO
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 키워드 업데이트 요청 (기존 불용어 완전 교체)",
        example = """
                {
                  "stopwords": ["욕설1", "욕설2", "비속어1"]
                }
                """
)
public class SktSafetyFilterGroupKeywordsUpdateReq {

    /**
     * 설정할 불용어 목록
     *
     * <p>그룹에 설정할 모든 불용어의 목록입니다.
     * 기존 불용어를 모두 삭제하고 이 목록으로 완전히 교체됩니다.</p>
     *
     * @apiNote 빈 배열도 허용되며, 이 경우 모든 불용어가 삭제됩니다.
     */
    @NotEmpty(message = "불용어 목록은 최소 1개 이상이어야 합니다")
    @JsonProperty("stopwords")
    @Schema(
            description = "그룹에 설정할 모든 불용어 목록 (기존 불용어 완전 교체)",
            example = "[\"욕설1\", \"욕설2\", \"비속어1\"]"
    )
    private List<String> stopWords;

    /**
     * List<String>으로부터 SktSafetyFilterGroupKeywordsUpdateReq 생성
     *
     * @param stopWords 불용어 목록
     * @return SktSafetyFilterGroupKeywordsUpdateReq 인스턴스
     */
    public static SktSafetyFilterGroupKeywordsUpdateReq from(List<String> stopWords) {
        return SktSafetyFilterGroupKeywordsUpdateReq.builder()
                .stopWords(stopWords.stream()
                        .map(String::trim)
                        .toList())
                .build();
    }

}
