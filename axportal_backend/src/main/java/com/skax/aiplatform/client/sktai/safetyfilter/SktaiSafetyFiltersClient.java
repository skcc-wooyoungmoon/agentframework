package com.skax.aiplatform.client.sktai.safetyfilter;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.CheckSafeOrNot;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterCreate;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterUpdate;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyCheckOutput;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFilterRead;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFiltersRead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * SKTAI SafetyFilter 개별 필터 관리 API 클라이언트
 *
 * <p>개별 SafetyFilter의 CRUD 작업과 안전성 검사를 담당하는 Feign 클라이언트입니다.
 * 개별 필터 관리와 텍스트 안전성 검사 기능을 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>필터 생성</strong>: 새로운 SafetyFilter 생성</li>
 *   <li><strong>필터 목록 조회</strong>: 페이지네이션, 정렬, 필터링, 검색 지원</li>
 *   <li><strong>필터 상세 조회</strong>: 특정 필터의 상세 정보 조회</li>
 *   <li><strong>필터 수정</strong>: 필터 정보 업데이트</li>
 *   <li><strong>필터 삭제</strong>: 개별 필터 삭제</li>
 *   <li><strong>안전성 검사</strong>: 텍스트의 유해성 판단</li>
 * </ul>
 *
 * <h3>인증 방식:</h3>
 * <ul>
 *   <li><strong>OAuth2 Password Bearer</strong>: JWT 토큰 기반 인증 (CRUD 작업)</li>
 *   <li><strong>Client Secret</strong>: API 키 기반 인증 (안전성 검사)</li>
 * </ul>
 *
 * <h3>URL 패턴:</h3>
 * <ul>
 *   <li><strong>Base URL</strong>: https://adxp.mobigen.com</li>
 *   <li><strong>API Path</strong>: /api/v1/safety-filters</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-17
 */
@Tag(name = "SKTAI Safety Filters", description = "SKTAI Safety Filter 개별 필터 관리 및 검사 API")
@FeignClient(
        name = "sktai-safety-filters-client",
        url = "${sktai.api.base-url}",
        configuration = SktaiClientConfig.class
)
public interface SktaiSafetyFiltersClient {

