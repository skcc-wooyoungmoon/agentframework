package com.skax.aiplatform.client.sktai.knowledge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * SKTAI Knowledge Document 일괄 업데이트 요청 DTO
 * 
 * <p>Repository 내의 여러 Document들을 일괄적으로 업데이트하기 위한 요청 데이터 구조입니다.
 * Document의 활성화 상태, 메타데이터, 처리 설정 등을 대량으로 변경할 수 있습니다.</p>
 * 
 * <h3>일괄 업데이트 기능:</h3>
 * <ul>
 *   <li><strong>상태 변경</strong>: 여러 Document의 enable/disable 상태 일괄 변경</li>
 *   <li><strong>메타데이터 업데이트</strong>: Document 메타데이터 일괄 수정</li>
 *   <li><strong>처리 설정 변경</strong>: 로더, 스플리터 등 처리 파라미터 변경</li>
 *   <li><strong>재인덱싱 트리거</strong>: 변경된 Document들의 자동 재인덱싱</li>
 * </ul>
 * 
 * <h3>선택 기준:</h3>
 * <ul>
 *   <li><strong>Document ID 목록</strong>: 특정 Document들만 선택</li>
 *   <li><strong>필터 조건</strong>: 조건에 맞는 Document들 자동 선택</li>
 *   <li><strong>전체 선택</strong>: Repository의 모든 Document 대상</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * DocumentUpdateRequest updateRequest = DocumentUpdateRequest.builder()
 *     .documentIds(Arrays.asList("doc-1", "doc-2", "doc-3"))
 *     .enabled(true)
 *     .metadata(Map.of("category", "policy", "version", "2.0"))
 *     .triggerReindexing(true)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI Knowledge Document 일괄 업데이트 요청 정보")
public class DocumentUpdateRequest {

    /**
     * 업데이트할 Document ID 목록 (선택적)
     * 
     * <p>특정 Document들만 업데이트하려는 경우 사용합니다.
     * 지정하지 않으면 필터 조건이나 전체 선택에 따라 대상이 결정됩니다.</p>
     */
    @JsonProperty("document_ids")
    @Schema(description = "업데이트할 Document ID 목록")
    private List<String> documentIds;

    /**
     * Document 활성화 상태 (선택적)
     * 
     * <p>Document의 enable/disable 상태를 변경합니다.
     * true: 활성화 (검색 가능), false: 비활성화 (검색 제외)</p>
     */
    @JsonProperty("enabled")
    @Schema(description = "Document 활성화 상태", example = "true")
    private Boolean enabled;

    /**
     * 메타데이터 업데이트 (선택적)
     * 
     * <p>Document에 추가할 메타데이터입니다.
     * 기존 메타데이터와 병합되며, 같은 키가 있으면 덮어씁니다.</p>
     */
    @JsonProperty("metadata")
    @Schema(description = "추가/수정할 메타데이터")
    private Map<String, Object> metadata;

    /**
     * 제거할 메타데이터 키 목록 (선택적)
     * 
     * <p>Document에서 제거할 메타데이터 키들의 목록입니다.</p>
     */
    @JsonProperty("remove_metadata_keys")
    @Schema(description = "제거할 메타데이터 키 목록")
    private List<String> removeMetadataKeys;

    /**
     * 로더 유형 변경 (선택적)
     * 
     * <p>Document의 로더를 변경합니다.
     * 변경 시 재인덱싱이 필요할 수 있습니다.</p>
     */
    @JsonProperty("loader")
    @Schema(description = "변경할 로더 유형", allowableValues = {"Default", "DataIngestionTool", "CustomLoader"})
    private String loader;

    /**
     * 스플리터 유형 변경 (선택적)
     * 
     * <p>Document의 스플리터를 변경합니다.
     * 변경 시 재인덱싱이 필요할 수 있습니다.</p>
     */
    @JsonProperty("splitter")
    @Schema(description = "변경할 스플리터 유형", allowableValues = {"RecursiveCharacter", "Character", "Semantic", "CustomSplitter", "NotSplit"})
    private String splitter;

    /**
     * 청크 크기 변경 (선택적)
     */
    @JsonProperty("chunk_size")
    @Schema(description = "변경할 청크 크기", example = "1000")
    private Integer chunkSize;

    /**
     * 청크 오버랩 변경 (선택적)
     */
    @JsonProperty("chunk_overlap")
    @Schema(description = "변경할 청크 오버랩", example = "50")
    private Integer chunkOverlap;

