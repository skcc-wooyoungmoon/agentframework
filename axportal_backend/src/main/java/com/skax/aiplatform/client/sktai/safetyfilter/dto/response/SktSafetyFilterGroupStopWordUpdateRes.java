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
 * SKT AI SafetyFilter 그룹 업데이트 작업 결과 응답 DTO
 *
 * <p>SafetyFilter 그룹의 불용어 업데이트 작업 (추가/교체/삭제) 결과를 담는 응답입니다.
 * 작업의 상세한 통계와 처리 시간을 포함합니다.</p>
 *
 * <h3>제공하는 통계:</h3>
 * <ul>
 *   <li><strong>생성 개수</strong>: 새로 추가된 불용어 수</li>
 *   <li><strong>삭제 개수</strong>: 제거된 불용어 수</li>
 *   <li><strong>최종 개수</strong>: 업데이트 후 총 불용어 수</li>
 *   <li><strong>처리 시간</strong>: 작업 소요 시간</li>
 * </ul>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>그룹 불용어 일괄 교체 후 결과 확인</li>
 *   <li>불용어 추가 작업 후 통계 조회</li>
 *   <li>불용어 삭제 작업 후 변경사항 확인</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 업데이트 작업 결과",
        example = """
                {
                  "group_id": "123e4567-e89b-12d3-a456-426614174000",
                  "group_name": "욕설 필터 그룹",
                  "created_count": 5,
                  "deleted_count": 2,
                  "total_count": 13,
                  "message": "그룹 불용어 업데이트가 성공적으로 완료되었습니다",
                  "stopwords": ["욕설1", "욕설2", "욕설3"],
                  "created_at": "2025-11-10T00:28:28.440Z",
                  "updated_at": "2025-11-10T00:28:28.440Z",
                  "created_by": "admin-user",
                  "updated_by": "admin-user",
                  "response_time_ms": 234.5
                }
                """
)
public class SktSafetyFilterGroupStopWordUpdateRes {

    /**
     * 업데이트된 그룹 ID
     *
     * <p>업데이트 작업이 수행된 SafetyFilter 그룹의 고유 식별자입니다.</p>
     */
    @JsonProperty("group_id")
    @Schema(
            description = "업데이트된 그룹의 고유 식별자",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String groupId;

    /**
     * 업데이트된 그룹 이름
     *
     * <p>업데이트 작업이 수행된 그룹의 이름입니다.
     * 작업 확인 및 로깅 목적으로 제공됩니다.</p>
     */
    @JsonProperty("group_name")
    @Schema(
            description = "업데이트된 그룹 이름",
            example = "욕설 필터 그룹"
    )
    private String groupName;

    /**
     * 생성된 불용어 개수
     *
     * <p>이번 업데이트 작업으로 새로 생성된 불용어의 개수입니다.
     * 추가 작업에서는 양수, 삭제 작업에서는 0입니다.</p>
     */
    @JsonProperty("created_count")
    @Schema(
            description = "새로 생성된 불용어 개수",
            example = "5"
    )
    private Integer createdCount;

    /**
     * 삭제된 불용어 개수
     *
     * <p>이번 업데이트 작업으로 삭제된 불용어의 개수입니다.
     * 삭제 작업에서는 양수, 추가 작업에서는 0입니다.</p>
     */
    @JsonProperty("deleted_count")
    @Schema(
            description = "삭제된 불용어 개수",
            example = "2"
    )
    private Integer deletedCount;

    /**
     * 업데이트 후 총 불용어 개수
     *
     * <p>업데이트 작업 완료 후 그룹에 속한 전체 불용어의 개수입니다.
     * (기존 개수 - 삭제 개수 + 생성 개수)와 같습니다.</p>
     */
    @JsonProperty("total_count")
    @Schema(
            description = "업데이트 후 그룹의 총 불용어 개수",
            example = "13"
    )
    private Integer totalCount;

    /**
     * 작업 결과 메시지
     *
     * <p>업데이트 작업의 결과를 요약한 메시지입니다.
     * 성공/실패 상태와 간단한 설명을 포함합니다.</p>
     */
    @JsonProperty("message")
    @Schema(
            description = "작업 결과 요약 메시지",
            example = "그룹 불용어 업데이트가 성공적으로 완료되었습니다"
    )
    private String message;

    /**
     * 불용어 목록
     *
     * <p>업데이트된 그룹에 속한 모든 불용어의 목록입니다.</p>
     */
    @JsonProperty("stopwords")
    @Schema(
            description = "그룹의 불용어 목록",
            example = "[\"욕설1\", \"욕설2\", \"욕설3\"]"
    )
    private List<String> stopwords;

    /**
     * 생성 일시
     *
     * <p>그룹이 생성된 날짜와 시간(ISO 8601 형식)입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
            description = "그룹 생성 일시 (ISO 8601 형식)",
            example = "2025-11-10T00:28:28.440Z"
    )
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     *
     * <p>그룹이 마지막으로 수정된 날짜와 시간(ISO 8601 형식)입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
            description = "그룹 수정 일시 (ISO 8601 형식)",
            example = "2025-11-10T00:28:28.440Z"
    )
    private LocalDateTime updatedAt;

    /**
     * 생성자
     *
     * <p>그룹을 생성한 사용자의 식별자입니다.</p>
     */
    @JsonProperty("created_by")
    @Schema(
            description = "그룹 생성자 식별자",
            example = "admin-user"
    )
    private String createdBy;

    /**
     * 수정자
     *
     * <p>그룹을 마지막으로 수정한 사용자의 식별자입니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
            description = "그룹 수정자 식별자",
            example = "admin-user"
    )
    private String updatedBy;

    /**
     * 응답 처리 시간
     *
     * <p>업데이트 작업의 총 소요 시간(밀리초)입니다.
     * 성능 모니터링 및 최적화에 활용됩니다.</p>
     */
    @JsonProperty("response_time_ms")
    @Schema(
            description = "작업 처리 시간 (밀리초)",
            example = "234.5"
    )
    private Double responseTimeMs;

}
