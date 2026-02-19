package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI SafetyFilter 그룹 집계 정보 목록 응답 DTO
 *
 * <p>SafetyFilter 그룹들의 상세 정보와 불용어 집계 정보를 포함하는 응답입니다.
 * 각 그룹에 속한 불용어들의 상세 정보와 통계를 함께 제공합니다.</p>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>그룹별 불용어 관리</strong>: 그룹과 불용어를 함께 조회</li>
 *   <li><strong>상세 현황 보고서</strong>: 그룹별 불용어 통계</li>
 *   <li><strong>데이터 분석</strong>: 그룹별 불용어 분포 분석</li>
 * </ul>
 *
 * <h3>페이지네이션:</h3>
 * <ul>
 *   <li>그룹 수준에서 페이지네이션 적용</li>
 *   <li>각 그룹 내 불용어들은 모두 포함</li>
 *   <li>정렬: 그룹별 또는 불용어별 정렬 가능</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupsRes 그룹 메타데이터만 포함하는 목록 응답
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 집계 정보 목록 응답 (불용어 포함)",
        example = """
                {
                  "data": [
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
                      "updated_at": "2025-10-17T15:45:00Z"
                    }
                  ],
                  "payload": {
                    "pagination": {
                      "page": 1,
                      "total": 5,
                      "items_per_page": 10
                    },
                    "response_time_ms": 125.7
                  }
                }
                """
)
public class SktSafetyFilterGroupsStopWordRes {

    /**
     * 그룹 집계 정보 목록
     *
     * <p>조회된 SafetyFilter 그룹들의 상세 정보와 불용어 집계 데이터 배열입니다.
     * 각 그룹은 기본 정보와 함께 속한 불용어들의 목록과 개수를 포함합니다.</p>
     */
    @JsonProperty("data")
    @Schema(
            description = "SafetyFilter 그룹 집계 정보 목록 (불용어 포함)",
            example = """
                    [
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
                        "updated_at": "2025-10-17T15:45:00Z"
                      }
                    ]
                    """
    )
    private List<SktSafetyFilterGroupAggregate> data;

    /**
     * 응답 메타데이터
     *
     * <p>페이지네이션 정보와 응답 시간을 포함하는 페이로드입니다.
     * null인 경우 페이지네이션이 적용되지 않았음을 의미합니다.</p>
     */
    @JsonProperty("payload")
    @Schema(
            description = "페이지네이션 정보 및 응답 메타데이터 (null일 수 있음)"
    )
    private Payload payload;

}
