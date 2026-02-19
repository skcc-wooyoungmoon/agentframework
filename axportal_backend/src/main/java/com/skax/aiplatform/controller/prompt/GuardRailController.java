package com.skax.aiplatform.controller.prompt;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.agent.dto.response.GuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionResponse;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.GuardRailCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailDeleteReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailUpdateReq;
import com.skax.aiplatform.dto.prompt.response.GuardRailCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailDeleteRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailDetailRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptMsgsByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptTagsListByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.service.prompt.GuardRailPromptService;
import com.skax.aiplatform.service.prompt.GuardRailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 가드레일 컨트롤러
 *
 * <p>
 * 가드레일 프롬프트 및 가드레일 관리 API를 제공합니다.
 * 가드레일 프롬프트와 가드레일의 생성, 조회, 수정, 삭제(CRUD) 기능을 포함합니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/guardrails")
@RequiredArgsConstructor
@Tag(name = "GuardRail Management", description = "가드레일 관리 API")
public class GuardRailController {

    private final GuardRailPromptService guardRailPromptService;
    private final GuardRailService guardRailService;
    private final GpoUsersMasRepository gpoUsersMasRepository;

    // ==================== 가드레일 프롬프트 CRUD ====================

    /**
     * 가드레일 프롬프트 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @param page      페이지 번호 (0부터 시작)
     * @param size      페이지 크기
     * @param search    검색어
     * @param sort      정렬 기준
     * @return 가드레일 프롬프트 목록
     */
    @GetMapping("/prompts")
    @Operation(
            summary = "가드레일 프롬프트 목록 조회",
            description = "전체 가드레일 프롬프트 목록을 조회합니다. (ptype:2로 필터링)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가드레일 프롬프트 목록 조회 성공")
    })
    public AxResponseEntity<PageResponse<GuardRailPromptRes>> getGuardRailPromptList(
            @RequestParam(value = "project_id", defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
            @Parameter(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "검색어")
            @RequestParam(required = false) String search,

            @Parameter(description = "태그")
            @RequestParam(required = false) String tag,

            @Parameter(description = "정렬 기준")
            @RequestParam(value = "sort", defaultValue = "created_at,desc") String sort
    ) {
        // filter를 ptype:2로 고정 (가드레일 프롬프트)
        // tag 파라미터가 있으면 ptype:2,tags:{tag값} 형식으로 조합
        String filter = "ptype:2";
        if (tag != null && !tag.trim().isEmpty()) {
            filter = "ptype:2,tags:" + tag.trim();
        }

        log.info("Controller: 가드레일 프롬프트 목록 조회 API 호출 - project_id: {}, page: {}, size: {}, search: {}, tag: {}, " +
                        "filter: {}, " +
                        "sort: {}",
                projectId, page, size, search, tag, filter, sort);

        // GuardRailPromptService를 사용하여 조회 (ptype:2로 필터링)
        PageResponse<GuardRailPromptRes> response = guardRailPromptService.getGuardRailPromptList(projectId, page, size,
                tag, search, sort, filter);

        if (response == null) {
            log.warn("가드레일 프롬프트 목록 조회 실패: 서비스에서 null을 반환했습니다.");
            throw new RuntimeException("가드레일 프롬프트 목록을 조회할 수 없습니다.");
        }
        /*
        if (response.getContent() != null) {
            response.getContent().stream()
                    .filter(prompt -> prompt != null && hasText(prompt.getUuid()))
                    .forEach(prompt -> {
                        String resolvedUpdatedAt = resolveUpdatedAt(prompt.getUuid());
                        String adjustedUpdatedAt = adjustToKst(resolvedUpdatedAt);
                        if (hasText(adjustedUpdatedAt)) {
                            prompt.setUpdatedAt(adjustedUpdatedAt);
                        }
                    });
        }
        */

        return AxResponseEntity.okPage(response, "가드레일 프롬프트 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 가드레일 프롬프트 태그 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @return 가드레일 프롬프트 태그 목록 (중복 제거됨)
     */
    @GetMapping("/prompts/tags")
    @Operation(
            summary = "가드레일 프롬프트 태그 목록 조회",
            description = "전체 가드레일 프롬프트의 태그를 중복 제거하여 조회합니다. (ptype:2로 필터링)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가드레일 프롬프트 태그 목록 조회 성공")
    })
    public AxResponseEntity<List<String>> getGuardRailPromptTagList(
            @RequestParam(value = "project_id", defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32")
            @Parameter(description = "프로젝트 ID", example = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId
    ) {
        log.info("가드레일 프롬프트 태그 목록 조회 - project_id: {}", projectId);

        // filter만 사용하여 ptype:2로 필터링 조회 (tag 파라미터는 null로 설정하여 태그 검색 API 우회)
        String filter = "ptype:2";

        PageResponse<GuardRailPromptRes> response = guardRailPromptService.getGuardRailPromptList(
                projectId,
                1,  // page
                100,  // size
                null,  // tag 파라미터는 null로 설정 (태그 검색 API 우회, filter만 사용)
                null,  // search
                "created_at,desc",  // sort
                filter
        );

        // 모든 태그를 추출하고 중복 제거 (TagInfo에서 tag 문자열만 추출)
        List<String> tags = response.getContent().stream()
                .flatMap(prompt -> prompt.getTags() != null ? prompt.getTags().stream() : Stream.empty())
                .map(GuardRailPromptRes.TagInfo::getTag)
                .filter(tag -> tag != null && !tag.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        log.info("가드레일 프롬프트 태그 목록 조회 완료 - 총 {} 개의 고유 태그", tags.size());
        return AxResponseEntity.ok(tags, "가드레일 프롬프트 태그 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 가드레일 프롬프트 상세 조회
     *
     * @param id 가드레일 프롬프트 ID
     * @return 가드레일 프롬프트 상세 정보
     */
    @GetMapping("/prompts/{id}")
    @Operation(
            summary = "가드레일 프롬프트 상세 조회",
            description = "특정 가드레일 프롬프트의 상세 정보를 조회합니다. 목록 조회를 통해 version_id를 자동으로 추출하여 상세 정보를 가져옵니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가드레일 프롬프트 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "가드레일 프롬프트를 찾을 수 없음")
    })
    public AxResponseEntity<GuardRailPromptByIdRes> getGuardRailPromptById(
            @PathVariable("id")
            @Parameter(description = "가드레일 프롬프트 ID") String id
    ) {
        log.info("가드레일 프롬프트 상세 조회 - id: {}", id);

        // GuardRailPromptService를 사용하여 기본 정보 조회
        GuardRailPromptByIdRes response = guardRailPromptService.getGuardRailPromptById(id);

        // null 체크
        if (response == null) {
            log.error("getInfPromptById 결과가 null입니다 - id: {}", id);
            throw new RuntimeException("프롬프트 정보를 찾을 수 없습니다.");
        }

        // 기본 정보 조회 결과 로그
        log.info("getInfPromptById 결과 - uuid: {}, name: {}, projectId: {}, message: {}, tags: {}",
                response.getUuid(), response.getName(), response.getProjectId(),
                response.getMessage() != null ? "있음" : "null",
                response.getTags() != null ? response.getTags().size() + " tags" : "null");

        // projectId null 체크
        String projectId = response.getProjectId();
        if (projectId == null || projectId.isBlank()) {
            log.warn("projectId가 null 또는 비어있습니다. 기본값 사용");
            projectId = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32"; // 기본 프로젝트 ID
        }

        // Version API를 통해 직접 version_id 추출 (더 신뢰성 있음)
        String versionId = null;
        try {
            PromptVersionResponse versionResponse = guardRailPromptService.getVersion(id);
            if (versionResponse != null && versionResponse.getData() != null) {
                versionId = versionResponse.getData().getVersionId();
                log.info("Version API를 통한 versionId 추출 성공 - promptId: {}, versionId: {}", id, versionId);
            } else {
                log.warn("Version API 응답이 null이거나 data가 없음 - promptId: {}", id);
            }
        } catch (BusinessException e) {
            log.warn("Version API 호출 실패 (BusinessException) - promptId: {}, errorCode: {}", id, e.getErrorCode(), e);
        } catch (RuntimeException e) {
            log.warn("Version API 호출 실패, 목록 조회로 폴백 - promptId: {}, error: {}", id, e.getMessage());
        }

        // Version API에서 versionId를 찾지 못했으면 목록 조회로 폴백
        if (versionId == null || versionId.isBlank()) {
            log.info("목록 조회를 통한 폴백 시작 - promptId: {}", id);
            versionId = extractVersionIdFromList(projectId, id);
        }

        // version_id가 있으면 해당 버전의 상세 정보 조회
        if (versionId != null && !versionId.isBlank()) {
            try {
                GuardRailPromptMsgsByIdRes messages = guardRailPromptService.getGuardRailPromptMsgsById(versionId);
                if (messages != null && messages.getMessages() != null && !messages.getMessages().isEmpty()) {
                    String firstMessage = messages.getMessages().get(0).getMessage();
                    response.setMessage(firstMessage);
                    log.debug("메시지 추가 (version_id: {})", versionId);
                }
            } catch (BusinessException e) {
                log.warn("메시지 조회 실패 (BusinessException) - version_id: {}, errorCode: {}", versionId, e.getErrorCode()
                        , e);
            } catch (RuntimeException e) {
                log.warn("메시지 조회 실패 (version_id: {}): {}", versionId, e.getMessage());
            }

            try {
                GuardRailPromptTagsListByIdRes tagsList = guardRailPromptService.getGuardRailPromptTagById(versionId);

                if (tagsList != null && tagsList.getTags() != null) {
                    String finalVersionId = versionId;
                    List<GuardRailPromptRes.TagInfo> tags = tagsList.getTags().stream()
                            .map(promptTag -> GuardRailPromptRes.TagInfo.builder()
                                    .tag(promptTag.getTag())
                                    .versionId(finalVersionId)
                                    .build())
                            .collect(Collectors.toList());
                    response.setTags(tags);
                    log.debug("태그 추가 (version_id: {}): {} 개", versionId, tags.size());
                }
            } catch (BusinessException e) {
                log.warn("태그 조회 실패 (BusinessException) - version_id: {}, errorCode: {}", versionId, e.getErrorCode(),
                        e);
            } catch (RuntimeException e) {
                log.warn("태그 조회 실패 (version_id: {}): {}", versionId, e.getMessage());
            }

            // 담당자 조회 (프롬프트 ID 사용)
            try {
                PromptVersionResponse versionResponse = guardRailPromptService.getVersion(id);
                if (versionResponse != null && versionResponse.getData() != null) {
                    String createdBy = versionResponse.getData().getCreatedBy();
                    String createdAt = versionResponse.getData().getCreatedAt();
                    String updatedBy = versionResponse.getData().getUpdatedBy();
                    String updatedAt = versionResponse.getData().getUpdatedAt();

                    response.setCreatedBy(createdBy);
                    response.setCreatedAt(createdAt);
                    response.setUpdatedBy(updatedBy);
                    response.setUpdatedAt(updatedAt);

                    if (createdBy != null && !createdBy.isBlank()) {
                        try {
                            GpoUsersMas createdByUser = gpoUsersMasRepository.findByUuid(createdBy).orElse(null);
                            if (createdByUser != null) {
                                response.setCreatedByName(createdByUser.getJkwNm());
                                response.setCreatedByDepts(createdByUser.getDeptNm());
                                response.setCreatedByPos(createdByUser.getJkgpNm());
                                log.debug("생성자 사용자 정보 설정 완료 - name: {}, dept: {}, pos: {}",
                                        createdByUser.getJkwNm(), createdByUser.getDeptNm(), createdByUser.getJkgpNm());
                            } else {
                                log.warn("생성자 사용자 정보를 찾을 수 없습니다 - uuid: {}", createdBy);
                            }
                        } catch (RuntimeException e) {
                            log.warn("생성자 사용자 정보 조회 실패 - uuid: {}, error: {}", createdBy, e.getMessage());
                        }
                    }

                    if (updatedBy != null && !updatedBy.isBlank()) {
                        try {
                            GpoUsersMas updatedByUser = gpoUsersMasRepository.findByUuid(updatedBy).orElse(null);
                            if (updatedByUser != null) {
                                response.setUpdatedByName(updatedByUser.getJkwNm());
                                response.setUpdatedByDepts(updatedByUser.getDeptNm());
                                response.setUpdatedByPos(updatedByUser.getJkgpNm());
                                log.debug("수정자 사용자 정보 설정 완료 - name: {}, dept: {}, pos: {}",
                                        updatedByUser.getJkwNm(), updatedByUser.getDeptNm(), updatedByUser.getJkgpNm());
                            } else {
                                log.warn("수정자 사용자 정보를 찾을 수 없습니다 - uuid: {}", updatedBy);
                            }
                        } catch (RuntimeException e) {
                            log.warn("수정자 사용자 정보 조회 실패 - uuid: {}, error: {}", updatedBy, e.getMessage());
                        }
                    }
                }
            } catch (BusinessException e) {
                log.warn("담당자 조회 실패 (BusinessException) - promptId: {}, errorCode: {}", id, e.getErrorCode(), e);
            } catch (RuntimeException e) {
                log.warn("담당자 조회 실패 (promptId: {}): {}", id, e.getMessage());
            }
        } else {
            log.debug("version_id를 찾을 수 없어 상세 정보(메시지, 태그, 담당자)를 조회하지 않습니다.");
        }

        return AxResponseEntity.ok(response, "가드레일 프롬프트 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 목록 조회를 통해 특정 ID의 version_id 추출
     *
     * @param promptId 가드레일 프롬프트 ID
     * @return version_id (찾지 못하면 null)
     */
    private String extractVersionIdFromList(String projectId, String promptId) {
        try {
            log.debug("목록 조회를 통한 version_id 추출 시작 - projectId: {}, promptId: {}", projectId, promptId);

            // 파라미터 null 체크
            if (projectId == null || projectId.isBlank()) {
                log.error("projectId가 null 또는 비어있습니다.");
                return null;
            }
            if (promptId == null || promptId.isBlank()) {
                log.error("promptId가 null 또는 비어있습니다.");
                return null;
            }

            // 전체 가드레일 프롬프트 목록 조회 (큰 페이지 사이즈로)
            int page = 0;
            int size = 100;
            String tag = null;  // tag 파라미터는 null
            String search = null;
            String sort = "created_at,desc";
            String filter = "ptype:2";  // filter로 필터링 (목록 조회와 동일)

            log.info("extractVersionIdFromList - 파라미터: projectId={}, page={}, size={}, tag={}, search={}, sort={}, " +
                            "filter={}",
                    projectId, page, size, tag, search, sort, filter);

            PageResponse<GuardRailPromptRes> listResponse = guardRailPromptService.getGuardRailPromptList(
                    projectId, page, size, tag, search, sort, filter
            );

            // 목록에서 해당 ID와 일치하는 프롬프트 찾기
            if (listResponse.getContent() != null && !listResponse.getContent().isEmpty()) {
                for (GuardRailPromptRes prompt : listResponse.getContent()) {
                    if (prompt == null) {
                        log.warn("목록 항목이 null입니다. 건너뜀");
                        continue;
                    }

                    if (promptId.equals(prompt.getUuid())) {
                        log.info("일치하는 프롬프트 발견 - uuid: {}, name: {}", prompt.getUuid(), prompt.getName());

                        // tags에서 version_id 추출 (모든 태그 확인)
                        if (prompt.getTags() != null && !prompt.getTags().isEmpty()) {
                            log.info("태그 개수: {}", prompt.getTags().size());

                            // 모든 태그를 순회하며 versionId 찾기
                            for (int i = 0; i < prompt.getTags().size(); i++) {
                                GuardRailPromptRes.TagInfo tagInfo = prompt.getTags().get(i);

                                if (tagInfo == null) {
                                    log.debug("{}번째 태그가 null, 계속 진행", i);
                                    continue;
                                }

                                String versionId = tagInfo.getVersionId();
                                log.debug("{}번째 태그 - tag: '{}', versionId: {}",
                                        i, tagInfo.getTag(), versionId != null ? "있음" : "null");

                                // versionId가 있고 비어있지 않으면 반환
                                if (versionId != null && !versionId.isBlank()) {
                                    log.info("version_id 추출 성공 - promptId: {}, versionId: {}, tag: '{}' ({}번째)",
                                            promptId, versionId, tagInfo.getTag(), i);
                                    return versionId;
                                }
                            }

                            // 모든 태그를 확인했지만 versionId를 찾지 못함
                            log.warn("모든 {} 개의 태그를 확인했지만 versionId가 없거나 null - promptId: {}",
                                    prompt.getTags().size(), promptId);
                        } else {
                            log.warn("태그가 없음 (null 또는 empty) - promptId: {}", promptId);
                        }
                    }
                }
            } else {
                log.warn("목록 내용이 null 또는 비어있음");
            }

            log.warn("목록에서 해당 프롬프트를 찾을 수 없음 - promptId: {}", promptId);
            return null;

        } catch (BusinessException e) {
            log.error("version_id 추출 실패 (BusinessException) - promptId: {}, errorCode: {}",
                    promptId, e.getErrorCode(), e);
            return null;
        } catch (NullPointerException e) {
            log.error("version_id 추출 실패 (NullPointerException) - promptId: {}, error: {}",
                    promptId, e.getMessage(), e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("version_id 추출 실패 (IllegalArgumentException) - promptId: {}, error: {}",
                    promptId, e.getMessage(), e);
            return null;
        } catch (IndexOutOfBoundsException e) {
            log.error("version_id 추출 실패 (IndexOutOfBoundsException) - promptId: {}, error: {}",
                    promptId, e.getMessage(), e);
            return null;
        } catch (RuntimeException e) {
            log.error("version_id 추출 실패 (기타 RuntimeException) - promptId: {}, error: {}",
                    promptId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 가드레일 프롬프트 생성
     *
     * @param request 가드레일 프롬프트 생성 요청
     * @return 생성된 가드레일 프롬프트 정보
     */
    @PostMapping("/prompts")
    @Operation(
            summary = "가드레일 프롬프트 생성",
            description = "새로운 가드레일 프롬프트를 생성합니다. 자동으로 ptype:2가 설정됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가드레일 프롬프트 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public AxResponseEntity<GuardRailPromptCreateRes> createGuardRailPrompt(
            @Valid @RequestBody GuardRailPromptCreateReq request
    ) {
        log.info("가드레일 프롬프트 생성 요청 - name: {}", request.getName());

        GuardRailPromptCreateRes response = guardRailPromptService.createGuardRailPrompt(request);

        log.info("가드레일 프롬프트 생성 완료 - promptUuid: {}, tags: {}",
                response.getPromptUuid(), request.getTags());
        return AxResponseEntity.created(response, "가드레일 프롬프트가 성공적으로 생성되었습니다.");
    }

    /**
     * 가드레일 프롬프트 수정
     *
     * @param id      가드레일 프롬프트 ID
     * @param request 가드레일 프롬프트 수정 요청
     * @return 수정 완료 응답
     */
    @PutMapping("/prompts/{id}")
    @Operation(
            summary = "가드레일 프롬프트 수정",
            description = "기존 가드레일 프롬프트 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가드레일 프롬프트 수정 성공"),
            @ApiResponse(responseCode = "404", description = "가드레일 프롬프트를 찾을 수 없음")
    })
    public AxResponseEntity<Void> updateGuardRailPrompt(
            @PathVariable("id")
            @Parameter(description = "가드레일 프롬프트 ID") String id,
            @Valid @RequestBody GuardRailPromptUpdateReq request
    ) {
        log.info("가드레일 프롬프트 수정 요청 - id: {}, newName: {}", id, request.getNewName());

        // GuardRailPromptService를 사용하여 수정
        guardRailPromptService.updateGuardRailPromptById(id, request);

        log.info("가드레일 프롬프트 수정 완료 - id: {}", id);
        return AxResponseEntity.ok(null, "가드레일 프롬프트가 성공적으로 수정되었습니다.");
    }

    /**
     * 가드레일 프롬프트 삭제
     *
     * @param id 가드레일 프롬프트 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/prompts/{id}")
    @Operation(
            summary = "가드레일 프롬프트 삭제",
            description = "특정 가드레일 프롬프트를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가드레일 프롬프트 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "가드레일 프롬프트를 찾을 수 없음")
    })
    public AxResponseEntity<Void> deleteGuardRailPrompt(
            @PathVariable("id")
            @Parameter(description = "가드레일 프롬프트 ID") String id
    ) {
        log.info("가드레일 프롬프트 삭제 요청 - id: {}", id);

        // GuardRailPromptService를 사용하여 삭제
        guardRailPromptService.deleteGuardRailPromptById(id);

        log.info("가드레일 프롬프트 삭제 완료 - id: {}", id);
        return AxResponseEntity.ok(null, "가드레일 프롬프트가 성공적으로 삭제되었습니다.");
    }

    // ==================== 가드레일 CRUD ====================

    /**
     * 가드레일 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @param filter    검색 필터
     * @param search    검색어
     * @param sort      정렬 기준
     * @return 가드레일 목록
     */
    @GetMapping
    @Operation(
            summary = "가드레일 목록 조회",
            description = "전체 가드레일 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "가드레일 목록 조회 성공")
    public AxResponseEntity<PageResponse<GuardRailRes>> getGuardRailList(
            @Parameter(description = "프로젝트 ID")
            @RequestParam(value = "project_id", required = false) String projectId,

            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "size", defaultValue = "12") Integer size,

            @Parameter(description = "필터 조건", example = "")
            @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "정렬 기준")
            @RequestParam(value = "sort", defaultValue = "updated_at,desc") String sort
    ) {
        log.info("가드레일 목록 조회 - projectId: {}, page: {}, size: {}, filter: {}, search: {}, sort: {}",
                projectId, page, size, filter, search, sort);

        PageResponse<GuardRailRes> response =
                guardRailService.getGuardRailList(projectId, page, size, filter, search, sort);

        return AxResponseEntity.okPage(response, "가드레일 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 가드레일 상세 조회
     *
     * @param id 가드레일 ID
     * @return 가드레일 상세 정보
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "가드레일 상세 조회",
            description = "특정 가드레일의 상세 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "가드레일 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "가드레일을 찾을 수 없음")
    public AxResponseEntity<GuardRailDetailRes> getGuardRailById(
            @PathVariable("id") @Parameter(description = "가드레일 ID") String id
    ) {
        log.info("가드레일 상세 조회 - id: {}", id);

        GuardRailDetailRes response = guardRailService.getGuardRailById(id);

        return AxResponseEntity.ok(response, "가드레일 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 가드레일 생성
     *
     * @param request 가드레일 생성 요청
     * @return 생성된 가드레일 ID
     */
    @PostMapping
    @Operation(
            summary = "가드레일 생성",
            description = "새로운 가드레일을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "가드레일 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    public AxResponseEntity<GuardRailCreateRes> createGuardRail(
            @Valid @RequestBody GuardRailCreateReq request
    ) {
        log.info("가드레일 생성 요청 - request: {}", request);

        GuardRailCreateRes response = guardRailService.createGuardRail(request);

        return AxResponseEntity.created(response, "가드레일이 성공적으로 생성되었습니다.");
    }

    /**
     * 가드레일 수정
     *
     * @param id      가드레일 ID
     * @param request 가드레일 수정 요청
     * @return 수정된 가드레일 정보
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "가드레일 수정",
            description = "기존 가드레일 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "가드레일 수정 성공")
    @ApiResponse(responseCode = "404", description = "가드레일을 찾을 수 없음")
    public AxResponseEntity<GuardRailUpdateRes> updateGuardRail(
            @PathVariable("id") @Parameter(description = "가드레일 ID") String id,
            @Valid @RequestBody GuardRailUpdateReq request
    ) {
        log.info("가드레일 수정 요청 - id: {}, request: {}", id, request);

        GuardRailUpdateRes response = guardRailService.updateGuardRail(id, request);

        return AxResponseEntity.updated(response, "가드레일이 성공적으로 수정되었습니다.");
    }

    /**
     * 가드레일 복수 삭제
     *
     * @param request 가드레일 삭제 요청 (ID 목록)
     * @return 삭제 결과 (전체 건수, 성공 건수)
     */
    @DeleteMapping
    @Operation(
            summary = "가드레일 복수 삭제",
            description = "여러 개의 가드레일을 한 번에 삭제합니다. 일부 실패해도 계속 진행되며 성공/실패 건수를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "가드레일 복수 삭제 완료")
    public AxResponseEntity<GuardRailDeleteRes> deleteGuardRailBulk(
            @RequestBody @Valid GuardRailDeleteReq request
    ) {
        log.info("가드레일 복수 삭제 요청 - guardrailIds: {}", request.getGuardrailIds());

        GuardRailDeleteRes result = guardRailService.deleteGuardRailBulk(request);

        return AxResponseEntity.ok(result, "가드레일 복수 삭제가 완료되었습니다.");
    }

    /**
     * 가드레일 Policy 설정
     *
     * @param guardrailId 가드레일 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/{guardrail_id}/policy")
    @Operation(summary = "가드레일 Policy 설정", description = "가드레일의 Policy를 설정합니다.")
    @ApiResponse(responseCode = "200", description = "가드레일 Policy 설정 성공")
    public AxResponseEntity<List<PolicyRequest>> setGuardRailPolicy(
            @PathVariable(name = "guardrail_id") @Parameter(description = "가드레일 ID") String guardrailId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID") String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명") String projectName
    ) {
        log.info("가드레일 Policy 설정 요청 - guardrailId: {}, memberId: {}, projectName: {}",
                guardrailId, memberId, projectName);

        List<PolicyRequest> policy = guardRailService.setGuardRailPolicy(guardrailId, memberId, projectName);

        return AxResponseEntity.ok(policy, "가드레일 Policy가 성공적으로 설정되었습니다.");
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String resolveUpdatedAt(String promptUuid) {
        String updatedAt = null;
        try {
            GuardRailPromptByIdRes detail = guardRailPromptService.getGuardRailPromptById(promptUuid);
            if (detail != null && hasText(detail.getUpdatedAt())) {
                updatedAt = detail.getUpdatedAt();
            }
        } catch (BusinessException e) {
            log.warn("Prompt 상세 조회 실패 (BusinessException) - promptId: {}, errorCode: {}",
                    promptUuid, e.getErrorCode(), e);
        } catch (RuntimeException e) {
            log.warn("Prompt 상세 조회 실패 - promptId: {}, error: {}", promptUuid, e.getMessage());
        }

        if (hasText(updatedAt)) {
            return updatedAt;
        }

        try {
            PromptVersionResponse versionResponse = guardRailPromptService.getVersion(promptUuid);
            if (versionResponse != null && versionResponse.getData() != null) {
                String versionUpdatedAt = versionResponse.getData().getUpdatedAt();
                if (hasText(versionUpdatedAt)) {
                    updatedAt = versionUpdatedAt;
                }
            }
        } catch (BusinessException e) {
            log.warn("Version API 조회 실패 (BusinessException) - promptId: {}, errorCode: {}",
                    promptUuid, e.getErrorCode(), e);
        } catch (RuntimeException e) {
            log.warn("Version API 조회 실패 - promptId: {}, error: {}", promptUuid, e.getMessage());
        }

        return updatedAt;
    }

    private String adjustToKst(String utcDateTime) {
        if (!hasText(utcDateTime)) {
            return null;
        }
        try {
            OffsetDateTime parsed = OffsetDateTime.parse(utcDateTime);
            return parsed.withOffsetSameInstant(ZoneOffset.ofHours(9))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime local = LocalDateTime.parse(utcDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return local.plusHours(9)
                        .atOffset(ZoneOffset.ofHours(9))
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (DateTimeParseException ex) {
                log.warn("updatedAt 시간 변환 실패 - value: {}, error: {}", utcDateTime, ex.getMessage());
                return utcDateTime;
            }
        }
    }

}
