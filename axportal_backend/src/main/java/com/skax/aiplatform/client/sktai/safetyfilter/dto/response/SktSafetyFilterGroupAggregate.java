package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI SafetyFilter 그룹 집계 정보 응답 DTO
 *
 * <p>SafetyFilter 그룹의 기본 정보와 함께 해당 그룹에 속한 불용어들의 집계 정보를 포함하는 응답입니다.
 * 그룹의 상세한 현황을 파악할 때 사용됩니다.</p>
 *
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>그룹 정보</strong>: ID, 이름, 생성/수정 이력</li>
 *   <li><strong>불용어 정보</strong>: 그룹에 속한 불용어 목록</li>
 *   <li><strong>집계 정보</strong>: 불용어 개수</li>
 * </ul>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>그룹별 불용어 현황 조회</li>
 *   <li>그룹 관리 대시보드 표시</li>
 *   <li>불용어 통계 분석</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupUpdateRes 그룹 기본 정보만 포함하는 DTO
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 집계 정보 (불용어 목록 포함)",
        example = """
                {
                  "group_id": "123e4567-e89b-12d3-a456-426614174000",
                  "group_name": "욕설 필터 그룹",
                  "stopwords": [
                    {
                      "id": "stopword-1",
                      "stopword": "욕설1"
                    }
                  ],
                  "count": 10,
                  "created_at": "2025-10-17T10:30:00Z",
                  "updated_at": "2025-10-17T15:45:00Z",
                  "created_by": "user123",
                  "updated_by": "user456"
                }
                """
)
public class SktSafetyFilterGroupAggregate {

    /**
     * 그룹 ID
     *
     * <p>SafetyFilter 그룹의 고유 식별자입니다.
     * null인 경우 '미분류' 그룹을 의미할 수 있습니다.</p>
     */
    @JsonProperty("group_id")
    @Schema(
            description = "그룹 고유 식별자 (null이면 미분류 그룹)",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String groupId;

    /**
     * 그룹 이름
     *
     * <p>그룹의 표시 이름입니다.
     * 그룹의 목적이나 특성을 나타냅니다.</p>
     */
    @JsonProperty("group_name")
    @Schema(
            description = "그룹 이름",
            example = "욕설 필터 그룹"
    )
    private String groupName;

    /**
     * 그룹에 속한 불용어 목록
     *
     * <p>이 그룹에 속한 모든 불용어들의 정보입니다.
     * 각 불용어는 ID와 실제 텍스트를 포함합니다.</p>
     *
     * @implNote Object 타입을 사용하지만, 실제로는 불용어 정보 객체들의 배열입니다.
     */
    @JsonProperty("stopwords")
    @Schema(
            description = "그룹에 속한 불용어 목록",
            example = """
                    [
                      {
                        "id": "stopword-1",
                        "stopword": "욕설1"
                      },
                      {
                        "id": "stopword-2",
                        "stopword": "욕설2"
                      }
                    ]
                    """
    )
    private List<StopWordData> stopWords;

    /**
     * 불용어 개수
     *
     * <p>이 그룹에 속한 불용어의 총 개수입니다.
     * stopwords 배열의 길이와 일치해야 합니다.</p>
     */
    @JsonProperty("count")
    @Schema(
            description = "그룹에 속한 불용어 총 개수",
            example = "10"
    )
    private Integer count;

    /**
     * 생성 일시
     *
     * <p>그룹이 생성된 일시입니다.
     * null인 경우 시스템 기본 그룹일 수 있습니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
            description = "그룹 생성 일시 (null일 수 있음)",
            example = "2025-10-17T10:30:00Z"
    )
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     *
     * <p>그룹이 마지막으로 수정된 일시입니다.
     * null인 경우 시스템 기본 그룹일 수 있습니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
            description = "그룹 수정 일시 (null일 수 있음)",
            example = "2025-10-17T15:45:00Z"
    )
    private LocalDateTime updatedAt;

    /**
     * 생성자
     *
     * <p>그룹을 생성한 사용자입니다.
     * null인 경우 시스템 생성 그룹일 수 있습니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(
            description = "그룹 생성자 (null일 수 있음)",
            example = "user123"
    )
    private String createdBy;

    /**
     * 수정자
     *
     * <p>그룹을 마지막으로 수정한 사용자입니다.
     * null인 경우 시스템 수정일 수 있습니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
            description = "그룹 수정자 (null일 수 있음)",
            example = "user456"
    )
    private String updatedBy;

    /**
     * 불용어 정보 내부 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "불용어 정보")
    public static class StopWordData {

        @JsonProperty("id")
        @Schema(description = "불용어 ID", example = "b5fea912-2fcb-4ee0-8bae-3aa0c42935d0")
        private String id;

        @JsonProperty("stopword")
        @Schema(description = "불용어", example = "단어1")
        private String stopWord;

    }

}
