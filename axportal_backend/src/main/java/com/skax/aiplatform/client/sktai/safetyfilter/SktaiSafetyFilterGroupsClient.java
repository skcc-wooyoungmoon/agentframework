package com.skax.aiplatform.client.sktai.safetyfilter;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterGroupImportRequest;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupCreateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFilterGroupImportResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupUpdateRes;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupsRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * SKTAI SafetyFilter 그룹 관리 API 클라이언트
 *
 * <p>
 * SafetyFilter 그룹의 CRUD 작업을 담당하는 Feign 클라이언트입니다.
 * 그룹 생성, 조회, 수정, 삭제 등의 기능을 제공합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>그룹 생성</strong>: 새로운 SafetyFilter 그룹 생성</li>
 * <li><strong>그룹 목록 조회</strong>: 페이지네이션, 정렬, 필터링, 검색 지원</li>
 * <li><strong>그룹 상세 조회</strong>: 특정 그룹의 상세 정보 조회</li>
 * <li><strong>그룹 수정</strong>: 그룹 정보 업데이트</li>
 * <li><strong>그룹 삭제</strong>: 그룹 및 관련 불용어 삭제 (CASCADE)</li>
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
 * <li><strong>API Path</strong>: /api/v1/safety-filters/groups</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-17
 */
