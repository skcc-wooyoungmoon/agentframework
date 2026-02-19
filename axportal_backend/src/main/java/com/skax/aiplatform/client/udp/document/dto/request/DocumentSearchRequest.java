package com.skax.aiplatform.client.udp.document.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UDP 문서 검색 요청 DTO
 * 
 * <p>UDP 시스템에서 데이터셋 내 문서를 검색하기 위한 요청 데이터 구조입니다.</p>
 *
 * <h3>검색 기능:</h3>
 * <ul>
 *   <li><strong>데이터셋 필터링</strong>: 특정 데이터셋 내에서만 검색</li>
 *   <li><strong>날짜 범위 검색</strong>: 문서 수정일 기준으로 검색</li>
 *   <li><strong>키워드 검색</strong>: 문서 내용에서 키워드 검색</li>
 *   <li><strong>페이징</strong>: 검색 결과 페이징 처리</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * DocumentSearchRequest request = DocumentSearchRequest.builder()
 *     .datasetCd("rgl")
 *     .docModStart("20240101")
 *     .docModEnd("20241231")
 *     .searchWord("규정")
 *     .countPerPage(20L)
 *     .page(1L)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "UDP 문서 검색 요청 정보",
    example = """
        {
          "dataset_cd": "rgl",
          "doc_mod_start": "19000101",
          "doc_mod_end": "99991231",
          "origin_metadata_yn": "Y",
          "search_word": "규정",
          "doc_uuid": "",
          "count_per_page": 20,
          "page": 1
        }
        """
)
public class DocumentSearchRequest {

    /**
     * 데이터셋코드
     * 
     * <p>데이터셋조회 API로 획득한 데이터셋 코드입니다.</p>
     */
    @JsonProperty("dataset_cd")
    @NotNull(message = "데이터셋코드는 필수입니다")
    @Schema(
        description = "데이터셋코드 (데이터셋조회 API로 획득, 2자)",
        example = "rgl",
        required = true
    )
    private String datasetCd;

    /**
     * 문서수정시작일
     * 
     * <p>문서가 생성/수정된 날짜의 시작일 (YYYYMMDD)입니다.
     * 전체 조회 시 19000101 입력</p>
     */
    @JsonProperty("doc_mod_start")
    @NotNull(message = "문서수정시작일은 필수입니다")
    @Schema(
        description = "문서수정시작일 (YYYYMMDD, 전체 조회 시 19000101 입력, 10자)",
        example = "19000101",
        required = true,
        defaultValue = "19000101"
    )
    @Builder.Default
    private String docModStart = "19000101";

    /**
     * 문서수정종료일
     * 
     * <p>문서가 생성/수정된 날짜의 종료일 (YYYYMMDD)입니다.
     * 전체 조회 시 99991231 입력</p>
     */
    @JsonProperty("doc_mod_end")
    @NotNull(message = "문서수정종료일은 필수입니다")
    @Schema(
        description = "문서수정종료일 (YYYYMMDD, 전체 조회 시 99991231 입력, 8자)",
        example = "99991231",
        required = true,
        defaultValue = "99991231"
    )
    @Builder.Default
    private String docModEnd = "99991231";

    /**
     * 원천메타데이터포함여부
     * 
     * <p>수집원천에서 추출된 메타데이터를 response에 포함할지 결정하는 Flag입니다.
     * Y/N 설정. N인 경우 메타정보 제외</p>
     */
    @JsonProperty("origin_metadata_yn")
    @Schema(
        description = "원천메타데이터포함여부 (Y/N)",
        example = "Y",
        defaultValue = "Y"
    )
    @Builder.Default
    private String originMetadataYn = "Y";

    /**
     * 검색어
     * 
     * <p>검색할 키워드입니다.</p>
     */
    @JsonProperty("search_word")
    @Size(min = 2, message = "검색어는 최소 2자 이상이어야 합니다")
    @Schema(
        description = "검색어 (2자 이상)",
        example = "규정"
    )
    private String searchWord;

    /**
     * 문서UUID
     * 
     * <p>특정 문서 조회 시 입력하는 UUID입니다.</p>
     */
    @JsonProperty("doc_uuid")
    @Schema(
        description = "문서UUID (특정 문서 조회 시 입력)",
        example = "doc03_sbrgl20060402003600000009"
    )
    private String docUuid;

    /**
     * 페이지당표시수
     * 
     * <p>페이지당 표시할 수입니다. 입력값 없으면 20, 최대값 100</p>
     */
    @JsonProperty("count_per_page")
    @Schema(
        description = "페이지당표시수 (기본값: 20, 최대값: 100)",
        example = "20",
        defaultValue = "20"
    )
    @Builder.Default
    private Long countPerPage = 20L;

    /**
     * 페이지
     * 
     * <p>조회 페이지번호입니다. 입력값 없으면 1</p>
     */
    @JsonProperty("page")
    @Schema(
        description = "페이지 (기본값: 1)",
        example = "1",
        defaultValue = "1"
    )
    @Builder.Default
    private Long page = 1L;
}