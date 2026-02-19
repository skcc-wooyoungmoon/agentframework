package com.skax.aiplatform.controller.prompt;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.InfPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.InfPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.response.*;
import com.skax.aiplatform.service.prompt.InferPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 추론 프롬프트 컨트롤러
 *
 * <p>추론 프롬프트를 관리합니다.
 * 추론 프롬프트 조회 항목: 프롬프트, 태그, 빌트인 템플릿, 버전
 * 추론 프롬프트 상세 조회 항목: 프롬프트, 버전, 변수, 메시지
 * 추론 프롬프트 복제, 수정, 삭제를 포함합니다.
 */
@Slf4j
@RestController
@RequestMapping("/inference-prompts")
@RequiredArgsConstructor
@Tag(name = "추론 프롬프트 관리 API")
public class InferPromptController {

    private final InferPromptService inferPromptService;

    /**
     * 추론 프롬프트 목록 조회 (페이징)
     *
     * @param projectId 프로젝트 ID
     * @param page      페이지 번호 (0부터 시작)
     * @param size      페이지 크기
     * @param sort      정렬 기준
     * @param search    검색어
     * @param tag       태그
     * @param filter    필터
     * @return 데이터셋 목록
     */
    @GetMapping
    @Operation(
            summary = "추론 프롬프트 목록",
            description = "전체 추론 프롬프트 목록을 조회합니다. (ptype:1로 필터링)"
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 목록 조회 성공")
    public AxResponseEntity<PageResponse<InfPromptRes>> getInfPromptList(
            @RequestParam(value = "project_id", defaultValue = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
            @Parameter(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "검색어")
            @RequestParam(required = false) String search,

            @Parameter(description = "태그")
            @RequestParam(required = false) String tag,

            @Parameter(description = "릴리즈 전용 여부")
            @RequestParam(required = false) Boolean release_only,

            @Parameter(description = "필터 (예: ptype:1, ptype:1,tags:tag값)", example = "ptype:1")
            @RequestParam(value = "filter", required = false, defaultValue = "ptype:1") String filter,

            @Parameter(description = "정렬 기준")
            @RequestParam(value = "sort", defaultValue = "created_at,desc") String sort
    ) {
        log.info("[컨트롤러] 추론프롬프트 목록 조회 - project_id: {}, page: {}, size: {}, search: {}, filter: {}",
                projectId, page, size, search, filter);

        PageResponse<InfPromptRes> inferPrompts = inferPromptService.getInfPromptList(projectId, page, size, tag,
                search, sort, filter, release_only);

        return AxResponseEntity.okPage(inferPrompts, "추론프롬프트 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 추론프롬프트 태그 목록 조회
     * <p>
     *
     * @return 추론프롬프트 태그 목록
     */
    @GetMapping("/tags")
    @Operation(
            summary = "추론프롬프트 태그 목록",
            description = "추론프롬프트 태그 목록을 조회한다. (ptype:1로 필터링)"
    )
    @ApiResponse(responseCode = "200", description = "추론프롬프트 목록 조회 성공")
    public AxResponseEntity<InfPromptTagsList> getInfPromptTagList(
            @RequestParam(value = "project_id", required = false, defaultValue = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
            @Parameter(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId,

            @RequestParam(value = "filter", required = false, defaultValue = "ptype:1")
            @Parameter(description = "필터 (예: ptype:1)", example = "ptype:1") String filter
    ) {
        log.info("Controller: 추론프롬프트 태그 목록 조회 API 호출 - project_id: {}, filter: {}", projectId, filter);
        InfPromptTagsList infPromptTagList = inferPromptService.getInfPromptTagList(projectId, filter);
        return AxResponseEntity.ok(infPromptTagList, "추론프롬프트 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 상세 조회
     *
     * @param promptUuid 추론 프롬프트 ID(UUID)
     * @return 추론 프롬프트 상세 정보
     */
    @GetMapping("/{promptUuid}")
    @Operation(
            summary = "추론 프롬프트 상세 조회",
            description = "UUID 기반으로 특정 추론 프롬프트의 상세 정보를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트가 존재하지 않음")
    public AxResponseEntity<InfPromptByIdRes> getInfPromptById(
            @Parameter(description = "추론 프롬프트 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable("promptUuid") String promptUuid
    ) {
        log.info("Controller: 추론프롬프트 상세 조회 API 호출 - promptUuid: {}", promptUuid);

        InfPromptByIdRes inferPromptById = inferPromptService.getInfPromptById(promptUuid);

        return AxResponseEntity.ok(inferPromptById, "추론프롬프트를 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 버전 목록 조회
     *
     * @param promptUuid 추론 프롬프트 ID(UUID)
     * @return 추론 프롬프트 버전 목록
     */
    @GetMapping("versions/{promptUuid}")
    @Operation(
            summary = "추론 프롬프트 버전 목록 조회",
            description = "UUID 기반으로 특정 추론 프롬프트의 버전 목록 정보를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 버전 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트 버전 목록이 존재하지 않음")
    public AxResponseEntity<InfPromptVerListByIdRes> getInfPromptVerListById(
            @Parameter(description = "추론 프롬프트 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required =
                    true)
            @PathVariable("promptUuid") String promptUuid
    ) {
        log.info("Controller: 추론프롬프트 버전 목록 조회 API 호출 - promptUuid: {}", promptUuid);

        InfPromptVerListByIdRes infPromptVerListById;

        try {
            infPromptVerListById = inferPromptService.getInfPromptVerListById(promptUuid);
        } catch (RuntimeException re) {
            return AxResponseEntity.ok(null, "추론프롬프트 버전 목록을 성공적으로 조회했습니다.");
        } catch (Exception e) {
            return AxResponseEntity.ok(null, "추론프롬프트 버전 목록을 성공적으로 조회했습니다.");
        }

        return AxResponseEntity.ok(infPromptVerListById, "추론프롬프트 버전 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 최신 버전 조회
     *
     * @param promptUuid 추론 프롬프트 ID(UUID)
     * @return 추론 프롬프트 최신 버전 정보
     */
    @GetMapping("versions/{promptUuid}/latest")
    @Operation(
            summary = "추론 프롬프트 최신 버전 조회",
            description = "UUID 기반으로 특정 추론 프롬프트의 최신 정보를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 최신 버전 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트 최신 버전이 존재하지 않음")
    public AxResponseEntity<InfPromptLatestByIdRes> getInfPromptLatestVerById(
            @Parameter(description = "추론 프롬프트 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required =
                    true)
            @PathVariable("promptUuid") String promptUuid
    ) {
        log.info("Controller: 추론프롬프트 최신 버전 조회 API 호출 - promptUuid: {}", promptUuid);

        InfPromptLatestByIdRes infPromptVerListLtestVerById = inferPromptService.getInfPromptLatestVerById(promptUuid);

        return AxResponseEntity.ok(infPromptVerListLtestVerById, "추론프롬프트 최신 버전을 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 특정 버전 메시지 조회
     *
     * @param versionUuid 추론 프롬프트 버전 ID(UUID)
     * @return 추론 프롬프트 특정 버전 메시지 정보
     */
    @GetMapping("messages/{versionUuid}")
    @Operation(
            summary = "추론 프롬프트 특정 버전 메시지 조회",
            description = "UUID 기반으로 특정 추론 프롬프트 버전의 메시지를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 버전 메세지 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트 버전 메시지가 존재하지 않음")
    public AxResponseEntity<InfPromptMsgsByIdRes> getInfPromptMsgsById(
            @Parameter(description = "추론 프롬프트 버전 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable("versionUuid") String versionUuid
    ) {
        log.info("Controller: 추론프롬프트 버전 메시지 조회 API 호출 - id: {}", versionUuid);

        InfPromptMsgsByIdRes infPromptMsgsById = inferPromptService.getInfPromptMsgsById(versionUuid);

        return AxResponseEntity.ok(infPromptMsgsById, "추론프롬프트 특정 버전의 메시지를 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 특정 버전 변수 조회
     *
     * @param versionUuid 추론 프롬프트 버전 ID(UUID)
     * @return 추론 프롬프트 특정 버전 변수 정보
     */
    @GetMapping("variables/{versionUuid}")
    @Operation(
            summary = "추론 프롬프트 특정 버전 변수 조회",
            description = "UUID 기반으로 특정 추론 프롬프트 버전의 변수를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 버전 변수 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트 버전 변수가 존재하지 않음")
    public AxResponseEntity<InfPromptVarsByIdRes> getInfPromptVarsById(
            @Parameter(description = "추론 프롬프트 버전 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable("versionUuid") String versionUuid
    ) {
        log.info("Controller: 추론프롬프트 버전 변수 조회 API 호출 - id: {}", versionUuid);

        InfPromptVarsByIdRes infPromptVarsById = inferPromptService.getInfPromptVarsById(versionUuid);

        return AxResponseEntity.ok(infPromptVarsById, "추론프롬프트 버전의 변수를 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 특정 버전 태그 조회
     *
     * @param versionUuid 추론 프롬프트 버전 ID(UUID)
     * @return 추론 프롬프트 특정 버전 태그 정보
     */
    @GetMapping("tags/{versionUuid}")
    @Operation(
            summary = "추론 프롬프트 특정 버전 태그 조회",
            description = "UUID 기반으로 특정 추론 프롬프트 버전의 태그를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 버전 태그 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트 버전 태그가 존재하지 않음")
    public AxResponseEntity<InfPromptTagsListByIdRes> getInfPromptTagById(
            @Parameter(description = "추론 프롬프트 버전 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable("versionUuid") String versionUuid
    ) {
        log.info("Controller: 추론프롬프트 태그 조회 API 호출 - id: {}", versionUuid);

        InfPromptTagsListByIdRes infPromptTagById = inferPromptService.getInfPromptTagById(versionUuid);

        return AxResponseEntity.ok(infPromptTagById, "추론프롬프트 버전의 태그를 성공적으로 조회했습니다.");
    }

    /**
     * 추론프롬프트 삭제
     *
     * @param promptUuid 추론프롬프트 UUID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{promptUuid}")
    @Operation(
            summary = "추론프롬프트 삭제",
            description = "특정 추론프롬프트을 삭제합니다."
    )
    @ApiResponse(responseCode = "200", description = "추론프롬프트 삭제 성공")
    @ApiResponse(responseCode = "404", description = "추론프롬프트을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public AxResponseEntity<Void> deleteInfPromptById(
            @PathVariable("promptUuid")
            @Parameter(description = "추론프롬프트 UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c")
            String promptUuid
    ) {
        log.info("추론프롬프트 삭제 요청: promptUuid={}", promptUuid);

        inferPromptService.deleteInfPromptById(promptUuid);

        log.info("추론프롬프트 삭제 완료: promptUuid={}", promptUuid);

        return AxResponseEntity.ok(null, "추론프롬프트가 성공적으로 삭제되었습니다.");
    }

    /**
     * 새로운 추론프롬프트 생성
     *
     * @param request 추론프롬프트 생성 요청
     * @return 생성된 추론프롬프트 정보
     */
    @PostMapping
    @Operation(
            summary = "새로운 추론프롬프트 생성",
            description = "새로운 추론프롬프트를 생성합니다. 자동으로 ptype:1이 설정됩니다."
    )
    @ApiResponse(responseCode = "201", description = "추론프롬프트 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public AxResponseEntity<InfPromptCreateRes> createInfPrompt(
            @RequestBody InfPromptCreateReq request
    ) {
        log.info("새로운 추론프롬프트 생성 요청: name={}", request.getName());

        InfPromptCreateRes infPromptRes = inferPromptService.createInfPrompt(request);

        log.info("추론프롬프트 생성 완료: name={}, tags: {}", request.getName(), request.getTags());

        return AxResponseEntity.created(infPromptRes, "새로운 추론프롬프트가 성공적으로 생성되었습니다.");
    }

    /**
     * 추론프롬프트 정보 수정
     *
     * @param promptUuid 추론프롬프트 UUID
     * @param request    추론프롬프트 수정 요청
     * @return 수정된 추론프롬프트 정보
     */
    @PutMapping("/{promptUuid}")
    @Operation(
            summary = "추론프롬프트 정보 수정",
            description = "기존 추론프롬프트의 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "추론프롬프트 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "추론프롬프트을 찾을 수 없음")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public AxResponseEntity<Void> updateInfPromptById(
            @PathVariable("promptUuid")
            @Parameter(description = "추론프롬프트 UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c")
            String promptUuid,

            @RequestBody InfPromptUpdateReq request
    ) {

        log.info("추론프롬프트 수정 요청: promptUuid={}, newName={}", promptUuid, request.getNewName());

        inferPromptService.updateInfPromptById(promptUuid, request);

        return AxResponseEntity.ok(null, "추론프롬프트 정보가 성공적으로 수정되었습니다.");
    }

    /**
     * 추론프롬프트 내장 템플릿 목록 조회
     *
     * @return 추론프롬프트 내장 템플릿 목록
     */
    @GetMapping("/builtin")
    @Operation(
            summary = "추론프롬프트 내장 템플릿 목록 조회",
            description = "추론프롬프트 내장 템플릿 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "추론프롬프트 내장 템플릿 목록 조회 성공")
    public AxResponseEntity<InfPromptBuiltinRes> getInfPromptBuiltinTmplts() {
        InfPromptBuiltinRes infPromptBuiltinTmplts = inferPromptService.getInfPromptBuiltin();

        return AxResponseEntity.ok(infPromptBuiltinTmplts, "추론프롬프트 내장 템플릿 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트와 연결된 프롬프트 목록 조회
     *
     * @param promptUuid 추론 프롬프트 ID(UUID)
     * @param page       페이지 번호 (1부터 시작)
     * @param size       페이지 크기
     * @return 연결된 프롬프트 목록
     */
    @GetMapping("/{promptUuid}/lineage-relations")
    @Operation(
            summary = "추론 프롬프트 연결 에이전트 조회",
            description = "특정 추론 프롬프트와 연결된 에이전트 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "연결된 에이전트 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 추론 프롬프트가 존재하지 않음")
    public AxResponseEntity<PageResponse<InfPromptLineageRes>> getInfPromptLineageRelations(
            @Parameter(description = "추론 프롬프트 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable("promptUuid") String promptUuid,

            @Parameter(description = "페이지 번호 (1부터 시작)")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "6") Integer size
    ) {
        log.info("Controller: 추론프롬프트 연결 에이전트 조회 API 호출 - promptUuid: {}, page: {}, size: {}", promptUuid, page, size);

        PageResponse<InfPromptLineageRes> lineageRelations = inferPromptService.getInfPromptLineageRelations(promptUuid, page, size);

        return AxResponseEntity.okPage(lineageRelations, "추론프롬프트 연결 에이전트 성공적으로 조회했습니다.");
    }

    /**
     * 추론 프롬프트 Policy 설정
     *
     * @param promptUuid  추론 프롬프트 ID
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @return 설정된 Policy 목록
     */
    @PostMapping("/{promptUuid}/policy")
    @Operation(summary = "추론 프롬프트 Policy 설정", description = "추론 프롬프트의 Policy를 설정합니다.")
    @ApiResponse(responseCode = "200", description = "추론 프롬프트 Policy 설정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "500", description = "서버 오류")
    public AxResponseEntity<List<PolicyRequest>> setInferPromptPolicy(
            @PathVariable("promptUuid") @Parameter(description = "추론 프롬프트 ID(UUID)", example = "550e8400-e29b-41d4-a716-446655440000", required = true) String promptUuid,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("추론 프롬프트 Policy 설정 요청 - promptUuid: {}, memberId: {}, projectName: {}", promptUuid, memberId, projectName);
        List<PolicyRequest> policy = inferPromptService.setInferPromptPolicy(promptUuid, memberId, projectName);
        return AxResponseEntity.ok(policy, "추론 프롬프트 Policy가 성공적으로 설정되었습니다.");
    }

}