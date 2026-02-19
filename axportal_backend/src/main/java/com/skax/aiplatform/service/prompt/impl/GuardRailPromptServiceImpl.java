package com.skax.aiplatform.service.prompt.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.skax.aiplatform.common.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptMessagesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptsResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentInferencePromptsService;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptMsgsByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptTagsListByIdRes;
import com.skax.aiplatform.mapper.prompt.GuardRailPromptMapper;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.prompt.GuardRailPromptService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 가드레일 프롬프트 서비스 구현체
 *
 * <p>
 * 가드레일 프롬프트 관리 비즈니스 로직을 구현하는 서비스 클래스입니다.
 * 추론 프롬프트와 완전히 분리되어 독립적으로 동작합니다.
 * </p>
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-11-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GuardRailPromptServiceImpl implements GuardRailPromptService {

    private static final String DEFAULT_GUARDRAIL_PROMPT_FILTER = "ptype:2";

    private final SktaiAgentInferencePromptsService sktaiAgentInferencePromptsService;
    private final GuardRailPromptMapper guardRailPromptMapper;
    private final AdminAuthService adminAuthService;

    @Override
    public PageResponse<GuardRailPromptRes> getGuardRailPromptList(String projectId, int page, int size, String tag,
            String search, String sort, String filter) {
        log.info("[ Execute Service GuardRailPromptServiceImpl.getGuardRailPromptList ]");
        log.info("projectId            : {}", projectId);
        log.info("Page                  : {}", page);
        log.info("Size                  : {}", size);
        log.info("Search                : {}", search);
        log.info("Tag                   : {}", tag);
        log.info("filter                : {}", filter);
        log.info("Sort                  : {}", sort);

        // filter에 ptype:2,tags:{tag값} 형식으로 전달되므로 일반 검색 API 사용
        // filter 파라미터를 그대로 사용하여 검색
        return searchGuardRailPromptsByName(projectId, page, size, search, sort, filter);
    }

    private PageResponse<GuardRailPromptRes> searchGuardRailPromptsByName(String projectId, int page, int size,
            String search,
            String sort, String filter) {
        // filter 파라미터가 있으면 사용, 없으면 기본 필터 사용
        // filter 형식: ptype:2 또는 ptype:2,tags:{tag값}
        String actualFilter = (filter != null && !filter.trim().isEmpty())
                ? filter
                : DEFAULT_GUARDRAIL_PROMPT_FILTER;

        log.info("실제 사용할 filter: {}", actualFilter);

        PromptsResponse response = sktaiAgentInferencePromptsService.getInferencePrompts(
                projectId,
                page,
                size,
                sort,
                actualFilter,
                search,
                false);

        log.debug("response {}", response);

        if (response == null) {
            log.warn("가드레일 프롬프트 목록 응답이 null입니다. 빈 결과를 반환합니다.");
            return buildEmptyPromptPage(page, size);
        }

        List<GuardRailPromptRes> content = toSanitizedPromptSummaries(response.getData());
        // populateUpdatedAtAsync(content);

        // Pagination pagination = null;
        // if (response.getPayload() != null) {
        //     pagination = response.getPayload().getPagination();
        // }
        //
        // if (pagination == null) {
        //     log.warn("가드레일 프롬프트 목록 응답에 페이징 정보가 없습니다. 빈 결과를 반환합니다.");
        //     return buildEmptyPromptPage(page, size);
        // }
        //
        // List<GuardRailPromptRes> safeContent = content != null ? content : new ArrayList<>();
        // int paginationPage = pagination.getPage() != null ? pagination.getPage() : 1;
        // int paginationSize = pagination.getItemsPerPage() != null ? pagination.getItemsPerPage() : size;
        // long paginationTotal = pagination.getTotal() != null ? pagination.getTotal() : safeContent.size();
        //
        // Page<GuardRailPromptRes> result = new PageImpl<>(
        //         safeContent,
        //         PageRequest.of(Math.max(paginationPage - 1, 0), Math.max(paginationSize, 1)),
        //         paginationTotal);
        //
        // return PageResponse.from(result);

        return PaginationUtils.toPageResponseFromAdxp(response.getPayload().getPagination(), content);
    }

    @Override
    public GuardRailPromptByIdRes getGuardRailPromptById(String promptUuid) {
        GuardRailPromptByIdRes result;

        log.info("[ Execute Service GuardRailPromptServiceImpl.getGuardRailPromptById ]");
        log.info("promptUuid           : {}", promptUuid);

        PromptResponse response = sktaiAgentInferencePromptsService.getInferencePrompt(promptUuid);

        log.debug("getGuardRailPromptById response => {}", response);

        result = mapPromptDetailOrThrow(response, promptUuid);

        if (result == null) {
            log.error("가드레일 프롬프트 상세 정보가 null입니다 - promptUuid: {}", promptUuid);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "가드레일 프롬프트 상세 정보를 가져올 수 없습니다.");
        }

        return result;
    }

    @Override
    public GuardRailPromptMsgsByIdRes getGuardRailPromptMsgsById(String versionUuid) {
        GuardRailPromptMsgsByIdRes result = null;

        log.info("[ Execute Service GuardRailPromptServiceImpl.getGuardRailPromptMsgsById ]");
        log.info("versionUuid            : {}", versionUuid);

        PromptMessagesResponse response = sktaiAgentInferencePromptsService.getInferencePromptMessages(versionUuid);

        log.debug("getGuardRailPromptMsgsById response => {}", response);

        result = guardRailPromptMapper.from(response);
        return result;
    }

    @Override
    public GuardRailPromptTagsListByIdRes getGuardRailPromptTagById(String versionUuid) {
        log.info("[ Execute Service GuardRailPromptServiceImpl.getGuardRailPromptTagById ]");
        log.info("versionUuid            : {}", versionUuid);

        PromptTagsResponse response = sktaiAgentInferencePromptsService.getInferencePromptTagsByVersion(versionUuid);

        log.debug("sktaiAgentInferencePromptsService.getGuardRailPromptTagById => {}", response);

        GuardRailPromptTagsListByIdRes result = guardRailPromptMapper.from(response);
        return result;
    }

    @Override
    public GuardRailPromptCreateRes createGuardRailPrompt(GuardRailPromptCreateReq request) {
        log.info("[ Execute Service GuardRailPromptServiceImpl.createGuardRailPrompt ]");
        log.info("Request DTO : {}", request);

        try {
            // InfPromptCreateReq를 PromptCreateRequest로 변환
            PromptCreateRequest createRequest = guardRailPromptMapper.toNewCreateRequest(request);

            // SKTAI API 호출
            PromptCreateResponse createResponse = sktaiAgentInferencePromptsService
                    .createInferencePrompt(createRequest);

            // 프롬프트 ADXP 권한부여
            adminAuthService.setResourcePolicyByCurrentGroup(
                    "/api/v1/agent/inference-prompts/" + createResponse.getPromptUuid());
            // adminAuthService.setResourcePolicyByCurrentGroup(
            //         "/api/v1/lineages/" + createResponse.getPromptUuid() + "/upstream");
//            adminAuthService.setResourcePolicyByCurrentGroup(
//                    "/api/v1/agent/inference-prompts/prompt/" + createResponse.getPromptUuid());
//            adminAuthService.setResourcePolicyByCurrentGroup(
//                    "/api/v1/agent/inference-prompts/versions/" + createResponse.getPromptUuid());
//            adminAuthService.setResourcePolicyByCurrentGroup(
//                    "/api/v1/agent/inference-prompts/versions/" + createResponse.getPromptUuid() + "/latest");

            // 프롬프트 버전 uuid 획득
//            PromptVersionResponse inferencePromptVersions = sktaiAgentInferencePromptsService
//                    .getLatestInferencePromptVersion(createResponse.getPromptUuid());

            // 프롬프트 버전 정책 부여
//            adminAuthService.setResourcePolicyByCurrentGroup(
//                    "/api/v1/agent/inference-prompts/variables/" + inferencePromptVersions.getData().getVersionId());
//            adminAuthService.setResourcePolicyByCurrentGroup(
//                    "/api/v1/agent/inference-prompts/messages/" + inferencePromptVersions.getData().getVersionId());
//            adminAuthService.setResourcePolicyByCurrentGroup(
//                    "/api/v1/agent/inference-prompts/tags/" + inferencePromptVersions.getData().getVersionId());

            // 응답을 GuardRailPromptCreateRes로 변환
            GuardRailPromptCreateRes guardRailPromptCreateRes = guardRailPromptMapper
                    .toNewCreateResponse(createResponse);

            log.info("가드레일 프롬프트 생성 성공: promptUuid={}", guardRailPromptCreateRes.getPromptUuid());

            return guardRailPromptCreateRes;

        } catch (FeignException e) {
            log.error("가드레일 프롬프트 생성 실패: name={}, status={}, 에러={}",
                    request.getName(), e.status(), e.getMessage());
            log.debug("가드레일 프롬프트 생성 실패 상세: {}", e.contentUTF8());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "가드레일 프롬프트를 생성할 수 없습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("가드레일 프롬프트 생성 실패 (Unexpected): name={}, error={}",
                    request.getName(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "가드레일 프롬프트 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public void updateGuardRailPromptById(String promptUuid, GuardRailPromptUpdateReq request) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service GuardRailPromptServiceImpl.updateGuardRailPromptById ]");
        log.info("Propmt UUID: {}", promptUuid);
        log.info("Request DTO : {}", request);
        log.info("-----------------------------------------------------------------------------------------");

        try {
            // InfPromptUpdateReq를 PromptUpdateRequest로 변환
            PromptUpdateRequest updateRequest = guardRailPromptMapper.toNewUpdateRequest(promptUuid, request);

            sktaiAgentInferencePromptsService.updateInferencePrompt(promptUuid, updateRequest);

            log.info("가드레일 프롬프트 수정 성공: promptUuid={}", promptUuid);

        } catch (FeignException e) {
            log.error("가드레일 프롬프트 수정 실패: promptUuid={}, status={}, 에러={}",
                    promptUuid, e.status(), e.getMessage());
            log.debug("가드레일 프롬프트 수정 실패 상세: {}", e.contentUTF8());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "가드레일 프롬프트를 수정할 수 없습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("가드레일 프롬프트 수정 실패 (Unexpected): promptUuid={}, error={}",
                    promptUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "가드레일 프롬프트 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public void deleteGuardRailPromptById(String promptUuid) {

        log.info("가드레일 프롬프트 삭제 요청: promptUuid={}", promptUuid);

        try {
            sktaiAgentInferencePromptsService.deleteInferencePrompt(promptUuid);

            log.info("가드레일 프롬프트 삭제 성공: promptUuid={}", promptUuid);

        } catch (FeignException e) {
            log.error("가드레일 프롬프트 삭제 실패: promptUuid={}, status={}, 에러={}",
                    promptUuid, e.status(), e.getMessage());
            log.debug("가드레일 프롬프트 삭제 실패 상세: {}", e.contentUTF8());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "가드레일 프롬프트를 삭제할 수 없습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("가드레일 프롬프트 삭제 실패 (Unexpected): promptUuid={}, error={}",
                    promptUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "가드레일 프롬프트 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public PromptVersionResponse getVersion(String promptUuid) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service GuardRailPromptServiceImpl.getVersion ]");
        log.info("Prompt ID            : {}", promptUuid);
        log.info("-----------------------------------------------------------------------------------------");
        try {
            PromptVersionsResponse versionsResponse = sktaiAgentInferencePromptsService
                    .getInferencePromptVersions(promptUuid);
            log.debug("sktaiAgentInferencePromptsService.getInferencePromptVersions => {}", versionsResponse);

            if (versionsResponse != null && versionsResponse.getData() != null
                    && !versionsResponse.getData().isEmpty()) {
                List<PromptVersionsResponse.VersionData> versions = versionsResponse.getData();

                // version 1 찾기 (최초 생성자)
                PromptVersionsResponse.VersionData version1Data = versions.stream()
                        .filter(v -> v.getVersion() != null && v.getVersion() == 1)
                        .findFirst()
                        .orElse(null);

                // 최신 버전 (첫 번째 요소)
                PromptVersionsResponse.VersionData latestVersion = versions.get(0);

                String createdBy = null;
                String createdAt = null;
                String updatedBy = null;
                String updatedAt = null;

                if (version1Data != null) {
                    // version 1이 존재하는 경우
                    createdBy = version1Data.getCreatedBy();
                    createdAt = DateUtils.toDateTimeString(version1Data.getCreatedAt());

                    // version이 1개만 있는 경우 updated 정보는 빈값
                    if (versions.size() == 1) {
                        updatedBy = null;
                        updatedAt = null;
                    } else {
                        // 여러 버전이 있는 경우 최신 버전의 정보를 updated로 사용
                        updatedBy = latestVersion.getCreatedBy();
                        updatedAt = DateUtils.toDateTimeString(latestVersion.getCreatedAt());
                    }
                } else {
                    // version 1이 없는 경우 (이상한 상황)
                    log.warn("version 1을 찾을 수 없습니다. promptUuid: {}", promptUuid);
                    createdBy = null;
                    createdAt = null;
                    updatedBy = latestVersion.getCreatedBy();
                    updatedAt = DateUtils.toDateTimeString(latestVersion.getCreatedAt());
                }

                log.info("버전 정보 처리 완료 - createdBy: {}, createdAt: {}, updatedBy: {}, updatedAt: {}",
                        createdBy, createdAt, updatedBy, updatedAt);

                // VersionData를 PromptVersionResponse로 변환 (추가 필드 포함)
                PromptVersionResponse.VersionData versionData = PromptVersionResponse.VersionData.builder()
                        .createdBy(createdBy)
                        .createdAt(createdAt)
                        .updatedBy(updatedBy)
                        .updatedAt(updatedAt)
                        .version(latestVersion.getVersion())
                        .release(latestVersion.getRelease())
                        .deleteFlag(latestVersion.getDeleteFlag())
                        .versionId(latestVersion.getVersionId())
                        .uuid(latestVersion.getUuid())
                        .build();

                return PromptVersionResponse.builder()
                        .timestamp(versionsResponse.getTimestamp())
                        .code(versionsResponse.getCode())
                        .detail(versionsResponse.getDetail())
                        .data(versionData)
                        .build();
            }

            return null;
        } catch (FeignException e) {
            log.error("버전 조회 실패 - promptUuid: {}, status: {}, 에러: {}",
                    promptUuid, e.status(), e.getMessage());
            log.debug("버전 조회 실패 상세 - {}", e.contentUTF8());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "버전 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("버전 조회 실패 (Unexpected) - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "버전 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ================= Helper Methods =================

    /**
     * 목록 요약 응답을 내부 DTO로 변환한다.
     */
    private List<GuardRailPromptRes> toSanitizedPromptSummaries(List<PromptsResponse.PromptSummary> summaries) {
        return Optional.ofNullable(summaries)
                .orElse(Collections.emptyList())
                .stream()
                .map(guardRailPromptMapper::from)
                .toList();
    }

    private PageResponse<GuardRailPromptRes> buildEmptyPromptPage(int page, int size) {
        int safePage = page >= 0 ? page : 0;
        int safeSize = size > 0 ? size : 20;
        Page<GuardRailPromptRes> emptyPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(safePage, safeSize),
                0);
        return PageResponse.from(emptyPage);
    }

    /**
     * 각 프롬프트의 updatedAt 정보를 비동기로 보완한다.
     *
     * <p>
     * 목록조회 응답에 updatedAt이 없는 경우, 버전조회 API를 호출하여 보완한다.
     * 상세조회 API는 호출하지 않고 버전조회 API만 사용하여 성능을 개선한다.
     */
    private void populateUpdatedAtAsync(List<GuardRailPromptRes> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            return;
        }

        List<CompletableFuture<Void>> futures = prompts.stream()
                .filter(Objects::nonNull)
                .filter(prompt -> StringUtils.hasText(prompt.getUuid()))
                // updatedAt이 이미 있으면 버전조회 API 호출 불필요
                .filter(prompt -> !StringUtils.hasText(prompt.getUpdatedAt()))
                .map(prompt -> CompletableFuture
                        .supplyAsync(() -> resolveDatesFromVersions(prompt.getUuid()))
                        .thenAccept(dates -> {
                            if (dates == null) {
                                return;
                            }
                            // updatedAt 설정
                            if (StringUtils.hasText(dates.updatedAt)) {
                                prompt.setUpdatedAt(dates.updatedAt);
                            }
                            // createdAt이 목록조회에 없으면 버전조회에서 가져온 값으로 설정
                            if (!StringUtils.hasText(prompt.getCreatedAt()) && StringUtils.hasText(dates.createdAt)) {
                                prompt.setCreatedAt(dates.createdAt);
                            }
                        }))
                .toList();

        futures.forEach(CompletableFuture::join);
    }

    /**
     * 버전조회 API를 통해 createdAt과 updatedAt을 조회한다.
     *
     * @param promptUuid 프롬프트 UUID
     * @return createdAt과 updatedAt을 포함한 객체 (조회 실패 시 null)
     */
    private VersionDates resolveDatesFromVersions(String promptUuid) {
        try {
            PromptVersionsResponse versionsResponse = sktaiAgentInferencePromptsService
                    .getInferencePromptVersions(promptUuid);
            if (versionsResponse == null || versionsResponse.getData() == null
                    || versionsResponse.getData().isEmpty()) {
                return null;
            }

            List<PromptVersionsResponse.VersionData> versions = versionsResponse.getData();

            // version 1 찾기 (최초 생성일)
            PromptVersionsResponse.VersionData version1Data = versions.stream()
                    .filter(v -> v.getVersion() != null && v.getVersion() == 1)
                    .findFirst()
                    .orElse(null);

            // 최신 버전 (첫 번째 요소, 수정일)
            PromptVersionsResponse.VersionData latestVersion = versions.get(0);

            String createdAt = null;
            String updatedAt = null;

            if (version1Data != null && version1Data.getCreatedAt() != null) {
                createdAt = DateUtils.toDateTimeString(version1Data.getCreatedAt());
            }

            // version이 1개만 있으면 updatedAt은 null
            if (versions.size() > 1 && latestVersion != null && latestVersion.getCreatedAt() != null) {
                updatedAt = DateUtils.toDateTimeString(latestVersion.getCreatedAt());
            }

            return new VersionDates(createdAt, updatedAt);

        } catch (FeignException e) {
            log.warn("가드레일 프롬프트 버전 조회 실패(Feign): uuid={}, status={}, reason={}",
                    promptUuid, e.status(), e.getMessage());
        } catch (RuntimeException e) {
            log.warn("가드레일 프롬프트 버전 조회 실패: uuid={}, message={}", promptUuid, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 버전조회에서 가져온 날짜 정보를 담는 내부 클래스
     */
    private static class VersionDates {

        final String createdAt;
        final String updatedAt;

        VersionDates(String createdAt, String updatedAt) {
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

    }

    private GuardRailPromptByIdRes mapPromptDetailOrThrow(PromptResponse response, String promptUuid) {
        GuardRailPromptByIdRes result = guardRailPromptMapper.from(response);
        if (result == null) {
            log.error("가드레일 프롬프트 상세 매핑 실패 - promptUuid: {}", promptUuid);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "가드레일 프롬프트 상세 변환에 실패했습니다.");
        }
        return result;
    }

}