@Tag(name = "SKTAI Safety Filter Groups", description = "SKTAI Safety Filter 그룹 관리 API")
@FeignClient(name = "sktai-safety-filter-groups-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
public interface SktaiSafetyFilterGroupsClient {

        /**
         * SafetyFilter 그룹 목록 조회
         *
         * <p>
         * 등록된 안전 필터 그룹 목록을 조회합니다.
         * 페이지네이션, 정렬, 필터링, 검색 기능을 지원합니다.
         * </p>
         *
         * @param page                페이지 번호 (기본값: 1)
         * @param size                페이지 크기 (기본값: 10, -1: 모든 그룹)
         * @param sort                정렬 조건 (예: "group_name,asc")
         * @param filter              필터 조건 (예: "group_name:test")
         * @param search              검색 키워드 (그룹 이름으로 검색)
         * @param groupId             특정 그룹 ID로 필터링
         * @param includeUnclassified 미분류 그룹 포함 여부
         * @return 그룹 목록과 페이지네이션 정보
         */
        @Operation(summary = "SafetyFilter 그룹 목록 조회", description = "등록된 안전 필터 그룹 목록을 조회합니다. 페이지네이션, 정렬, 필터링, 검색 기능을 지원합니다.")
        @ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공")
        @ApiResponse(responseCode = "401", description = "인증 실패")
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
        @GetMapping("/api/v1/safety-filters/groups")
        SktSafetyFilterGroupsRes getSafetyFilterGroups(
                        @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(value = "page", defaultValue = "1") Integer page,

                        @Parameter(description = "페이지 크기 (-1: 모든 그룹)") @RequestParam(value = "size", defaultValue = "10") Integer size,

                        @Parameter(description = "정렬 조건 (예: group_name,asc | updated_at,desc)") @RequestParam(value = "sort", required = false, defaultValue = "updated_at,desc") String sort,

                        @Parameter(description = "필터 조건 (예: group_name:test | created_by:user1)") @RequestParam(value = "filter", required = false) String filter,

                        @Parameter(description = "검색 키워드 (그룹 이름 부분 일치)") @RequestParam(value = "search", required = false) String search,

                        @Parameter(description = "특정 그룹 ID로 필터링") @RequestParam(value = "group_id", required = false) String groupId,

                        @Parameter(description = "미분류 그룹 포함 여부") @RequestParam(value = "include_unclassified", defaultValue = "false") Boolean includeUnclassified);

        /**
         * SafetyFilter 그룹 상세 조회
         *
         * <p>
         * 특정 안전 필터 그룹의 상세 정보를 조회합니다.
         * </p>
         *
         * @param groupId 조회할 그룹 ID (UUID 형태)
         * @return 그룹 상세 정보
         */
        @Operation(summary = "SafetyFilter 그룹 상세 조회", description = "특정 안전 필터 그룹의 상세 정보를 조회합니다.")
        @ApiResponse(responseCode = "200", description = "그룹 조회 성공")
        @ApiResponse(responseCode = "401", description = "인증 실패")
        @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
        @GetMapping("/api/v1/safety-filters/groups/{group_id}")
        SktSafetyFilterGroupUpdateRes getSafetyFilterGroup(
                        @Parameter(description = "그룹 ID (UUID 형태)", required = true) @PathVariable("group_id") String groupId);

        /**
         * SafetyFilter 그룹 생성
         *
         * <p>
         * 새로운 안전 필터 그룹을 생성합니다.
         * 그룹을 통해 관련된 안전 필터들을 조직화하고 관리할 수 있습니다.
         * </p>
         *
         * @param request 그룹 생성 요청 정보
         * @return 생성된 그룹 정보
         */
        @Operation(summary = "SafetyFilter 그룹 생성", description = "새로운 안전 필터 그룹을 생성합니다. 그룹을 통해 관련된 안전 필터들을 조직화하고 관리할 수 있습니다.")
        @ApiResponse(responseCode = "201", description = "그룹 생성 성공")
        @ApiResponse(responseCode = "401", description = "인증 실패")
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
        @PostMapping("/api/v1/safety-filters/groups")
        SktSafetyFilterGroupUpdateRes createSafetyFilterGroup(@RequestBody SktSafetyFilterGroupCreateReq request);

        /**
         * SafetyFilter 그룹 수정
         *
         * <p>
         * 기존 안전 필터 그룹의 정보를 수정합니다.
         * 현재는 그룹 이름만 수정 가능합니다.
         * </p>
         *
         * @param groupId 수정할 그룹 ID (UUID 형태)
         * @param request 그룹 수정 요청 정보
         * @return 수정된 그룹 정보
         */
        @Operation(summary = "SafetyFilter 그룹 수정", description = "기존 안전 필터 그룹의 정보를 수정합니다. 현재는 그룹 이름만 수정 가능합니다.")
        @ApiResponse(responseCode = "200", description = "그룹 수정 성공")
        @ApiResponse(responseCode = "401", description = "인증 실패")
        @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
        @PutMapping("/api/v1/safety-filters/groups/{group_id}")
        SktSafetyFilterGroupUpdateRes updateSafetyFilterGroup(
                        @Parameter(description = "수정할 그룹 ID (UUID 형태)", required = true) @PathVariable("group_id") String groupId,
                        @RequestBody SktSafetyFilterGroupUpdateReq request);

        /**
         * SafetyFilter 그룹 삭제
         *
         * <p>
         * 안전 필터 그룹을 삭제합니다.
         * 그룹에 속한 모든 불용어(안전 필터)도 함께 삭제됩니다 (CASCADE 삭제).
         * </p>
         *
         * @param groupId 삭제할 그룹 ID (UUID 형태)
         */
        @Operation(summary = "SafetyFilter 그룹 삭제", description = "안전 필터 그룹을 삭제합니다. 그룹에 속한 모든 불용어(안전 필터)도 함께 삭제됩니다.")
        @ApiResponse(responseCode = "204", description = "그룹 삭제 성공")
        @ApiResponse(responseCode = "401", description = "인증 실패")
        @ApiResponse(responseCode = "404", description = "그룹을 찾을 수 없음")
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
        @DeleteMapping("/api/v1/safety-filters/groups/{group_id}")
        void deleteSafetyFilterGroup(
                        @Parameter(description = "삭제할 그룹 ID (UUID 형태)", required = true) @PathVariable("group_id") String groupId);

        /**
         * SafetyFilter 그룹 Import
         *
         * <p>
         * Export된 안전 필터 그룹을 Import합니다.
         * 기존 그룹이 존재하면 업데이트하고, 없으면 새로 생성합니다.
         * </p>
         *
         * <h3>Import 동작:</h3>
         * <ul>
         * <li><strong>created</strong>: 새로운 그룹으로 생성됨</li>
         * <li><strong>existing</strong>: 기존 그룹 업데이트됨</li>
         * <li><strong>conflict</strong>: Import 실패 (이름 중복 등)</li>
         * </ul>
         *
         * @param request Import할 그룹 정보
         * @return Import 결과와 그룹 정보
         */
        @Operation(summary = "SafetyFilter 그룹 Import", description = "Export된 안전 필터 그룹을 Import합니다. 기존 그룹이 존재하면 업데이트하고, 없으면 새로 생성합니다.")
        @ApiResponse(responseCode = "201", description = "그룹 Import 성공")
        @ApiResponse(responseCode = "401", description = "인증 실패")
        @ApiResponse(responseCode = "422", description = "유효성 검증 실패")
        @PostMapping("/api/v1/safety-filters/groups/import")
        SafetyFilterGroupImportResponse importSafetyFilterGroup(@RequestBody SafetyFilterGroupImportRequest request);

}
