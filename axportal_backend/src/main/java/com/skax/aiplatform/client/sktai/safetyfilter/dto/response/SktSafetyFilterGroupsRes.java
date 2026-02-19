package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SafetyFilter 그룹 메타데이터 목록 응답 DTO
 *
 * <p>SafetyFilter 그룹들의 기본 정보 목록을 담는 응답입니다.
 * 불용어 상세 정보는 포함하지 않고 그룹의 메타데이터만 포함합니다.</p>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>그룹 목록 조회</strong>: 전체 그룹 현황 파악</li>
 *   <li><strong>그룹 선택 UI</strong>: 드롭다운, 라디오 버튼 등에서 사용</li>
 *   <li><strong>대시보드</strong>: 그룹 개요 정보 표시</li>
 * </ul>
 *
 * <h3>페이지네이션:</h3>
 * <ul>
 *   <li>payload를 통해 페이지네이션 정보 제공</li>
 *   <li>size=-1 지정 시 모든 그룹을 한 페이지에 반환</li>
 *   <li>정렬, 필터링, 검색 기능 지원</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupsStopWordRes 그룹 + 불용어 집계 정보 목록 응답
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 메타데이터 목록 응답",
        example = """
                {
                  "data": [
                    {
                      "id": "123e4567-e89b-12d3-a456-426614174000",
                      "name": "욕설 필터 그룹",
                      "project_id": "proj-123",
                      "created_at": "2025-10-17T10:30:00Z",
                      "updated_at": "2025-10-17T15:45:00Z"
                    }
                  ],
                  "payload": {
                    "pagination": {
                      "page": 1,
                      "total": 50,
                      "items_per_page": 10
                    },
                    "response_time_ms": 45.2
                  }
                }
                """
)
public class SktSafetyFilterGroupsRes {

    /**
     * 그룹 기본 정보 목록
     *
     * <p>조회된 SafetyFilter 그룹들의 기본 메타데이터 배열입니다.
     * 각 그룹은 ID, 이름, 생성/수정 이력 등을 포함합니다.</p>
     *
     * @apiNote 불용어 상세 정보는 포함되지 않습니다.
     */
    @JsonProperty("data")
    @Schema(
            description = "SafetyFilter 그룹 기본 정보 목록",
            example = """
                    [
                      {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "name": "욕설 필터 그룹",
                        "project_id": "proj-123",
                        "created_at": "2025-10-17T10:30:00Z",
                        "updated_at": "2025-10-17T15:45:00Z"
                      }
                    ]
                    """
    )
    private List<SktSafetyFilterGroupUpdateRes> data;

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
