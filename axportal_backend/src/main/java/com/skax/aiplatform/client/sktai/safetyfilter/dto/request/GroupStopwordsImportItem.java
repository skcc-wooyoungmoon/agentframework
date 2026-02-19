package com.skax.aiplatform.client.sktai.safetyfilter.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.GroupStopwordsBatchImportResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 그룹 Stopwords Import 아이템 DTO
 *
 * <p>
 * Safety Filter 그룹과 해당 그룹의 Stopwords를 일괄로 Import하기 위한 단일 아이템 데이터 구조입니다.
 * 배치 Import 요청 시 여러 아이템을 리스트로 전송합니다.
 * </p>
 *
 * <h3>필수 필드:</h3>
 * <ul>
 * <li><strong>group_id</strong>: Import할 그룹 ID (UUID)</li>
 * <li><strong>group_name</strong>: 그룹 이름</li>
 * </ul>
 *
 * <h3>선택 필드:</h3>
 * <ul>
 * <li><strong>stopwords</strong>: Stopword 리스트</li>
 * <li><strong>policy</strong>: 정책 설정</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * 
 * <pre>
 * GroupStopwordsImportItem item = GroupStopwordsImportItem.builder()
 *         .groupId("550e8400-e29b-41d4-a716-446655440000")
 *         .groupName("Offensive Words")
 *         .stopwords(Arrays.asList("badword1", "badword2"))
 *         .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-12-03
 * @version 1.0
 * @see GroupStopwordsBatchImportResponse Import 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "그룹 Stopwords Import 아이템", example = """
        {
          "group_id": "550e8400-e29b-41d4-a716-446655440000",
          "group_name": "Offensive Words",
          "stopwords": ["badword1", "badword2"],
          "policy": [...]
        }
        """)
public class GroupStopwordsImportItem {

    /**
     * 그룹 ID
     *
     * <p>
     * Import할 그룹의 고유 식별자입니다 (UUID 형식).
     * 이 ID로 그룹과 Stopwords가 연결됩니다.
     * </p>
     *
     * @apiNote UUID 형식을 따라야 하며, 중복 ID인 경우 기존 그룹에 Stopwords가 추가될 수 있습니다.
     */
    @JsonProperty("group_id")
    @Schema(description = "그룹 ID (UUID 형식)", example = "550e8400-e29b-41d4-a716-446655440000", required = true, format = "uuid")
    private String groupId;

    /**
     * 그룹 이름
     *
     * <p>
     * Import할 그룹의 이름입니다.
     * Safety Filter Stopwords를 조직화하고 관리하기 위한 그룹 이름입니다.
     * </p>
     *
     * @implNote 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("group_name")
    @Schema(description = "그룹 이름 (최대 255자)", example = "Offensive Words", required = true, maxLength = 255)
    private String groupName;

    /**
     * Stopwords 리스트
     *
     * <p>
     * 이 그룹에 추가할 Stopword들의 리스트입니다.
     * 각 Stopword는 Safety Filter로 등록되어 유해 콘텐츠를 차단하는 데 사용됩니다.
     * </p>
     *
     * <p>
     * Stopword 특징:
     * </p>
     * <ul>
     * <li>공백 포함 가능 (예: "bad word")</li>
     * <li>형태소 원형 사용 가능</li>
     * <li>대소문자 구분</li>
     * <li>중복 제거 자동 처리</li>
     * </ul>
     *
     * @implNote 선택적 필드이며, 빈 리스트도 허용됩니다.
     */
    @JsonProperty("stopwords")
    @Schema(description = "Stopword 리스트 (선택 사항)", example = "[\"badword1\", \"badword2\", \"offensive term\"]")
    private List<String> stopwords;

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
     * <li>policies: 세부 정책 목록 (user, group, role, token-exchange, regex)</li>
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
