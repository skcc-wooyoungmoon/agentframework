package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI SafetyFilter 그룹 조회 응답 DTO
 *
 * <p>SafetyFilter 그룹의 기본 정보를 담은 응답 데이터입니다.
 * 그룹의 메타데이터와 생성/수정 이력을 포함합니다.</p>
 *
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 이름, 프로젝트 ID</li>
 *   <li><strong>메타데이터</strong>: 생성일시, 수정일시</li>
 *   <li><strong>이력 정보</strong>: 생성자, 수정자</li>
 * </ul>
 *
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>그룹 생성 후 결과 반환</li>
 *   <li>그룹 상세 정보 조회</li>
 *   <li>그룹 수정 후 결과 반환</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupAggregate 그룹 + 불용어 집계 정보
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 기본 정보",
        example = """
                {
                  "id": "123e4567-e89b-12d3-a456-426614174000",
                  "name": "욕설 필터 그룹",
                  "project_id": "proj-123",
                  "created_at": "2025-10-17T10:30:00Z",
                  "updated_at": "2025-10-17T15:45:00Z",
                  "created_by": "user123",
                  "updated_by": "user456"
                }
                """
)
public class SktSafetyFilterGroupUpdateRes {

    /**
     * 그룹 고유 식별자
     *
     * <p>SafetyFilter 그룹의 UUID 형태의 고유 식별자입니다.
     * 시스템에서 자동 생성되며 변경할 수 없습니다.</p>
     */
    @JsonProperty("id")
    @Schema(
            description = "그룹 고유 식별자 (UUID 형태)",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String id;

    /**
     * 그룹 이름
     *
     * <p>사용자가 지정한 그룹의 이름입니다.
     * 그룹의 목적이나 포함된 필터의 특성을 나타냅니다.</p>
     */
    @JsonProperty("name")
    @Schema(
            description = "그룹 이름",
            example = "욕설 필터 그룹"
    )
    private String name;

    /**
     * 프로젝트 ID
     *
     * <p>이 그룹이 속한 프로젝트의 식별자입니다.
     * null인 경우 전역 그룹을 의미할 수 있습니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
            description = "그룹이 속한 프로젝트 ID (null일 수 있음)",
            example = "proj-123"
    )
    private String projectId;

    /**
     * 생성 일시
     *
     * <p>그룹이 처음 생성된 일시입니다.
     * ISO 8601 형식의 UTC 시간으로 표시됩니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
            description = "그룹 생성 일시 (ISO 8601 UTC)",
            example = "2025-10-17T10:30:00Z"
    )
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     *
     * <p>그룹이 마지막으로 수정된 일시입니다.
     * ISO 8601 형식의 UTC 시간으로 표시됩니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
            description = "그룹 수정 일시 (ISO 8601 UTC)",
            example = "2025-10-17T15:45:00Z"
    )
    private LocalDateTime updatedAt;

    /**
     * 생성자
     *
     * <p>그룹을 생성한 사용자의 식별자입니다.
     * null인 경우 시스템에서 생성되었음을 의미할 수 있습니다.</p>
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
     * <p>그룹을 마지막으로 수정한 사용자의 식별자입니다.
     * null인 경우 시스템에서 수정되었음을 의미할 수 있습니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(
            description = "그룹 수정자 (null일 수 있음)",
            example = "user456"
    )
    private String updatedBy;

}
