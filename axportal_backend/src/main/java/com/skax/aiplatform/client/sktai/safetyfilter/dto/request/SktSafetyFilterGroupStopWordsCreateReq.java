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
 * SKTAI SafetyFilter 그룹 불용어 추가 요청 DTO
 *
 * <p>SafetyFilter 그룹에 새로운 불용어들을 추가하는 요청입니다.
 * 기존 불용어를 유지하면서 새로운 불용어만 추가합니다 (비파괴적 추가).</p>
 *
 * <h3>동작 방식:</h3>
 * <ul>
 *   <li><strong>비파괴적 추가</strong>: 기존 불용어를 유지하면서 새로운 불용어만 추가</li>
 *   <li><strong>중복 처리</strong>: 이미 존재하는 불용어는 무시</li>
 *   <li><strong>원자적 처리</strong>: 모든 추가가 하나의 트랜잭션으로 처리</li>
 * </ul>
 *
 * <h3>장점:</h3>
 * <ul>
 *   <li>기존 불용어 목록을 조회할 필요 없음</li>
 *   <li>네트워크 트래픽 최소화</li>
 *   <li>동시성 문제 방지</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * SafetyFilterGroupStopwordsAppendInput request = SafetyFilterGroupStopwordsAppendInput.builder()
 *     .stopwords(List.of("새로운욕설1", "새로운욕설2"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupKeywordsUpdateReq 불용어 완전 교체용 DTO
 * @see SafetyFilterGroupStopwordsDelete 불용어 삭제용 DTO
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 불용어 추가 요청 (기존 유지하며 추가)",
        example = """
                {
                  "stopwords": ["새로운욕설1", "새로운욕설2"]
                }
                """
)
public class SktSafetyFilterGroupStopWordsCreateReq {

    /**
     * 추가할 불용어 목록
     *
     * <p>그룹에 추가할 새로운 불용어들의 목록입니다.
     * 기존 불용어는 유지되고 이 목록의 불용어들만 추가됩니다.</p>
     *
     * @apiNote 이미 존재하는 불용어는 자동으로 무시되어 중복 오류가 발생하지 않습니다.
     */
    @NotEmpty(message = "추가할 불용어 목록은 최소 1개 이상이어야 합니다")
    @JsonProperty("stopwords")
    @Schema(
            description = "그룹에 추가할 새로운 불용어 목록",
            example = "[\"새로운욕설1\", \"새로운욕설2\"]"
    )
    private List<String> stopWords;

    public static SktSafetyFilterGroupStopWordsCreateReq from(List<String> stopWords) {
        return SktSafetyFilterGroupStopWordsCreateReq.builder()
                .stopWords(stopWords)
                .build();
    }

}
