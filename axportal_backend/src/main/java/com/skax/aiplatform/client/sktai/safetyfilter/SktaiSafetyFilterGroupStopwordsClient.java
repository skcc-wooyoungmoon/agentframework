package com.skax.aiplatform.client.sktai.safetyfilter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterGroupStopwordsDelete;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupKeywordsUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupStopWordsCreateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.GroupStopwordsBatchImportResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.OperationResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupStopWordUpdateRes;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupsStopWordRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI SafetyFilter 그룹 불용어 관리 API 클라이언트
 *
 * <p>
 * SafetyFilter 그룹의 불용어(stopword) 관리를 담당하는 Feign 클라이언트입니다.
 * 그룹별 불용어 조회, 추가, 교체, 삭제 등의 기능을 제공합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>그룹 불용어 조회</strong>: 그룹별 불용어 목록 및 통계 조회</li>
 * <li><strong>불용어 완전 교체</strong>: 기존 불용어를 모두 삭제하고 새 목록으로 교체</li>
 * <li><strong>불용어 추가</strong>: 기존 불용어를 유지하면서 새로운 불용어 추가</li>
 * <li><strong>불용어 삭제</strong>: 특정 불용어들을 선택적으로 삭제</li>
 * </ul>
 *
 * <h3>인증 방식:</h3>
 * <ul>
 * <li><strong>OAuth2 Password Bearer</strong>: JWT 토큰 기반 인증</li>
 * </ul>
 *
 * <h3>URL 패턴:</h3>
 * <ul>
 * <li><strong>Base URL</strong>: https://adxp.mobigen.com</li>
 * <li><strong>API Path</strong>: /api/v1/safety-filters/groups/stopwords</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-17
 */
@Tag(name = "SKTAI Safety Filter Group Stopwords", description = "SKTAI Safety Filter 그룹 불용어 관리 API")
@FeignClient(name = "sktai-safety-filter-group-stopwords-client", url = "${sktai.api.base-url}", configuration =
        SktaiClientConfig.class)
public interface SktaiSafetyFilterGroupStopwordsClient {

    /**
     * SafetyFilter 그룹 불용어 목록 조회
     *
     * <p>
     * SafetyFilter 그룹별로 분류된 불용어 목록을 조회합니다.
     * 각 그룹에 속한 불용어들과 통계 정보를 함께 제공합니다.
     * </p>
     *
     * @param page                페이지 번호 (기본값: 1)
     * @param size                페이지 크기 (기본값: 10, -1: 모든 그룹)
     * @param sort                정렬 조건 (예: "group_name,asc;stopwords.stopword,asc")
     * @param filter              필터 조건 (예: "group_name:test")
     * @param search              검색 키워드 (그룹 이름 또는 불용어로 검색)
     * @param groupId             특정 그룹 ID로 필터링
     * @param includeUnclassified 미분류 그룹 포함 여부
     * @return 그룹별 불용어 목록과 통계 정보
     */
    @Operation(summary = "SafetyFilter 그룹 불용어 목록 조회", description = "SafetyFilter 그룹별로 분류된 불용어 목록을 조회합니다. 각 그룹에 속한 " +
            "불용어들과 통계 정보를 함께 제공합니다.")
    @ApiResponse(responseCode = "200", description = "그룹 불용어 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    @GetMapping("/api/v1/safety-filters/groups/stopwords")
    SktSafetyFilterGroupsStopWordRes getSafetyFilterGroupsStopwords(
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기 (-1: 모든 그룹)") @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "정렬 조건 (그룹: group_name,asc | 불용어: stopwords.stopword,asc)") @RequestParam(value
                    = "sort", required = false, defaultValue = "group_name,asc;stopwords.stopword,asc") String sort,

            @Parameter(description = "필터 조건 (예: group_name:test | count:0)") @RequestParam(value = "filter",
                    required = false) String filter,

            @Parameter(description = "검색 키워드 (그룹 이름 또는 불용어 부분 일치)") @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "특정 그룹 ID로 필터링") @RequestParam(value = "group_id", required = false) String groupId,

