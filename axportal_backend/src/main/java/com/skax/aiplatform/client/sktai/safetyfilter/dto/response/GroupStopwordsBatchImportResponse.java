package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.GroupStopwordsImportItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 그룹 Stopwords 배치 Import 응답 DTO
 *
 * <p>
 * Safety Filter 그룹과 Stopwords를 일괄 Import한 결과를 나타내는 응답 데이터 구조입니다.
 * 전체 처리 결과, 성공/실패 카운트, 상세 오류 정보를 포함합니다.
 * </p>
 *
 * <h3>응답 필드:</h3>
 * <ul>
 * <li><strong>status</strong>: 전체 처리 상태 (success, partial_success, failed)</li>
 * <li><strong>success_count</strong>: 성공한 아이템 수</li>
 * <li><strong>failure_count</strong>: 실패한 아이템 수</li>
 * <li><strong>total_count</strong>: 전체 처리된 아이템 수</li>
 * <li><strong>successful_group_ids</strong>: 성공한 그룹 ID 리스트</li>
 * <li><strong>failed_group_ids</strong>: 실패한 그룹 ID 리스트</li>
 * <li><strong>errors</strong>: 상세 오류 정보 (선택)</li>
 * </ul>
 *
 * <h3>처리 상태 종류:</h3>
 * <ul>
 * <li><strong>success</strong>: 모든 아이템 Import 성공</li>
 * <li><strong>partial_success</strong>: 일부 성공, 일부 실패</li>
 * <li><strong>failed</strong>: 모든 아이템 Import 실패</li>
 * </ul>
 *
 * <h3>응답 예시:</h3>
 * 
 * <pre>
 * {
 *   "status": "partial_success",
 *   "success_count": 2,
 *   "failure_count": 1,
 *   "total_count": 3,
 *   "successful_group_ids": [
 *     "550e8400-e29b-41d4-a716-446655440000",
 *     "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
 *   ],
 *   "failed_group_ids": [
 *     "7c9e6679-7425-40de-944b-e07fc1f90ae7"
 *   ],
 *   "errors": [
 *     {
 *       "group_id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
 *       "error_message": "Duplicate group name"
 *     }
 *   ]
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-12-03
 * @version 1.0
 * @see GroupStopwordsImportItem Import 요청 아이템 DTO
 * @see GroupStopwordsImportError Import 오류 상세 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "그룹 Stopwords 배치 Import 응답 정보", example = """
        {
          "status": "partial_success",
          "success_count": 2,
          "failure_count": 1,
          "total_count": 3,
          "successful_group_ids": [
            "550e8400-e29b-41d4-a716-446655440000",
            "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
          ],
          "failed_group_ids": [
            "7c9e6679-7425-40de-944b-e07fc1f90ae7"
          ],
          "errors": [...]
        }
        """)
public class GroupStopwordsBatchImportResponse {

    /**
     * 전체 처리 상태
     *
     * <p>
     * 배치 Import 작업의 최종 상태를 나타냅니다.
     * </p>
     *
     * <p>
     * 가능한 상태값:
     * </p>
     * <ul>
     * <li><strong>success</strong>: 모든 아이템 Import 성공</li>
     * <li><strong>partial_success</strong>: 일부 성공, 일부 실패</li>
     * <li><strong>failed</strong>: 모든 아이템 Import 실패</li>
     * </ul>
     *
     * @implNote 클라이언트는 이 값을 기반으로 재시도 여부를 결정할 수 있습니다.
     */
    @JsonProperty("status")
    @Schema(description = "전체 처리 상태 (success, partial_success, failed)", example = "partial_success", required = true, allowableValues = {
            "success", "partial_success", "failed" })
    private String status;

    /**
     * 성공한 아이템 수
     *
     * <p>
     * 배치 Import에서 성공적으로 처리된 아이템의 개수입니다.
     * </p>
     *
     * @apiNote success_count + failure_count = total_count
     */
    @JsonProperty("success_count")
    @Schema(description = "성공한 아이템 수", example = "2", required = true, minimum = "0")
    private Integer successCount;

    /**
     * 실패한 아이템 수
     *
     * <p>
     * 배치 Import에서 실패한 아이템의 개수입니다.
     * </p>
     *
     * @apiNote 실패한 아이템의 상세 오류는 errors 필드에서 확인할 수 있습니다.
     */
    @JsonProperty("failure_count")
    @Schema(description = "실패한 아이템 수", example = "1", required = true, minimum = "0")
    private Integer failureCount;

    /**
     * 전체 처리된 아이템 수
     *
     * <p>
     * 배치 Import 요청에 포함된 전체 아이템의 개수입니다.
     * </p>
     *
     * @apiNote total_count = success_count + failure_count
     */
    @JsonProperty("total_count")
    @Schema(description = "전체 처리된 아이템 수", example = "3", required = true, minimum = "0")
    private Integer totalCount;

    /**
     * 성공한 그룹 ID 리스트
     *
     * <p>
     * Import에 성공한 그룹들의 ID 리스트입니다 (UUID 형식).
     * 이 그룹들은 정상적으로 생성되거나 업데이트되었습니다.
     * </p>
     *
     * @implNote 빈 리스트일 수 있으며, 모든 아이템이 실패한 경우 비어있습니다.
     */
    @JsonProperty("successful_group_ids")
    @Schema(description = "성공한 그룹 ID 리스트 (UUID)", example = "[\"550e8400-e29b-41d4-a716-446655440000\", \"6ba7b810-9dad-11d1-80b4-00c04fd430c8\"]")
    private List<String> successfulGroupIds;

    /**
     * 실패한 그룹 ID 리스트
     *
     * <p>
     * Import에 실패한 그룹들의 ID 리스트입니다 (UUID 형식).
     * 각 실패 원인은 errors 필드에서 확인할 수 있습니다.
     * </p>
     *
     * @implNote 빈 리스트일 수 있으며, 모든 아이템이 성공한 경우 비어있습니다.
     */
    @JsonProperty("failed_group_ids")
    @Schema(description = "실패한 그룹 ID 리스트 (UUID)", example = "[\"7c9e6679-7425-40de-944b-e07fc1f90ae7\"]")
    private List<String> failedGroupIds;

    /**
     * 상세 오류 정보
     *
     * <p>
     * 실패한 각 아이템에 대한 상세 오류 정보입니다.
     * 선택적 필드이며, 실패한 아이템이 없으면 null이거나 빈 리스트입니다.
     * </p>
     *
     * <p>
     * 각 오류 정보는 다음을 포함합니다:
     * </p>
     * <ul>
     * <li>group_id: 실패한 그룹 ID</li>
     * <li>error_message: 오류 메시지</li>
     * </ul>
     *
     * @implNote 클라이언트는 이 정보를 기반으로 재시도 전략을 수립할 수 있습니다.
     */
    @JsonProperty("errors")
    @Schema(description = "상세 오류 정보 (선택 사항)", implementation = GroupStopwordsImportError.class)
    private List<GroupStopwordsImportError> errors;
}
