package com.skax.aiplatform.client.sktai.safetyfilter.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFilterGroupImportResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Safety Filter 그룹 Import 요청 DTO
 *
 * <p>
 * Safety Filter 그룹을 일괄로 Import하기 위한 요청 데이터 구조입니다.
 * Export된 그룹 데이터를 다시 Import할 때 사용됩니다.
 * </p>
 *
 * <h3>필수 필드:</h3>
 * <ul>
 * <li><strong>id</strong>: Import할 그룹 ID (UUID)</li>
 * <li><strong>name</strong>: 그룹 이름</li>
 * </ul>
 *
 * <h3>선택 필드:</h3>
 * <ul>
 * <li><strong>policy</strong>: 정책 설정 (PolicyPayload 타입)</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * 
 * <pre>
 * SafetyFilterGroupImportRequest request = SafetyFilterGroupImportRequest.builder()
 *         .id("550e8400-e29b-41d4-a716-446655440000")
 *         .name("Imported Group")
 *         .policy(policyData)
 *         .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-12-03
 * @version 1.0
 * @see SafetyFilterGroupImportResponse Import 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Safety Filter 그룹 Import 요청 정보", example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "name": "Imported Group",
          "policy": [...]
        }
        """)
public class SafetyFilterGroupImportRequest {

    /**
     * 그룹 ID
     *
     * <p>
     * Import할 그룹의 고유 식별자입니다 (UUID 형식).
     * Export된 데이터의 원본 ID를 유지합니다.
     * </p>
     *
     * @apiNote UUID 형식을 따라야 하며, 중복 ID인 경우 Import 동작이 달라질 수 있습니다.
     */
    @JsonProperty("id")
    @Schema(description = "그룹 ID (UUID 형식)", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String id;

    /**
     * 그룹 이름
     *
     * <p>
     * Import할 그룹의 이름입니다.
     * Safety Filter들을 조직화하고 관리하기 위한 그룹 이름입니다.
     * </p>
     *
     * @implNote 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("name")
    @Schema(description = "그룹 이름 (최대 255자)", example = "Imported Group", required = true, maxLength = 255)
    private String name;

    /**
     * 정책 설정
     *
     * <p>
     * Safety Filter 그룹에 적용할 정책 설정입니다.
     * 선택적 필드이며, 생략 가능합니다.
     * </p>
     *
     * <p>
     * 정책 설정은 다음을 포함할 수 있습니다:
     * </p>
     * <ul>
     * <li>scopes: 권한 범위 (GET, POST, PUT, DELETE)</li>
     * <li>policies: 세부 정책 목록</li>
     * <li>logic: 정책 논리 (POSITIVE, NEGATIVE)</li>
     * <li>decision_strategy: 결정 전략 (AFFIRMATIVE, CONSENSUS, UNANIMOUS)</li>
     * <li>cascade: 계단식 적용 여부</li>
     * </ul>
     *
     * @implNote PolicyPayload 타입으로, List 형태의 복잡한 구조를 가집니다.
     */
    @JsonProperty("policy")
    @Schema(description = "정책 설정 (선택 사항)", implementation = Object.class, example = """
            [{
              "scopes": ["GET", "POST", "PUT", "DELETE"],
              "policies": [{
                "type": "user",
                "logic": "POSITIVE",
                "names": ["admin"]
              }],
              "logic": "POSITIVE",
              "decision_strategy": "UNANIMOUS",
              "cascade": false
            }]
            """)
    private List<Object> policy;
}