            @Parameter(description = "미분류 그룹 포함 여부") @RequestParam(value = "include_unclassified", defaultValue =
                    "false") Boolean includeUnclassified);

    /**
     * SafetyFilter 그룹 키워드 완전 교체
     *
     * <p>
     * 그룹의 모든 불용어를 새로운 목록으로 완전히 교체합니다.
     * 기존의 모든 불용어가 삭제되고 제공된 목록으로 대체됩니다.
     * </p>
     *
     * @param groupId 대상 그룹 ID (UUID 형태)
     * @param request 새로운 불용어 목록
     * @return 업데이트 작업 결과 및 통계
     */
    @Operation(summary = "SafetyFilter 그룹 키워드 완전 교체", description = "그룹의 모든 불용어를 새로운 목록으로 완전히 교체합니다. 기존 불용어는 모두 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키워드 교체 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PutMapping("/api/v1/safety-filters/groups/{group_id}/stopwords")
    SktSafetyFilterGroupStopWordUpdateRes updateSafetyFilterGroupKeywords(
            @Parameter(description = "대상 그룹 ID (UUID 형태)", required = true) @PathVariable("group_id") String groupId,

            @RequestBody SktSafetyFilterGroupKeywordsUpdateReq request);

    /**
     * SafetyFilter 그룹 키워드 추가
     *
     * <p>
     * 그룹에 새로운 불용어들을 추가합니다.
     * 기존 불용어는 유지되고 새로운 불용어만 추가됩니다 (비파괴적 추가).
     * </p>
     *
     * @param groupId 대상 그룹 ID (UUID 형태)
     * @param request 추가할 불용어 목록
     * @return 업데이트 작업 결과 및 통계
     */
    @Operation(summary = "SafetyFilter 그룹 키워드 추가", description = "그룹에 새로운 불용어들을 추가합니다. 기존 불용어는 유지됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키워드 추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PostMapping("/api/v1/safety-filters/groups/{group_id}/stopwords")
    SktSafetyFilterGroupStopWordUpdateRes appendSafetyFilterGroupKeywords(
            @Parameter(description = "대상 그룹 ID (UUID 형태)", required = true) @PathVariable("group_id") String groupId,

            @RequestBody SktSafetyFilterGroupStopWordsCreateReq request);

    /**
     * SafetyFilter 그룹 불용어 삭제
     *
     * <p>
     * 그룹에서 특정 불용어들을 삭제합니다.
     * 지정된 불용어만 제거되고 나머지는 유지됩니다.
     * </p>
     *
     * @param groupId 대상 그룹 ID (UUID 형태)
     * @param request 삭제할 불용어 목록
     * @return 삭제 작업 결과
     */
    @Operation(summary = "SafetyFilter 그룹 불용어 삭제", description = "그룹에서 특정 불용어들을 삭제합니다. 지정된 불용어만 제거됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "불용어 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @DeleteMapping("/api/v1/safety-filters/groups/{group_id}/stopwords")
    OperationResponse deleteSafetyFilterGroupStopwords(
            @Parameter(description = "대상 그룹 ID (UUID 형태)", required = true) @PathVariable("group_id") String groupId,

            @RequestBody SafetyFilterGroupStopwordsDelete request);

    /**
     * SafetyFilter 그룹 Stopwords 배치 Import
     *
     * <p>
     * 여러 그룹의 Stopwords를 한 번에 Import합니다.
     * 각 그룹별로 Import 결과가 개별적으로 반환됩니다.
     * </p>
     *
     * <h3>배치 Import 특징:</h3>
     * <ul>
     * <li><strong>일괄 처리</strong>: 여러 그룹의 Stopwords를 동시에 Import</li>
     * <li><strong>부분 성공 지원</strong>: 일부 그룹 실패 시에도 나머지 처리</li>
     * <li><strong>상세 결과</strong>: 각 그룹별 성공/실패 정보 제공</li>
     * </ul>
     *
     * <h3>응답 상태:</h3>
     * <ul>
     * <li><strong>success</strong>: 모든 그룹 Import 성공</li>
     * <li><strong>partial_success</strong>: 일부 성공, 일부 실패</li>
     * <li><strong>failed</strong>: 모든 그룹 Import 실패</li>
     * </ul>
     *
     * @param jsonData Import할 그룹 Stopwords 목록 Json Data
     * @return 배치 Import 결과 (성공/실패 카운트, 상세 정보)
     */
    @Operation(summary = "SafetyFilter 그룹 Stopwords 배치 Import", description = "여러 그룹의 Stopwords를 한 번에 Import합니다. 각 " +
            "그룹별로 Import 결과가 개별적으로 반환됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배치 Import 처리 완료 (부분 성공 포함)"),
            @ApiResponse(responseCode = "201", description = "배치 Import 전체 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PostMapping("/api/v1/safety-filters/groups/stopwords/import")
    GroupStopwordsBatchImportResponse importGroupStopwordsBatch(Object jsonData);

}
