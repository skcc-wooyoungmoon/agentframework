package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterGroupImportRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Safety Filter 그룹 Import 응답 DTO
 *
 * <p>
 * Safety Filter 그룹 Import 작업의 결과를 나타내는 응답 데이터 구조입니다.
 * Import 상태와 처리 결과를 포함합니다.
 * </p>
 *
 * <h3>응답 필드:</h3>
 * <ul>
 * <li><strong>id</strong>: Import된 그룹 ID</li>
 * <li><strong>status</strong>: Import 상태 (created, existing, conflict)</li>
 * <li><strong>message</strong>: 상태 메시지 (선택)</li>
 * <li><strong>group</strong>: 그룹 상세 데이터</li>
 * </ul>
 *
 * <h3>Import 상태 종류:</h3>
 * <ul>
 * <li><strong>created</strong>: 새로운 그룹이 생성됨</li>
 * <li><strong>existing</strong>: 기존 그룹이 이미 존재함</li>
 * <li><strong>conflict</strong>: 충돌 발생 (예: 이름 중복)</li>
 * </ul>
 *
 * <h3>응답 예시:</h3>
 * 
 * <pre>
 * {
 *   "id": "550e8400-e29b-41d4-a716-446655440000",
 *   "status": "created",
 *   "message": "Group imported successfully",
 *   "group": {
 *     "id": "550e8400-e29b-41d4-a716-446655440000",
 *     "name": "Imported Group",
 *     "created_at": "2025-12-03T10:00:00Z",
 *     "updated_at": "2025-12-03T10:00:00Z"
 *   }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-12-03
 * @version 1.0
 * @see SafetyFilterGroupImportRequest Import 요청 DTO
 * @see SafetyFilterGroupRead 그룹 상세 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Safety Filter 그룹 Import 응답 정보", example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "status": "created",
          "message": "Group imported successfully",
          "group": {
            "id": "550e8400-e29b-41d4-a716-446655440000",
            "name": "Imported Group"
          }
        }
        """)
public class SafetyFilterGroupImportResponse {

    /**
     * 그룹 ID
     *
     * <p>
     * Import된 그룹의 고유 식별자입니다 (UUID 형식).
     * </p>
     *
     * @apiNote Import 요청의 ID와 동일한 값이 반환됩니다.
     */
    @JsonProperty("id")
    @Schema(description = "Import된 그룹 ID (UUID 형식)", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String id;

    /**
     * Import 상태
     *
     * <p>
     * Import 작업의 최종 상태를 나타냅니다.
     * </p>
     *
     * <p>
     * 가능한 상태값:
     * </p>
     * <ul>
     * <li><strong>created</strong>: 새로운 그룹이 성공적으로 생성됨</li>
     * <li><strong>existing</strong>: 동일한 ID의 그룹이 이미 존재함</li>
     * <li><strong>conflict</strong>: Import 중 충돌 발생 (예: 이름 중복)</li>
     * </ul>
     *
     * @implNote 클라이언트는 이 값을 기반으로 후속 처리를 결정할 수 있습니다.
     */
    @JsonProperty("status")
    @Schema(description = "Import 상태 (created, existing, conflict)", example = "created", required = true, allowableValues = {
            "created", "existing", "conflict" })
    private String status;

    /**
     * 상태 메시지
     *
     * <p>
     * Import 상태에 대한 추가 설명 메시지입니다.
     * 선택적 필드이며, 상세한 정보가 필요한 경우에 제공됩니다.
     * </p>
     *
     * @implNote conflict 상태인 경우 충돌 원인이 메시지에 포함될 수 있습니다.
     */
    @JsonProperty("message")
    @Schema(description = "상태 메시지 (선택 사항)", example = "Group imported successfully")
    private String message;

    /**
     * 그룹 상세 데이터
     *
     * <p>
     * Import된 또는 기존 그룹의 상세 정보입니다.
     * SafetyFilterGroupRead 타입의 전체 그룹 데이터를 포함합니다.
     * </p>
     *
     * <p>
     * 포함되는 정보:
     * </p>
     * <ul>
     * <li>id: 그룹 ID</li>
     * <li>name: 그룹 이름</li>
     * <li>project_id: 프로젝트 ID</li>
     * <li>created_at: 생성 시각</li>
     * <li>updated_at: 수정 시각</li>
     * <li>created_by: 생성자</li>
     * <li>updated_by: 수정자</li>
     * </ul>
     *
     * @apiNote 그룹 데이터를 통해 Import된 그룹의 전체 정보를 확인할 수 있습니다.
     */
    @JsonProperty("group")
    @Schema(description = "Import된 그룹 상세 정보", required = true, implementation = SafetyFilterGroupRead.class)
    private SafetyFilterGroupRead group;
}
