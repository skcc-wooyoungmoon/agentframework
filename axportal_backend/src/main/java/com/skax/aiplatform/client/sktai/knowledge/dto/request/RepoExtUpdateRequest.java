package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * SKTAI Knowledge External Repository 업데이트 요청 DTO
 * 
 * <p>기존 External Repository의 설정을 수정하기 위한 요청 데이터 구조입니다.
 * 연결 정보, 인증 설정, 동기화 옵션 등을 필요에 따라 부분적으로 업데이트할 수 있습니다.</p>
 * 
 * <h3>업데이트 가능한 항목:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 이름, 설명, 활성화 상태</li>
 *   <li><strong>연결 설정</strong>: 엔드포인트, 인증 정보, 연결 옵션</li>
 *   <li><strong>데이터 매핑</strong>: 스키마 매핑, 필드 변환 규칙</li>
 *   <li><strong>동기화 설정</strong>: 동기화 주기, 배치 크기, 자동 동기화</li>
 *   <li><strong>메타데이터</strong>: 분류, 태그, 관리 정보</li>
 * </ul>
 * 
 * <h3>업데이트 방식:</h3>
 * <ul>
 *   <li><strong>부분 업데이트</strong>: 지정된 필드만 변경하고 나머지는 기존 값 유지</li>
 *   <li><strong>병합 업데이트</strong>: Map 타입 필드는 기존 데이터와 병합</li>
 *   <li><strong>전체 교체</strong>: 특정 필드의 전체 값을 새로운 값으로 교체</li>
 * </ul>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li><strong>인증 정보 변경</strong>: 변경 후 연결 테스트를 권장합니다</li>
 *   <li><strong>매핑 설정 변경</strong>: 기존 동기화된 데이터에 영향을 줄 수 있습니다</li>
 *   <li><strong>엔드포인트 변경</strong>: 완전히 다른 시스템으로 변경 시 재동기화가 필요합니다</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoExtUpdateRequest updateRequest = RepoExtUpdateRequest.builder()
 *     .description("업데이트된 인사 지식 베이스 설명")
 *     .enabled(true)
 *     .authConfig(Map.of(
 *         "type", "api_key",
 *         "api_key", "new_api_key_value"
 *     ))
 *     .syncConfig(Map.of(
 *         "sync_interval", "30m",
 *         "enable_auto_sync", true
 *     ))
 *     .testAfterUpdate(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoExtInfo External Repository 업데이트 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge External Repository 업데이트 요청 정보")
public class RepoExtUpdateRequest {

    /**
     * External Repository 이름 (선택적)
     * 
     * <p>변경할 External Repository의 새로운 이름입니다.
     * 지정하지 않으면 기존 이름이 유지됩니다.</p>
     */
    @JsonProperty("name")
    @Schema(description = "변경할 External Repository 이름", example = "Updated HR Knowledge Base")
    private String name;

    /**
     * External Repository 설명 (선택적)
     * 
     * <p>변경할 External Repository의 새로운 설명입니다.
     * 지정하지 않으면 기존 설명이 유지됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(description = "변경할 External Repository 설명", example = "업데이트된 인사 지식 베이스 설명")
    private String description;

    /**
     * 검색 스크립트 (선택적)
     * 
     * <p>변경할 검색 스크립트입니다.
     * 지정하지 않으면 기존 스크립트가 유지됩니다.</p>
     */
    @JsonProperty("script")
    @Schema(description = "변경할 검색 스크립트 (Python)")
    private String script;

    /**
     * 인덱스명 (선택적)
     * 
     * <p>변경할 Vector DB 인덱스명입니다.
     * 지정하지 않으면 기존 인덱스명이 유지됩니다.</p>
     */
    @JsonProperty("index_name")
    @Schema(description = "변경할 인덱스명", example = "my_custom_index")
    private String indexName;

    /**
     * 엔드포인트 URL (선택적)
     * 
     * <p>변경할 외부 시스템의 엔드포인트 URL입니다.
     * <strong>주의:</strong> 엔드포인트 변경 시 연결 테스트를 권장합니다.</p>
     */
    @JsonProperty("endpoint")
    @Schema(description = "변경할 엔드포인트 URL", example = "https://new-hr-kb.company.com/api/v2")
    private String endpoint;

    /**
     * 인증 설정 업데이트 (선택적)
     * 
     * <p>변경할 인증 정보입니다.
     * 기존 인증 설정과 병합되며, 같은 키가 있으면 새로운 값으로 덮어씁니다.</p>
     * 
     * <h4>부분 업데이트 예시:</h4>
     * <pre>
     * // 기존: {"type": "bearer_token", "token": "old_token", "expires_in": 3600}
     * // 업데이트: {"token": "new_token"}
     * // 결과: {"type": "bearer_token", "token": "new_token", "expires_in": 3600}
     * </pre>
     */
    @JsonProperty("auth_config")
    @Schema(description = "인증 설정 업데이트 (기존 설정과 병합)")
    private Map<String, Object> authConfig;

    /**
     * 데이터 매핑 설정 업데이트 (선택적)
     * 
     * <p>변경할 데이터 매핑 정보입니다.
     * 기존 매핑 설정과 병합되며, 매핑 변경 시 재동기화를 고려해야 합니다.</p>
     */
    @JsonProperty("data_mapping")
    @Schema(description = "데이터 매핑 설정 업데이트")
    private Map<String, Object> dataMapping;

    /**
     * 동기화 설정 업데이트 (선택적)
     * 
     * <p>변경할 동기화 설정입니다.
     * 기존 동기화 설정과 병합되며, 즉시 적용됩니다.</p>
     * 
     * <h4>동기화 설정 업데이트 예시:</h4>
     * <pre>
     * {
     *   "sync_interval": "30m",        // 30분마다 동기화
     *   "enable_auto_sync": true,      // 자동 동기화 활성화
     *   "batch_size": 200              // 배치 크기 증가
     * }
     * </pre>
     */
    @JsonProperty("sync_config")
    @Schema(description = "동기화 설정 업데이트")
    private Map<String, Object> syncConfig;

    /**
     * 연결 옵션 업데이트 (선택적)
     * 
     * <p>변경할 연결 옵션들입니다.
     * 기존 연결 옵션과 병합됩니다.</p>
     */
    @JsonProperty("connection_options")
    @Schema(description = "연결 옵션 업데이트")
    private Map<String, Object> connectionOptions;

    /**
     * 활성화 상태 (선택적)
     * 
     * <p>External Repository의 활성화 상태를 변경합니다.
     * false로 변경 시 즉시 동기화가 중단되고 검색에서 제외됩니다.</p>
     */
    @JsonProperty("enabled")
    @Schema(description = "활성화 상태 변경", example = "true")
    private Boolean enabled;

    /**
     * 우선순위 (선택적)
     * 
     * <p>검색 시 외부 Repository들 간의 우선순위를 변경합니다.
     * 낮은 숫자가 높은 우선순위를 가집니다.</p>
     */
    @JsonProperty("priority")
    @Schema(description = "검색 우선순위 변경", example = "2")
    private Integer priority;

    /**
     * 메타데이터 업데이트 (선택적)
     * 
     * <p>External Repository의 메타데이터를 업데이트합니다.
     * 기존 메타데이터와 병합됩니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(description = "메타데이터 업데이트")
    private Map<String, Object> metadata;

    /**
     * 제거할 메타데이터 키 목록 (선택적)
     * 
     * <p>External Repository에서 제거할 메타데이터 키들의 목록입니다.</p>
     */
    @JsonProperty("remove_metadata_keys")
    @Schema(description = "제거할 메타데이터 키 목록")
    private java.util.List<String> removeMetadataKeys;

    /**
     * 업데이트 후 연결 테스트 여부 (선택적)
     * 
     * <p>업데이트 완료 후 연결 테스트를 자동으로 수행할지 여부입니다.
     * 인증 정보나 엔드포인트 변경 시 권장됩니다.</p>
     */
    @JsonProperty("test_after_update")
    @Schema(description = "업데이트 후 자동 연결 테스트 여부", example = "true")
    private Boolean testAfterUpdate;

    /**
     * 즉시 동기화 여부 (선택적)
     * 
     * <p>업데이트 완료 후 즉시 동기화를 수행할지 여부입니다.
     * 설정 변경 사항을 바로 반영하고 싶을 때 사용합니다.</p>
     */
    @JsonProperty("trigger_sync")
    @Schema(description = "업데이트 후 즉시 동기화 수행 여부", example = "false")
    private Boolean triggerSync;

    /**
     * 설정 백업 여부 (선택적)
     * 
     * <p>업데이트 전 현재 설정을 백업할지 여부입니다.
     * 중요한 변경 사항의 경우 롤백을 위해 권장됩니다.</p>
     */
    @JsonProperty("backup_current_config")
    @Schema(description = "업데이트 전 현재 설정 백업 여부", example = "true")
    private Boolean backupCurrentConfig;

    /**
     * 업데이트 수행자 정보 (선택적)
     * 
     * <p>External Repository를 업데이트하는 사용자의 식별 정보입니다.
     * 지정하지 않으면 현재 인증된 사용자 정보가 사용됩니다.</p>
     */
    @JsonProperty("updated_by")
    @Schema(description = "업데이트 수행자 정보", example = "admin@example.com")
    private String updatedBy;

    /**
     * 업데이트 사유 (선택적)
     * 
     * <p>External Repository 업데이트의 사유나 목적을 설명하는 텍스트입니다.
     * 변경 이력 관리를 위해 권장됩니다.</p>
     */
    @JsonProperty("update_reason")
    @Schema(description = "업데이트 사유", example = "외부 시스템 API 버전 업그레이드에 따른 엔드포인트 변경")
    private String updateReason;

    /**
     * 업데이트 영향도 (선택적)
     * 
     * <p>이번 업데이트가 시스템에 미칠 영향의 정도입니다.
     * 운영 관리를 위한 참고 정보입니다.</p>
     */
    @JsonProperty("impact_level")
    @Schema(
        description = "업데이트 영향도",
        example = "MEDIUM",
        allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}
    )
    private String impactLevel;
}