    /**
     * SafetyFilter 등록
     *
     * <p>새로운 안전 필터를 생성합니다.
     * 키워드, 라벨, 정책 등을 설정하여 텍스트 필터링 규칙을 등록합니다.</p>
     *
     * @param request SafetyFilter 생성 요청 정보
     * @return 생성된 SafetyFilter 정보
     */
    @Operation(
            summary = "SafetyFilter 등록",
            description = "새로운 안전 필터를 생성합니다. 키워드, 라벨, 정책 등을 설정하여 텍스트 필터링 규칙을 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "SafetyFilter 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PostMapping("/api/v1/safety-filters")
    SafetyFilterRead registerSafetyFilter(@RequestBody SafetyFilterCreate request);

    /**
     * SafetyFilter 목록 조회
     *
     * <p>등록된 안전 필터 목록을 조회합니다.
     * 페이지네이션, 정렬, 필터링, 검색 기능을 지원합니다.</p>
     *
     * @param page   페이지 번호 (기본값: 1)
     * @param size   페이지 크기 (기본값: 10, -1: 모든 필터)
     * @param sort   정렬 조건 (예: "stopword,asc")
     * @param filter 필터 조건 (예: "group_id:uuid")
     * @param search 검색 키워드 (불용어로 검색)
     * @return SafetyFilter 목록과 페이지네이션 정보
     */
    @Operation(
            summary = "SafetyFilter 목록 조회",
            description = "등록된 안전 필터 목록을 조회합니다. 페이지네이션, 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SafetyFilter 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @GetMapping("/api/v1/safety-filters")
    SafetyFiltersRead getSafetyFilters(
            @Parameter(description = "페이지 번호 (1부터 시작)")
            @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기 (-1: 모든 필터)")
            @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "정렬 조건 (예: stopword,asc | updated_at,desc)")
            @RequestParam(value = "sort", required = false) String sort,

            @Parameter(description = "필터 조건 (예: group_id:uuid | label:unsafe_user_defined)")
            @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색 키워드 (불용어 부분 일치)")
            @RequestParam(value = "search", required = false) String search
    );

    /**
     * SafetyFilter 상세 조회
     *
     * <p>특정 안전 필터의 상세 정보를 조회합니다.</p>
     *
     * @param safetyFilterId 조회할 SafetyFilter ID (UUID 형태)
     * @return SafetyFilter 상세 정보
     */
    @Operation(
            summary = "SafetyFilter 상세 조회",
            description = "특정 안전 필터의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SafetyFilter 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "SafetyFilter를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @GetMapping("/api/v1/safety-filters/{safety_filter_id}")
    SafetyFilterRead getSafetyFilter(
            @Parameter(description = "SafetyFilter ID (UUID 형태)", required = true)
            @PathVariable("safety_filter_id") String safetyFilterId
    );

    /**
     * SafetyFilter 수정
     *
     * <p>기존 안전 필터의 정보를 수정합니다.
     * 키워드, 라벨, 그룹 등의 설정을 업데이트할 수 있습니다.</p>
     *
     * @param safetyFilterId 수정할 SafetyFilter ID (UUID 형태)
     * @param request        SafetyFilter 수정 요청 정보
     * @return 수정된 SafetyFilter 정보
     */
    @Operation(
            summary = "SafetyFilter 수정",
            description = "기존 안전 필터의 정보를 수정합니다. 키워드, 라벨, 그룹 등의 설정을 업데이트할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SafetyFilter 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "SafetyFilter를 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PutMapping("/api/v1/safety-filters/{safety_filter_id}")
    SafetyFilterRead updateSafetyFilter(
            @Parameter(description = "수정할 SafetyFilter ID (UUID 형태)", required = true)
            @PathVariable("safety_filter_id") String safetyFilterId,

            @RequestBody SafetyFilterUpdate request
    );

    /**
     * SafetyFilter 삭제
     *
     * <p>지정된 안전 필터를 삭제합니다.</p>
     *
     * @param safetyFilterId 삭제할 SafetyFilter ID (UUID 형태)
     */
    @Operation(
            summary = "SafetyFilter 삭제",
            description = "지정된 안전 필터를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "SafetyFilter 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "SafetyFilter를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @DeleteMapping("/api/v1/safety-filters/{safety_filter_id}")
    void deleteSafetyFilter(
            @Parameter(description = "삭제할 SafetyFilter ID (UUID 형태)", required = true)
            @PathVariable("safety_filter_id") String safetyFilterId
    );

    /**
     * 텍스트 안전성 검사
     *
     * <p>지정된 텍스트의 유해성을 판단합니다.
     * 등록된 안전 필터들을 사용하여 텍스트에 유해한 내용이 포함되어 있는지 검사합니다.</p>
     *
     * @param clientSecret API 클라이언트 시크릿 (인증용)
     * @param projectId    프로젝트 ID (선택사항)
     * @param request      안전성 검사 요청 정보
     * @return 안전성 검사 결과
     */
    @Operation(
            summary = "텍스트 안전성 검사",
            description = "지정된 텍스트의 유해성을 판단합니다. 등록된 안전 필터들을 사용하여 검사를 수행합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "안전성 검사 완료"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
    })
    @PostMapping("/api/v1/safety-filters/safe")
    SafetyCheckOutput checkSafety(
            @Parameter(description = "API 클라이언트 시크릿", required = true)
            @RequestParam("client_secret") String clientSecret,

            @Parameter(description = "프로젝트 ID (선택사항)")
            @RequestParam(value = "project_id", required = false) String projectId,

            @RequestBody CheckSafeOrNot request
    );

}
