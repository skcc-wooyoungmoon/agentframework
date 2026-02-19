package com.skax.aiplatform.service.prompt.impl;

import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.*;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGraphsService;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentInferencePromptsService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.lineage.SktaiLineageClient;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.prompt.request.InfPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.InfPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.response.*;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.prompt.InferPromptMapper;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.prompt.InferPromptService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 추론프롬프트 서비스 구현체
 *
 * <p>
 * 추론프롬프트 관리 비즈니스 로직을 구현하는 서비스 클래스입니다.
 * SKTAI 추론프롬프트 API와의 연동을 통해 추론프롬프트 CRUD 작업을 수행합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li>추론프롬프트 목록 조회 (페이지네이션 지원)</li>
 * <li>추론프롬프트 상세 정보 조회</li>
 * <li>새로운 추론프롬프트 생성</li>
 * <li>추론프롬프트 정보 수정</li>
 * <li>추론프롬프트 삭제</li>
 * </ul>
 *
 * @author 권두현
 * @since 2025-11-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InferPromptServiceImpl implements InferPromptService {

    private static final String DEFAULT_INFER_PROMPT_FILTER = "ptype:1";
    private static final int TAG_FETCH_PAGE_SIZE = 100;

    private final SktaiAgentInferencePromptsService sktaiAgentInferencePromptsService;
    private final SktaiAuthService sktaiAuthService;
    private final SktaiAgentGraphsService sktaiAgentGraphsService;
    private final InferPromptMapper inferPromptMapper;
    private final AdminAuthService adminAuthService;
    private final SktaiLineageClient sktaiLineageClient;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final GpoUsersMasRepository usersMasRepository;

    @Override
    public PageResponse<InfPromptRes> getInfPromptList(String projectId, int page, int size, String tag, String search,
                                                       String sort, String filter, Boolean release_only) {
        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptList ]");
        log.info("projectId            : {}", projectId);
        log.info("Page                  : {}", page);
        log.info("Size                  : {}", size);
        log.info("Search                : {}", search);
        log.info("filter                : {}", filter);
        log.info("Sort                  : {}", sort);

        PageResponse<InfPromptRes> infPromptResPageResponse;

        // 태그 필터가 지정되면 SKT 태그 검색 엔드포인트로 우회
        if (tag != null && !tag.isBlank()) {
            infPromptResPageResponse = searchInfPromptsByTag(tag, filter);
        } else {
            infPromptResPageResponse = searchInfPromptsByName(projectId, page, size, search, sort, filter, release_only);
        }

        List<InfPromptRes> content = infPromptResPageResponse.getContent();

        for (InfPromptRes infPromptRes : content) {
            // 공개 여부 설정 (lst_prj_seq 값에 따라)
            GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                    .findByAsstUrl("/api/v1/agent/inference-prompts/" + infPromptRes.getUuid()).orElse(null);
            String publicStatus = null;

            if (existing != null && existing.getLstPrjSeq() != null) {
                // 음수면 "전체공유", 양수면 "내부공유"
                publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";

                infPromptRes.setFstPrjSeq(existing.getFstPrjSeq());
                infPromptRes.setLstPrjSeq(existing.getLstPrjSeq());
            } else {
                publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
            }

            infPromptRes.setPublicStatus(publicStatus);
        }

        return infPromptResPageResponse;
    }

    @Override
    public InfPromptTagsList getInfPromptTagList(String projectId, String filter) {
        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptTagList ]");
        log.info("Project ID            : {}", projectId);
        log.info("Filter                : {}", filter);

        Set<String> tagAccumulator = collectDistinctPromptTags(projectId, filter);

        List<String> sortedTags = tagAccumulator.stream()
                .sorted(String::compareToIgnoreCase)
                .toList();

        return InfPromptTagsList.of(sortedTags);
    }

    private PageResponse<InfPromptRes> searchInfPromptsByName(String projectId, int page, int size, String search,
                                                              String sort, String filter, Boolean release_only) {
        String actualFilter = StringUtils.hasText(filter) ? filter : DEFAULT_INFER_PROMPT_FILTER;

        PromptsResponse response = sktaiAgentInferencePromptsService.getInferencePrompts(
                projectId,
                page,
                size,
                sort,
                actualFilter,
                search,
                release_only);

        log.debug("response {}", response);

        List<InfPromptRes> content = toSanitizedPromptSummaries(response.getData());

        // 각 프롬프트에 대해 연결된 PROMPT 수 조회 및 매핑
        for (InfPromptRes prompt : content) {
            try {
                // Lineage 조회 (upstream 방향으로)
                List<LineageRelationWithTypes> lineageRelations = sktaiLineageClient.getLineageByObjectKeyAndDirection(
                        prompt.getUuid(),
                        "upstream",
                        ActionType.USE.getValue(),
                        5);

                // PROMPT 타입만 필터링하여 개수 계산
                long agentGraphCount = lineageRelations.stream()
                        .filter(relation -> ObjectType.AGENT_GRAPH.equals(relation.getSourceType()))
                        .count();

                prompt.setConnectedAgentCount((int) agentGraphCount);

            } catch (FeignException e) {
                log.warn("InferPrompt {} Lineage 조회 실패 (FeignException): {}", prompt.getUuid(), e.getMessage());
                // Lineage 조회 실패 시 기본값 설정
                prompt.setConnectedAgentCount(0);
            } catch (RuntimeException e) {
                log.warn("InferPrompt {} Lineage 조회 실패 (RuntimeException): {}", prompt.getUuid(), e.getMessage());
                // Lineage 조회 실패 시 기본값 설정
                prompt.setConnectedAgentCount(0);
            }
        }

        // 2) 페이지네이션 정보 확인
        // Pagination pagination = response.getPayload().getPagination();
        //
        // Page<InfPromptRes> result = new PageImpl<>(
        //         content,
        //         PageRequest.of(pagination.getPage() - 1, pagination.getItemsPerPage()),
        //         pagination.getTotal());
        //
        // return PageResponse.from(result);

        return PaginationUtils.toPageResponseFromAdxp(response.getPayload().getPagination(), content);
    }

    /**
     * SKT 태그 검색 API를 감싼다. API가 단일 페이지 결과만 제공하므로 응답을 한 페이지로 정규화한다.
     */
    private PageResponse<InfPromptRes> searchInfPromptsByTag(String tag, String filter) {
        PromptFilterByTagsResponse response = sktaiAgentInferencePromptsService.searchInferencePromptsByTags(tag);

        log.debug("태그 검색 response => {}", response);

        String scopeTag = extractScopeTag(filter);

        List<InfPromptRes> content = Optional.ofNullable(response)
                .map(PromptFilterByTagsResponse::getData)
                .orElse(Collections.emptyList())
                .stream()
                .map(inferPromptMapper::from)
                .filter(prompt -> matchesScopeTag(prompt, scopeTag))
                .toList();

        // 각 프롬프트에 대해 연결된 에이전트 수 조회 및 매핑
        for (InfPromptRes prompt : content) {
            try {
                // todo 확인 필요
                // Lineage 조회 (upstream 방향으로)
                List<LineageRelationWithTypes> lineageRelations = sktaiLineageClient.getLineageByObjectKeyAndDirection(
                        prompt.getUuid(),
                        "upstream",
                        ActionType.USE.getValue(),
                        5);

                // 프롬프트 타입만 필터링하여 개수 계산
                long agentGraphCount = lineageRelations.stream()
                        .filter(relation -> ObjectType.AGENT_GRAPH.equals(relation.getSourceType()))
                        .count();

                prompt.setConnectedAgentCount((int) agentGraphCount);

            } catch (FeignException e) {
                log.warn("InferPrompt {} Lineage 조회 실패 (FeignException): {}", prompt.getUuid(), e.getMessage());
                // Lineage 조회 실패 시 기본값 설정
                prompt.setConnectedAgentCount(0);
            } catch (RuntimeException e) {
                log.warn("InferPrompt {} Lineage 조회 실패 (RuntimeException): {}", prompt.getUuid(), e.getMessage());
                // Lineage 조회 실패 시 기본값 설정
                prompt.setConnectedAgentCount(0);
            }
        }

        // Page<InfPromptRes> pageResult = new PageImpl<>(
        //         content,
        //         PageRequest.of(0, Math.max(content.size(), 1)),
        //         content.size());
        //
        // return PageResponse.from(pageResult);

        return PaginationUtils.toPageResponseFromAdxp(response.getPayload(), content);
    }

    @Override
    public InfPromptByIdRes getInfPromptById(String promptUuid) {
        InfPromptByIdRes result = null;

        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptById ]");
        log.info("promptUuid           : {}", promptUuid);

        PromptResponse response = sktaiAgentInferencePromptsService.getInferencePrompt(promptUuid);

        GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                .findByAsstUrl("/api/v1/agent/inference-prompts/" + promptUuid).orElse(null);
        int fstPrjSeq = -999;
        int lstPrjSeq = -999;

        if (existing != null) {
            fstPrjSeq = existing.getFstPrjSeq();
            lstPrjSeq = existing.getLstPrjSeq();
        }

        log.debug("getInfPromptById response => {}", response);

        result = inferPromptMapper.from(response);
        result.setFstPrjSeq(fstPrjSeq);
        result.setLstPrjSeq(lstPrjSeq);

        return result;
    }

    @Override
    public InfPromptVerListByIdRes getInfPromptVerListById(String promptUuid) {
        InfPromptVerListByIdRes result = null;

        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptVerListById ]");
        log.info("promptUuid            : {}", promptUuid);

        PromptVersionsResponse response = sktaiAgentInferencePromptsService.getInferencePromptVersions(promptUuid);

        log.debug("getInfPromptVerListById response => {}", response);

        List<PromptVersionsResponse.VersionData> data = response.getData();

        for (PromptVersionsResponse.VersionData versionData : data) {
            usersMasRepository.findByUuid(versionData.getCreatedBy()).ifPresent(
                    createUser -> versionData.setCreatedBy(createUser.getJkwNm() + " | " + createUser.getDeptNm()));
        }

        result = inferPromptMapper.from(response);
        return result;
    }

    @Override
    public InfPromptLatestByIdRes getInfPromptLatestVerById(String promptUuid) {
        InfPromptLatestByIdRes result = null;

        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptLatestVerById ]");
        log.info("promptUuid            : {}", promptUuid);

        PromptVersionResponse response = sktaiAgentInferencePromptsService.getLatestInferencePromptVersion(promptUuid);

        log.debug("getInfPromptLatestVerById response => {}", response);

        result = inferPromptMapper.from(response);
        return result;
    }

    @Override
    public InfPromptMsgsByIdRes getInfPromptMsgsById(String versionUuid) {
        InfPromptMsgsByIdRes result = null;

        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptMsgsById ]");
        log.info("versionUuid            : {}", versionUuid);

        PromptMessagesResponse response = sktaiAgentInferencePromptsService.getInferencePromptMessages(versionUuid);

        log.debug("getInfPromptMsgsById response => {}", response);

        result = inferPromptMapper.from(response);
        return result;
    }

    @Override
    public InfPromptVarsByIdRes getInfPromptVarsById(String versionUuid) {
        InfPromptVarsByIdRes result = null;

        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptVarsById ]");
        log.info("versionUuid            : {}", versionUuid);

        try {
            PromptVariablesResponse response = sktaiAgentInferencePromptsService.getInferencePromptVariables(versionUuid);
            log.debug("getInfPromptVarsById response => {}", response);

            result = inferPromptMapper.from(response);
        } catch (Exception e) {
            result = inferPromptMapper.from(new PromptVariablesResponse());
        }

        return result;
    }

    @Override
    public InfPromptTagsListByIdRes getInfPromptTagById(String versionUuid) {
        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptTagById ]");
        log.info("versionUuid            : {}", versionUuid);

        PromptTagsResponse response = sktaiAgentInferencePromptsService.getInferencePromptTagsByVersion(versionUuid);

        log.debug("sktaiAgentInferencePromptsService.getInfPromptTagById => {}", response);

        InfPromptTagsListByIdRes result = inferPromptMapper.from(response);
        return result;
    }

    @Override
    public void deleteInfPromptById(String promptUuid) {

        log.info("추론프롬프트 삭제 요청: promptUuid={}", promptUuid);

        try {
            // Few-Shot API 호출 전에 토큰 확인 및 갱신

            sktaiAgentInferencePromptsService.deleteInferencePrompt(promptUuid);

            log.info("추론프롬프트 삭제 성공: promptUuid={}", promptUuid);

        } catch (FeignException e) {
            log.error("추론프롬프트 삭제 실패: promptUuid={}, 에러={}", promptUuid, e.getMessage());
            log.debug("추론프롬프트 삭제 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("추론프롬프트를 삭제할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public InfPromptCreateRes createInfPrompt(InfPromptCreateReq request) {
        log.info("[ Execute Service InferPromptServiceImpl.createInfPrompt ]");
        log.info("Request DTO : {}", request);

        try {
            // InfPromptCreateReq를 PromptCreateRequest로 변환
            PromptCreateRequest createRequest = inferPromptMapper.toNewCreateRequest(request);

            // 프롬프트 생성
            PromptCreateResponse createResponse = sktaiAgentInferencePromptsService.createInferencePrompt(createRequest);

            // 프롬프트 정책 부여
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/" + createResponse.getPromptUuid());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/lineages/" + createResponse.getPromptUuid() + "/upstream");
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/prompt/" + createResponse.getPromptUuid());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/versions/" + createResponse.getPromptUuid());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/versions/" + createResponse.getPromptUuid() + "/latest");

            // 프롬프트 버전 uuid 획득
            // PromptVersionResponse inferencePromptVersions = sktaiAgentInferencePromptsService.getLatestInferencePromptVersion(createResponse.getPromptUuid());

            // 프롬프트 버전 정책 부여
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/variables/" + inferencePromptVersions.getData().getVersionId());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/messages/" + inferencePromptVersions.getData().getVersionId());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/inference-prompts/tags/" + inferencePromptVersions.getData().getVersionId());

            // 응답을 InfPromptCreateRes로 변환
            InfPromptCreateRes infPromptCreateRes = InfPromptCreateRes.builder()
                    .promptUuid(Objects.requireNonNull(createResponse).getPromptUuid())
                    .build();

            log.info("추론프롬프트 생성 성공: promptUuid={}", infPromptCreateRes.getPromptUuid());

            return infPromptCreateRes;

        } catch (FeignException e) {
            log.error("추론프롬프트 생성 실패: name={}, 에러={}",
                    request != null ? request.getName() : "unknown", e.getMessage());
            log.debug("추론프롬프트 생성 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("추론프롬프트를 생성할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateInfPromptById(String promptUuid, InfPromptUpdateReq request) {
        try {
            // InfPromptUpdateReq를 PromptUpdateRequest로 변환
            PromptUpdateRequest updateRequest = inferPromptMapper.toNewUpdateRequest(promptUuid, request);

            sktaiAgentInferencePromptsService.updateInferencePrompt(promptUuid, updateRequest);

            // 버전 uuid 획득
            // PromptVersionResponse inferencePromptVersions = sktaiAgentInferencePromptsService.getLatestInferencePromptVersion(promptUuid);

            // 현재 에셋의 프로젝트 seq 조회
            // GpoAssetPrjMapMas gpoAssetPrjMapMas = assetPrjMapMasRepository
            //         .findByAsstUrl("/api/v1/agent/inference-prompts/" + promptUuid)
            //         .orElseThrow(() -> new RuntimeException("유호하지 않은 추론프롬프트 UUID"));

            // 버전 ADXP 권한부여
            // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/variables/" + inferencePromptVersions.getData().getVersionId(), gpoAssetPrjMapMas.getLstPrjSeq());
            // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/messages/" + inferencePromptVersions.getData().getVersionId(), gpoAssetPrjMapMas.getLstPrjSeq());
            // adminAuthService.setResourcePolicyByProjectSequence("/api/v1/agent/inference-prompts/tags/" + inferencePromptVersions.getData().getVersionId(), gpoAssetPrjMapMas.getLstPrjSeq());

            log.info("추론프롬프트 수정 성공: promptUuid={}", promptUuid);

        } catch (FeignException e) {
            log.error("추론프롬프트 수정 실패: promptUuid={}, 에러={}", promptUuid, e.getMessage());
            log.debug("추론프롬프트 수정 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("추론프롬프트를 수정할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public InfPromptBuiltinRes getInfPromptBuiltin() {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service InferPromptServiceImpl.getInfPromptBuiltin ]");
        log.info("-----------------------------------------------------------------------------------------");

        try {
            BuiltinPromptsResponse builtinResponse = sktaiAgentInferencePromptsService
                    .getBuiltinInferencePromptTemplates();
            log.debug("sktaiAgentInferencePromptsService.getInfPromptBuiltin => {}", builtinResponse);

            // BuiltinPromptsResponse를 InfPromptBuiltinRes로 변환
            InfPromptBuiltinRes infPromptBuiltinRes = InfPromptBuiltinRes.builder()
                    .data(convertBuiltinPrompts(builtinResponse.getData()))
                    .build();

            log.info("추론프롬프트 내장 템플릿 목록 조회 성공");

            return infPromptBuiltinRes;

        } catch (FeignException e) {
            log.error("추론프롬프트 내장 템플릿 목록 조회 실패: 에러={}", e.getMessage());
            log.debug("추론프롬프트 내장 템플릿 목록 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("추론프롬프트 내장 템플릿 목록 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    // ================= Builtin Prompt 변환 메서드 =================

    private List<InfPromptBuiltinRes.BuiltinPrompt> convertBuiltinPrompts(
            List<BuiltinPromptsResponse.BuiltinPrompt> prompts) {
        if (prompts == null) {
            return null;
        }

        return prompts.stream()
                .map(prompt -> InfPromptBuiltinRes.BuiltinPrompt.builder()
                        .name(prompt.getName())
                        .uuid(prompt.getUuid())
                        .messages(convertBuiltinMessages(prompt.getMessages()))
                        .variables(convertBuiltinVariables(prompt.getVariables()))
                        .build())
                .toList();
    }

    private List<InfPromptBuiltinRes.Message> convertBuiltinMessages(List<BuiltinPromptsResponse.Message> messages) {
        if (messages == null) {
            return null;
        }

        return messages.stream()
                .map(msg -> InfPromptBuiltinRes.Message.builder()
                        .mtype(msg.getMtype())
                        .message(msg.getMessage())
                        .build())
                .toList();
    }

    private List<InfPromptBuiltinRes.Variable> convertBuiltinVariables(
            List<BuiltinPromptsResponse.Variable> variables) {
        if (variables == null) {
            return null;
        }

        return variables.stream()
                .map(var -> InfPromptBuiltinRes.Variable.builder()
                        .variable(var.getVariable())
                        .validationFlag(var.isValidationFlag())
                        .validation(var.getValidation())
                        .tokenLimitFlag(var.isTokenLimitFlag())
                        .tokenLimit(var.getTokenLimit())
                        .build())
                .toList();
    }

    // ================= Helper Methods =================

    /**
     * 프로젝트 범위 응답을 페이지 단위로 순회하며 태그를 집계한다.
     */
    private Set<String> collectDistinctPromptTags(String projectId, String filter) {
        String effectiveFilter = (filter != null && !filter.isBlank()) ? filter : DEFAULT_INFER_PROMPT_FILTER;

        int currentPage = 1;
        Set<String> tagAccumulator = new LinkedHashSet<>();
        boolean hasNext = true;

        while (hasNext) {
            PromptsResponse response = sktaiAgentInferencePromptsService.getInferencePrompts(
                    projectId,
                    currentPage,
                    TAG_FETCH_PAGE_SIZE,
                    null,
                    effectiveFilter,
                    null,
                    false);

            if (response == null) {
                break;
            }

            List<InfPromptRes> content = toSanitizedPromptSummaries(response.getData());

            content.stream()
                    .flatMap(prompt -> Optional.ofNullable(prompt.getTags())
                            .orElse(Collections.emptyList())
                            .stream())
                    .map(InfPromptRes.TagInfo::getTag)
                    .filter(tag -> tag != null && !tag.isBlank())
                    .forEach(tagAccumulator::add);

            Pagination pagination = Optional.ofNullable(response.getPayload())
                    .map(PromptsResponse.Payload::getPagination)
                    .orElse(null);

            if (pagination != null && pagination.getPage() != null && pagination.getLastPage() != null
                    && pagination.getPage() < pagination.getLastPage()) {
                currentPage = pagination.getPage() + 1;
            } else {
                hasNext = false;
            }
        }

        return tagAccumulator;
    }


    /**
     * 목록 요약 응답을 내부 DTO로 변환한다.
     */
    private List<InfPromptRes> toSanitizedPromptSummaries(List<PromptsResponse.PromptSummary> summaries) {
        return Optional.ofNullable(summaries)
                .orElse(Collections.emptyList())
                .stream()
                .map(inferPromptMapper::from)
                .toList();
    }

    private String extractScopeTag(String filter) {
        if (!StringUtils.hasText(filter)) {
            return null;
        }
        if (filter.startsWith("tags:") && filter.length() > 5) {
            return filter.substring(5);
        }
        return null;
    }

    private boolean matchesScopeTag(InfPromptRes prompt, String scopeTag) {
        if (!StringUtils.hasText(scopeTag)) {
            return true;
        }
        return Optional.ofNullable(prompt.getTags())
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(tagInfo -> scopeTag.equalsIgnoreCase(tagInfo.getTag()));
    }

    private List<PromptCreateRequest.PromptMessage> convertMessages(
            List<?> messages) {
        if (messages == null) {
            return null;
        }

        return messages.stream()
                .map(msg -> {
                    if (msg instanceof InfPromptCreateReq.Message createMsg) {
                        return PromptCreateRequest.PromptMessage.builder()
                                .message(createMsg.getMessage())
                                .mtype(createMsg.getMtype())
                                .build();
                    } else if (msg instanceof InfPromptUpdateReq.Message updateMsg) {
                        return PromptCreateRequest.PromptMessage.builder()
                                .message(updateMsg.getMessage())
                                .mtype(updateMsg.getMtype())
                                .build();
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<PromptCreateRequest.PromptTag> convertTags(
            List<?> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(tag -> {
                    if (tag instanceof InfPromptCreateReq.Tag createTag) {
                        return PromptCreateRequest.PromptTag.builder()
                                .tag(createTag.getTag())
                                .build();
                    } else if (tag instanceof InfPromptUpdateReq.Tag updateTag) {
                        return PromptCreateRequest.PromptTag.builder()
                                .tag(updateTag.getTag())
                                .build();
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<PromptCreateRequest.PromptVariable> convertVariables(List<?> variables) {
        if (variables == null) {
            return null;
        }

        return variables.stream()
                .map(var -> {
                    if (var instanceof InfPromptCreateReq.Variable createVar) {
                        return PromptCreateRequest.PromptVariable.builder()
                                .variable(createVar.getVariable())
                                .validation(createVar.getValidation())
                                .validationFlag(createVar.isValidationFlag())
                                .tokenLimitFlag(createVar.isTokenLimitFlag())
                                .tokenLimit(createVar.getTokenLimit())
                                .build();
                    } else if (var instanceof InfPromptUpdateReq.Variable updateVar) {
                        return PromptCreateRequest.PromptVariable.builder()
                                .variable(updateVar.getVariable())
                                .validation(updateVar.getValidation())
                                .validationFlag(updateVar.isValidationFlag())
                                .tokenLimitFlag(updateVar.isTokenLimitFlag())
                                .tokenLimit(updateVar.getTokenLimit())
                                .build();
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    // ================= Update Helper Methods =================

    private List<PromptUpdateRequest.PromptMessage> convertMessagesForUpdate(
            List<InfPromptUpdateReq.Message> messages) {
        if (messages == null) {
            return null;
        }

        return messages.stream()
                .map(msg -> PromptUpdateRequest.PromptMessage.builder()
                        .message(msg.getMessage())
                        .mtype(msg.getMtype())
                        .build())
                .toList();
    }

    private List<PromptUpdateRequest.PromptTag> convertTagsForUpdate(List<InfPromptUpdateReq.Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }

        return tags.stream()
                .map(tag -> PromptUpdateRequest.PromptTag.builder()
                        .tag(tag.getTag())
                        .build())
                .toList();
    }

    private List<PromptUpdateRequest.PromptVariable> convertVariablesForUpdate(
            List<InfPromptUpdateReq.Variable> variables) {
        if (variables == null) {
            return null;
        }

        return variables.stream()
                .map(var -> PromptUpdateRequest.PromptVariable.builder()
                        .variable(var.getVariable())
                        .validation(var.getValidation())
                        .validationFlag(var.isValidationFlag())
                        .tokenLimitFlag(var.isTokenLimitFlag())
                        .tokenLimit(var.getTokenLimit())
                        .build())
                .toList();
    }

    private String nvl(String s) {
        return (s == null) ? "" : s;
    }

    @Override
    public PromptVersionResponse getVersion(String promptUuid) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service InferPromptServiceImpl.getVersion ]");
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
                    createdAt = DateUtils.utcToKstDateTimeString(version1Data.getCreatedAt());

                    // version이 1개만 있는 경우 updated 정보는 빈값
                    if (versions.size() == 1) {
                        updatedBy = null;
                        updatedAt = null;
                    } else {
                        // 여러 버전이 있는 경우 최신 버전의 정보를 updated로 사용
                        updatedBy = latestVersion.getCreatedBy();
                        updatedAt = DateUtils.utcToKstDateTimeString(latestVersion.getCreatedAt());
                    }
                } else {
                    // version 1이 없는 경우 (이상한 상황)
                    log.warn("version 1을 찾을 수 없습니다. promptUuid: {}", promptUuid);
                    createdBy = null;
                    createdAt = null;
                    updatedBy = latestVersion.getCreatedBy();
                    updatedAt = DateUtils.utcToKstDateTimeString(latestVersion.getCreatedAt());
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
                        .traceId(versionsResponse.getTraceId())
                        .data(versionData)
                        .payload(versionsResponse.getPayload())
                        .build();
            }
            return null;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("promptUuid={} > 프롬프트 버전 조회 실패", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "프롬프트 버전 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<InfPromptLineageRes> getInfPromptLineageRelations(String promptUuid, Integer page,
                                                                          Integer size) {
        try {
            // 기본값 설정
            if (page == null)
                page = 1;
            if (size == null)
                size = 6;

            // 화면에서는 1부터 시작하므로 0부터 시작하는 백엔드 로직에 맞게 변환
            int backendPage = page - 1;

            // 추론 프롬프트 ID로 Lineage 조회 (upstream 방향으로 - 추론 프롬프트에서 나가는 관계)
            List<LineageRelationWithTypes> lineageRelations = sktaiLineageClient.getLineageByObjectKeyAndDirection(
                    promptUuid,
                    "upstream",
                    ActionType.USE.getValue(),
                    5);

            log.info("InfPrompt {} - Lineage 관계 개수: {}", promptUuid, lineageRelations.size());

            // PROMPT 타입만 필터링
            List<LineageRelationWithTypes> agentGraphRelations = lineageRelations.stream()
                    .filter(relation -> ObjectType.AGENT_GRAPH.equals(relation.getSourceType()))
                    .collect(Collectors.toList());

            log.info("@@ agentGraphRelations: {}", agentGraphRelations);

            // Lineage 관계에서 sourceKey(PROMPT ID)를 추출하여 Prompt 정보 조회
            List<InfPromptLineageRes> allRelations = new ArrayList<>();
            for (LineageRelationWithTypes relation : agentGraphRelations) {
                try {
                    // sourceKey는 PROMPT ID라고 가정하고 Prompt 정보 조회
                    String agentGraphId = relation.getSourceKey();

                    // AgentGraph 상세 정보 조회
                    GraphResponse graph = sktaiAgentGraphsService.getGraph(agentGraphId);

                    if (graph != null) {
                        boolean deployed = false; // 배포 여부
                        try {
                            GraphAppResponse graphAppResponse = sktaiAgentGraphsService.getGraphAppInfo(agentGraphId);
                            deployed = graphAppResponse != null && graphAppResponse.getData() != null;
                        } catch (FeignException e) {
                            log.debug("Graph {} App 정보 조회 실패 (FeignException, 배포되지 않음): {}", agentGraphId,
                                    e.getMessage());
                            deployed = false;
                        } catch (RuntimeException e) {
                            log.debug("Graph {} App 정보 조회 실패 (RuntimeException, 배포되지 않음): {}", agentGraphId,
                                    e.getMessage());
                            deployed = false;
                        }

                        InfPromptLineageRes agentGraphLineageRes = InfPromptLineageRes.builder()
                                .id(graph.getId())
                                .name(graph.getName())
                                .description(graph.getDescription())
                                .deployed(deployed)
                                .createdAt(graph.getCreatedAt() != null ? DateUtils.utcToKstDateTimeString(OffsetDateTime.parse(graph.getCreatedAt()).toLocalDateTime()) : null)
                                .updatedAt(graph.getUpdatedAt() != null ? DateUtils.utcToKstDateTimeString(OffsetDateTime.parse(graph.getUpdatedAt()).toLocalDateTime()) : null)
                                .build();

                        allRelations.add(agentGraphLineageRes);
                    }
                } catch (FeignException e) {
                    log.warn("Prompt {} 정보 조회 실패 (FeignException): {}", relation.getSourceKey(), e.getMessage());
                } catch (RuntimeException e) {
                    log.warn("Prompt {} 정보 조회 실패 (RuntimeException): {}", relation.getSourceKey(), e.getMessage());
                }
            }

            // 페이징 처리
            int totalElements = allRelations.size();
            int startIndex = backendPage * size;
            int endIndex = Math.min(startIndex + size, totalElements);

            List<InfPromptLineageRes> pagedRelations;
            if (startIndex >= totalElements) {
                pagedRelations = Collections.emptyList();
            } else {
                pagedRelations = allRelations.subList(startIndex, endIndex);
            }

            // Page 객체 생성
            Page<InfPromptLineageRes> result = new PageImpl<>(
                    pagedRelations,
                    PageRequest.of(backendPage, size),
                    totalElements);

            return PageResponse.from(result);

        } catch (FeignException e) {
            log.error("추론 프롬프트 Lineage 조회 실패 (FeignException): promptUuid={}, 에러={}", promptUuid, e.getMessage());
            // 에러 시 빈 페이지 반환
            Page<InfPromptLineageRes> emptyPage = new PageImpl<>(
                    Collections.emptyList(),
                    PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 6),
                    0);

            return PageResponse.from(emptyPage);
        } catch (RuntimeException e) {
            log.error("추론 프롬프트 Lineage 조회 실패 (RuntimeException): promptUuid={}, 에러={}", promptUuid, e.getMessage());
            // 에러 시 빈 페이지 반환
            Page<InfPromptLineageRes> emptyPage = new PageImpl<>(
                    Collections.emptyList(),
                    PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 6),
                    0);

            return PageResponse.from(emptyPage);
        }
    }

    @Override
    public List<PolicyRequest> setInferPromptPolicy(String promptUuid, String memberId, String projectName) {
        log.info("[ Execute Service InferPromptServiceImpl.setInferPromptPolicy ]");
        log.info("promptUuid            : {}", promptUuid);
        log.info("memberId              : {}", memberId);
        log.info("projectName           : {}", projectName);

        // promptUuid 검증
        if (!StringUtils.hasText(promptUuid)) {
            log.error("추론 프롬프트 Policy 설정 실패 - promptUuid가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "추론 프롬프트 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("추론 프롬프트 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("추론 프롬프트 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // 프롬프트 정책 부여
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/" + promptUuid, memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/lineages/" + promptUuid + "/upstream", memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/prompt/" + promptUuid, memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/versions/" + promptUuid, memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/versions/" + promptUuid + "/latest", memberId, projectName);

            // 프롬프트 버전 uuid 획득
            // PromptVersionResponse inferencePromptVersions = sktaiAgentInferencePromptsService.getLatestInferencePromptVersion(promptUuid);

            // 프롬프트 버전 정책 부여
            // if (inferencePromptVersions != null && inferencePromptVersions.getData() != null) {
            //     String versionId = inferencePromptVersions.getData().getVersionId();
            //     adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/variables/" + versionId, memberId, projectName);
            //     adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/messages/" + versionId, memberId, projectName);
            //     adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/inference-prompts/tags/" + versionId, memberId, projectName);
            // }

            String resourceUrl = "/api/v1/agent/inference-prompts/" + promptUuid;
            log.info("추론 프롬프트 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("추론 프롬프트 Policy 조회 결과가 null - promptUuid: {}, resourceUrl: {}", promptUuid, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "추론 프롬프트 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
            }

            // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
            List<PolicyRequest> filteredPolicy = policy.stream()
                    .filter(policyReq -> {
                        if (policyReq.getPolicies() != null) {
                            // policies에 type이 "role"인 항목이 있는지 확인
                            return policyReq.getPolicies().stream()
                                    .noneMatch(p -> "role".equals(p.getType()));
                        }
                        return true; // policies가 null이면 포함
                    })
                    .collect(Collectors.toList());

            log.info("추론 프롬프트 Policy 설정 완료 - promptUuid: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", promptUuid, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("추론 프롬프트 Policy 설정 실패 (BusinessException) - promptUuid: {}, errorCode: {}", promptUuid, e.getErrorCode(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("추론 프롬프트 Policy 설정 실패 (RuntimeException) - promptUuid: {}, error: {}", promptUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "추론 프롬프트 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("추론 프롬프트 Policy 설정 실패 (Exception) - promptUuid: {}", promptUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "추론 프롬프트 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }

}
