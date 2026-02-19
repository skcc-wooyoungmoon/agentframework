package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Safety Filter 그룹 기본 정보 DTO
 *
 * <p>
 * Safety Filter 그룹의 기본 정보를 담고 있는 응답 DTO입니다.
 * 그룹 조회 시 반환되는 표준 그룹 데이터 구조입니다.
 * </p>
 *
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>id</strong>: 그룹 고유 ID (UUID)</li>
 * <li><strong>name</strong>: 그룹 이름</li>
 * <li><strong>project_id</strong>: 프로젝트 ID (선택)</li>
 * <li><strong>created_at</strong>: 생성 시각</li>
 * <li><strong>updated_at</strong>: 수정 시각</li>
 * <li><strong>created_by</strong>: 생성자 (선택)</li>
 * <li><strong>updated_by</strong>: 수정자 (선택)</li>
 * </ul>
 *
 * <h3>응답 예시:</h3>
 * 
 * <pre>
 * {
 *   "id": "550e8400-e29b-41d4-a716-446655440000",
 *   "name": "My Safety Group",
 *   "project_id": "project-123",
 *   "created_at": "2025-12-03T10:00:00Z",
 *   "updated_at": "2025-12-03T10:00:00Z",
 *   "created_by": "user@example.com",
 *   "updated_by": "user@example.com"
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-12-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Safety Filter 그룹 기본 정보", example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "name": "My Safety Group",
          "project_id": "project-123",
          "created_at": "2025-12-03T10:00:00Z",
          "updated_at": "2025-12-03T10:00:00Z",
          "created_by": "user@example.com",
          "updated_by": "user@example.com"
        }
        """)
public class SafetyFilterGroupRead {

    /**
     * 그룹 ID
     *
     * <p>
     * Safety Filter 그룹의 고유 식별자입니다 (UUID 형식).
     * </p>
     *
     * @apiNote 이 ID는 그룹 관련 모든 작업에서 기본 키로 사용됩니다.
     */
    @JsonProperty("id")
    @Schema(description = "그룹 ID (UUID 형식)", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String id;

    /**
     * 그룹 이름
     *
     * <p>
     * Safety Filter 그룹의 이름입니다.
     * 사용자가 그룹을 식별하고 관리하기 위한 고유한 이름입니다.
     * </p>
     *
     * @implNote 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("name")
    @Schema(description = "그룹 이름 (최대 255자)", example = "My Safety Group", required = true, maxLength = 255)
    private String name;

    /**
     * 프로젝트 ID
     *
     * <p>
     * 이 그룹이 속한 프로젝트의 식별자입니다.
     * 선택적 필드이며, 프로젝트 기반 관리가 필요한 경우 사용됩니다.
     * </p>
     *
     * @implNote null일 수 있으며, 이 경우 전역 그룹을 의미할 수 있습니다.
     */
    @JsonProperty("project_id")
    @Schema(description = "프로젝트 ID (선택 사항)", example = "project-123")
    private String projectId;

    /**
     * 생성 시각
     *
     * <p>
     * 그룹이 최초 생성된 날짜와 시간입니다 (ISO 8601 형식).
     * </p>
     *
     * @apiNote 서버에서 자동으로 설정되며, 클라이언트가 수정할 수 없습니다.
     */
    @JsonProperty("created_at")
    @Schema(description = "생성 시각 (ISO 8601 형식)", example = "2025-12-03T10:00:00Z", required = true, format = "date-time")
    private String createdAt;

    /**
     * 수정 시각
     *
     * <p>
     * 그룹이 마지막으로 수정된 날짜와 시간입니다 (ISO 8601 형식).
     * </p>
     *
     * @apiNote 그룹 정보가 변경될 때마다 자동으로 업데이트됩니다.
     */
    @JsonProperty("updated_at")
    @Schema(description = "수정 시각 (ISO 8601 형식)", example = "2025-12-03T10:00:00Z", required = true, format = "date-time")
    private String updatedAt;

    /**
     * 생성자
     *
     * <p>
     * 이 그룹을 생성한 사용자의 식별자입니다.
     * 선택적 필드이며, 보통 이메일이나 사용자명이 사용됩니다.
     * </p>
     *
     * @implNote null일 수 있으며, 인증 토큰에서 자동으로 추출됩니다.
     */
    @JsonProperty("created_by")
    @Schema(description = "생성자 (이메일 또는 사용자명)", example = "user@example.com")
    private String createdBy;

    /**
     * 수정자
     *
     * <p>
     * 이 그룹을 마지막으로 수정한 사용자의 식별자입니다.
     * 선택적 필드이며, 보통 이메일이나 사용자명이 사용됩니다.
     * </p>
     *
     * @implNote null일 수 있으며, 수정 시 인증 토큰에서 자동으로 추출됩니다.
     */
    @JsonProperty("updated_by")
    @Schema(description = "수정자 (이메일 또는 사용자명)", example = "user@example.com")
    private String updatedBy;
}
