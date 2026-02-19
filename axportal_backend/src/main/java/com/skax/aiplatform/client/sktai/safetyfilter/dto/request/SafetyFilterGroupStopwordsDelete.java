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
 * SKTAI SafetyFilter 그룹 불용어 삭제 요청 DTO
 *
 * <p>SafetyFilter 그룹에서 특정 불용어들을 삭제하는 요청입니다.
 * 지정된 불용어들만 그룹에서 제거되고 나머지는 유지됩니다.</p>
 *
 * <h3>동작 방식:</h3>
 * <ul>
 *   <li><strong>선택적 삭제</strong>: 지정된 불용어들만 삭제</li>
 *   <li><strong>존재하지 않는 불용어 무시</strong>: 없는 불용어는 자동으로 무시</li>
 *   <li><strong>원자적 처리</strong>: 모든 삭제가 하나의 트랜잭션으로 처리</li>
 * </ul>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>특정 불용어가 더 이상 필요 없을 때</li>
 *   <li>잘못 추가된 불용어를 제거할 때</li>
 *   <li>그룹 정리 및 최적화 작업</li>
 * </ul>
 *
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>존재하지 않는 불용어를 삭제해도 오류가 발생하지 않습니다</li>
 *   <li>삭제된 불용어는 복구할 수 없습니다</li>
 *   <li>그룹의 다른 불용어들은 영향받지 않습니다</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * SafetyFilterGroupStopwordsDelete request = SafetyFilterGroupStopwordsDelete.builder()
 *     .stopwords(List.of("삭제할욕설1", "삭제할욕설2"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupStopWordsCreateReq 불용어 추가용 DTO
 * @see SktSafetyFilterGroupKeywordsUpdateReq 불용어 완전 교체용 DTO
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹에서 특정 불용어 삭제 요청",
        example = """
                {
                  "stopwords": ["삭제할욕설1", "삭제할욕설2"]
                }
                """
)
public class SafetyFilterGroupStopwordsDelete {

    /**
     * 삭제할 불용어 목록
     *
     * <p>그룹에서 삭제하고자 하는 불용어들의 목록입니다.
     * 존재하지 않는 불용어는 자동으로 무시됩니다.</p>
     *
     * @apiNote 빈 배열은 허용되지 않으며, 최소 1개 이상의 불용어를 지정해야 합니다.
     */
    @NotEmpty(message = "삭제할 불용어 목록은 최소 1개 이상이어야 합니다")
    @JsonProperty("stopwords")
    @Schema(
            description = "그룹에서 삭제할 불용어 목록",
            example = "[\"삭제할욕설1\", \"삭제할욕설2\"]",
            required = true
    )
    private List<String> stopwords;

}
