package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 그룹 Stopwords Import 오류 상세 DTO
 *
 * <p>
 * 배치 Import 작업에서 개별 아이템의 실패 원인을 나타내는 오류 정보입니다.
 * 실패한 그룹 ID와 오류 메시지를 포함합니다.
 * </p>
 *
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>group_id</strong>: 실패한 그룹의 ID (UUID)</li>
 * <li><strong>error_message</strong>: 실패 원인 설명</li>
 * </ul>
 *
 * <h3>일반적인 오류 메시지:</h3>
 * <ul>
 * <li>"Duplicate group name": 그룹 이름 중복</li>
 * <li>"Invalid policy format": 정책 형식 오류</li>
 * <li>"Group not found": 그룹을 찾을 수 없음</li>
 * <li>"Invalid UUID format": UUID 형식 오류</li>
 * <li>"Stopwords validation failed": Stopwords 유효성 검증 실패</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * 
 * <pre>
 * {
 *   "group_id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
 *   "error_message": "Duplicate group name"
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-12-03
 * @version 1.0
 * @see GroupStopwordsBatchImportResponse 배치 Import 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "그룹 Stopwords Import 오류 상세 정보", example = """
        {
          "group_id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
          "error_message": "Duplicate group name"
        }
        """)
public class GroupStopwordsImportError {

    /**
     * 실패한 그룹 ID
     *
     * <p>
     * Import에 실패한 그룹의 고유 식별자입니다 (UUID 형식).
     * 재시도 시 이 ID를 사용하여 특정 아이템만 재처리할 수 있습니다.
     * </p>
     *
     * @apiNote UUID 형식 (8-4-4-4-12 hex digits)을 따릅니다.
     * @implNote 예: "550e8400-e29b-41d4-a716-446655440000"
     */
    @JsonProperty("group_id")
    @Schema(description = "실패한 그룹 ID (UUID 형식)", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true, format = "uuid", pattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    private String groupId;

    /**
     * 오류 메시지
     *
     * <p>
     * Import 실패의 구체적인 원인을 설명하는 메시지입니다.
     * 사용자가 문제를 이해하고 수정할 수 있도록 명확한 설명을 제공합니다.
     * </p>
     *
     * <p>
     * 일반적인 오류 메시지:
     * </p>
     * <ul>
     * <li>"Duplicate group name": 동일한 이름의 그룹이 이미 존재</li>
     * <li>"Invalid policy format": 정책 데이터 형식이 올바르지 않음</li>
     * <li>"Group not found": 업데이트 대상 그룹을 찾을 수 없음</li>
     * <li>"Invalid UUID format": 제공된 UUID가 형식에 맞지 않음</li>
     * <li>"Stopwords validation failed": Stopwords 데이터 검증 실패</li>
     * <li>"Permission denied": 그룹에 대한 접근 권한 없음</li>
     * </ul>
     *
     * @implNote 메시지는 클라이언트에게 그대로 표시될 수 있으므로, 민감한 정보를 포함하지 않습니다.
     */
    @JsonProperty("error_message")
    @Schema(description = "오류 메시지 (실패 원인 설명)", example = "Duplicate group name", required = true, maxLength = 500)
    private String errorMessage;
}