    /**
     * 분할 구분자 변경 (선택적)
     */
    @JsonProperty("separator")
    @Schema(description = "변경할 분할 구분자", example = "\\n")
    private String separator;

    /**
     * Tool ID (선택적)
     * 
     * <p>loader가 "DataIngestionTool"인 경우 사용할 Tool의 ID입니다.</p>
     */
    @JsonProperty("tool_id")
    @Schema(description = "Tool ID (loader가 DataIngestionTool인 경우)")
    private String toolId;

    /**
     * 사용자 정의 로더 ID (선택적)
     */
    @JsonProperty("custom_loader_id")
    @Schema(description = "사용자 정의 로더 ID")
    private String customLoaderId;

    /**
     * 사용자 정의 스플리터 ID (선택적)
     */
    @JsonProperty("custom_splitter_id")
    @Schema(description = "사용자 정의 스플리터 ID")
    private String customSplitterId;

    /**
     * 재인덱싱 트리거 여부 (선택적)
     * 
     * <p>업데이트 후 변경된 Document들을 자동으로 재인덱싱할지 여부입니다.
     * true인 경우 업데이트 즉시 재인덱싱이 시작됩니다.</p>
     */
    @JsonProperty("trigger_reindexing")
    @Schema(description = "업데이트 후 자동 재인덱싱 여부", example = "true")
    private Boolean triggerReindexing;

    /**
     * 필터 조건 (선택적)
     * 
     * <p>특정 조건에 맞는 Document들만 업데이트하고 싶을 때 사용합니다.
     * documentIds가 지정되지 않은 경우에만 적용됩니다.</p>
     */
    @JsonProperty("filter")
    @Schema(description = "Document 선택을 위한 필터 조건")
    private DocumentFilter filter;

    /**
     * 전체 적용 여부 (선택적)
     * 
     * <p>Repository의 모든 Document에 적용할지 여부입니다.
     * true인 경우 documentIds와 filter는 무시됩니다.</p>
     */
    @JsonProperty("apply_to_all")
    @Schema(description = "Repository의 모든 Document에 적용 여부", example = "false")
    private Boolean applyToAll;

    /**
     * 배치 크기 (선택적)
     * 
     * <p>대량 업데이트 시 한 번에 처리할 Document 개수입니다.</p>
     */
    @JsonProperty("batch_size")
    @Schema(description = "배치 처리 크기", example = "100")
    private Integer batchSize;

    /**
     * 업데이트 수행자 정보 (선택적)
     */
    @JsonProperty("updated_by")
    @Schema(description = "업데이트 수행자 정보", example = "admin@example.com")
    private String updatedBy;

    /**
     * 업데이트 사유 (선택적)
     */
    @JsonProperty("update_reason")
    @Schema(description = "업데이트 사유", example = "정책 변경에 따른 메타데이터 업데이트")
    private String updateReason;

    /**
     * Document 필터 조건 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Document 필터 조건")
    public static class DocumentFilter {

        /**
         * 파일명 패턴
         */
        @JsonProperty("filename_pattern")
        @Schema(description = "파일명 패턴 (glob 스타일)", example = "*.pdf")
        private String filenamePattern;

        /**
         * 파일 크기 범위 (최소)
         */
        @JsonProperty("min_file_size")
        @Schema(description = "최소 파일 크기 (바이트)", example = "1024")
        private Long minFileSize;

        /**
         * 파일 크기 범위 (최대)
         */
        @JsonProperty("max_file_size")
        @Schema(description = "최대 파일 크기 (바이트)", example = "10485760")
        private Long maxFileSize;

        /**
         * 생성일 범위 (시작)
         */
        @JsonProperty("created_after")
        @Schema(description = "생성일 이후 (ISO 8601)", example = "2024-01-01T00:00:00Z")
        private String createdAfter;

        /**
         * 생성일 범위 (종료)
         */
        @JsonProperty("created_before")
        @Schema(description = "생성일 이전 (ISO 8601)", example = "2024-12-31T23:59:59Z")
        private String createdBefore;

        /**
         * 메타데이터 조건
         */
        @JsonProperty("metadata_conditions")
        @Schema(description = "메타데이터 조건")
        private Map<String, Object> metadataConditions;

        /**
         * 현재 상태 필터
         */
        @JsonProperty("current_status")
        @Schema(description = "현재 Document 상태", allowableValues = {"enabled", "disabled", "all"})
        private String currentStatus;
    }
}
