package com.skax.aiplatform.controller.deploy;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterCreateReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterDeleteReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterListReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterUpdateReq;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterCreateRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterDeleteRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterDetailRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterUpdateRes;
import com.skax.aiplatform.service.deploy.SafetyFilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 세이프티 필터 컨트롤러
 *
 * <p>서비스 응답의 품질과 안전성을 보장하기 위한 금지어 관리 API를 제공합니다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/safety-filter")
@RequiredArgsConstructor
@Tag(name = "세이프티 필터 관리", description = "세이프티 필터 관리 API")
public class SafetyFilterController {

    private final SafetyFilterService safetyFilterService;

    /**
     * 세이프티 필터 목록 조회
     *
     * @param page   페이지 번호 (0부터 시작)
     * @param size   페이지 크기
     * @param filter 필터 (금지어)
     * @param search 검색어 (분류, 금지어)
     * @param sort   정렬
     * @return 페이징된 세이프티 필터 목록
     */
    @GetMapping
    @Operation(summary = "세이프티 필터 목록 조회", description = "세이프티 필터 목록을 조회합니다.")
    public AxResponseEntity<PageResponse<SafetyFilterRes>> getSafetyFilterList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "created_at,desc") String sort
    ) {
        log.info("세이프티 필터 목록 조회 API 호출 - page: {}, size: {}, filter: {}, search: {},  sort: {}",
                page, size, filter, search, sort);

        SafetyFilterListReq request = SafetyFilterListReq.of(page, size, filter, search, sort);

        PageResponse<SafetyFilterRes> response = safetyFilterService.getSafetyFilterList(request);

        return AxResponseEntity.ok(response, "세이프티 필터 목록 조회 성공");
    }

    /**
     * 세이프티 필터 상세 조회
     *
     * @param filterGroupId 세이프티 필터 그룹 ID
     * @return 필터 상세 정보
     */
    @GetMapping("/{filter_groupId}")
    @Operation(summary = "세이프티 필터 상세 조회", description = "특정 세이프티 필터의 상세 정보를 조회합니다.")
    public AxResponseEntity<SafetyFilterDetailRes> getSafetyFilterDetail(
            @PathVariable(name = "filter_groupId") String filterGroupId
    ) {
        log.info("세이프티 필터 상세 조회 API 호출 - filterGroupId: {}", filterGroupId);

        SafetyFilterDetailRes response = safetyFilterService.getSafetyFilterDetail(filterGroupId);

        return AxResponseEntity.ok(response, "세이프티 필터 상세 조회 성공");
    }

    /**
     * 세이프티 필터 생성
     *
     * @param request 생성 요청 (분류, 금지어 목록)
     * @return 생성 결과
     */
    @PostMapping
    @Operation(summary = "세이프티 필터 생성", description = "새로운 세이프티 필터를 생성합니다.")
    public AxResponseEntity<SafetyFilterCreateRes> createSafetyFilter(
            @RequestBody @Valid SafetyFilterCreateReq request
    ) {
        log.info("세이프티 필터 생성 API 호출 - category: {}, stopWords: {}",
                request.getFilterGroupName(), request.getStopWords());

        SafetyFilterCreateRes response = safetyFilterService.createSafetyFilter(request);

        return AxResponseEntity.created(response, "세이프티 필터 생성 성공");
    }

    /**
     * 세이프티 필터 수정
     *
     * @param filterGroupId 세이프티 필터 그룹 ID
     * @param request       수정 요청 (분류, 금지어 목록)
     * @return 수정 결과
     */
    @PutMapping("/{filter_groupId}")
    @Operation(summary = "세이프티 필터 수정", description = "기존 세이프티 필터를 수정합니다.")
    public AxResponseEntity<SafetyFilterUpdateRes> updateSafetyFilter(
            @PathVariable(name = "filter_groupId") String filterGroupId,
            @Valid @RequestBody SafetyFilterUpdateReq request
    ) {

        log.info("세이프티 필터 수정 API 호출 - filterGroupId: {}, category: {}, stopWords: {}",
                filterGroupId, request.getFilterGroupName(), request.getStopWords());

        SafetyFilterUpdateRes response = safetyFilterService.updateSafetyFilter(filterGroupId, request);

        return AxResponseEntity.ok(response, "세이프티 필터 수정 성공");
    }

    /**
     * 세이프티 필터 삭제 (단일/복수 통합)
     *
     * @param request 삭제 요청 (ID 목록 - 단일인 경우 1개, 복수인 경우 여러 개)
     * @return 삭제 결과
     */
    @DeleteMapping
    @Operation(summary = "세이프티 필터 삭제", description = "선택한 세이프티 필터를 삭제합니다. (단일/복수 모두 지원)")
    public AxResponseEntity<SafetyFilterDeleteRes> deleteSafetyFilter(
            @RequestBody @Valid SafetyFilterDeleteReq request
    ) {
        log.info("세이프티 필터 삭제 API 호출 - ids: {}, 건수: {}",
                request.getFilterGroupIds(), request.getFilterGroupIds().size());

        SafetyFilterDeleteRes response = safetyFilterService.deleteSafetyFilterBulk(request);

        return AxResponseEntity.ok(response, "세이프티 필터 삭제 성공");
    }

    /**
     * 세이프티 필터 Policy 설정
     *
     * @param filterGroupId 세이프티 필터 그룹 ID (필수)
     * @param memberId      사용자 ID (필수)
     * @param projectName   프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/{filter_groupId}/policy")
    @Operation(summary = "세이프티 필터 Policy 설정", description = "세이프티 필터의 Policy를 설정합니다.")
    public AxResponseEntity<List<PolicyRequest>> setSafetyFilterPolicy(
            @PathVariable(name = "filter_groupId") String filterGroupId,
            @RequestParam(value = "member_id", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) String projectName
    ) {
        log.info("세이프티 필터 Policy 설정 요청 - filterGroupId: {}, memberId: {}, projectName: {}",
                filterGroupId, memberId, projectName);

        List<PolicyRequest> policy = safetyFilterService.setSafetyFilterPolicy(filterGroupId, memberId, projectName);

        return AxResponseEntity.ok(policy, "세이프티 필터 Policy가 성공적으로 설정되었습니다.");
    }

}
