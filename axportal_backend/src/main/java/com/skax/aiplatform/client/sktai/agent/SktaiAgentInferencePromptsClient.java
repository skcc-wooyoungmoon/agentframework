package com.skax.aiplatform.client.sktai.agent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCommentCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCommentUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCopyRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptTestRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.TagSearchTestRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.TestPromptVariablesRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.BuiltinPromptsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.CommonResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptCommentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptCommentsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptExportResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptFilterByTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptIntegrationResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptMessagesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptTagListResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVariablesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptsResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Agent Inference Prompts API Client
 *
 * <p>SKTAI Agent 시스템의 Inference Prompt 관리 기능을 제공하는 Feign Client입니다.
 * Agent가 추론에 사용할 프롬프트를 생성, 관리, 버전 관리할 수 있습니다.</p>
 *
 * <h3>제공 기능:</h3>
 * <ul>
 *   <li><strong>Prompt CRUD</strong>: 프롬프트 생성, 조회, 수정, 삭제</li>
 *   <li><strong>버전 관리</strong>: 프롬프트 버전 관리 및 릴리즈</li>
 *   <li><strong>변수 관리</strong>: 프롬프트 변수 정의 및 검증</li>
 *   <li><strong>태그 관리</strong>: 프롬프트 분류 및 검색을 위한 태그</li>
 *   <li><strong>댓글 시스템</strong>: 프롬프트에 대한 댓글 및 피드백</li>
 *   <li><strong>복사 기능</strong>: 기존 프롬프트 복사하여 새 프롬프트 생성</li>
 *   <li><strong>빌트인 템플릿</strong>: 사전 정의된 프롬프트 템플릿</li>
 * </ul>
 *
 * <h3>프롬프트 메시지 타입:</h3>
 * <ul>
 *   <li><strong>0: text</strong> - 일반 텍스트 메시지</li>
 *   <li><strong>1: system</strong> - 시스템 메시지</li>
 *   <li><strong>2: user</strong> - 사용자 메시지</li>
 *   <li><strong>3: assistant</strong> - 어시스턴트 메시지</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * // 프롬프트 목록 조회
 * PromptsResponse prompts = promptsClient.getInferencePrompts("project-123", 1, 10, null, null, null);
 *
 * // 프롬프트 생성
 * PromptCreateRequest request = PromptCreateRequest.builder()
 *     .name("Chat Assistant Prompt")
 *     .messages(Arrays.asList(
 *         PromptMessage.builder().mtype(1).message("You are a helpful assistant.").build(),
 *         PromptMessage.builder().mtype(2).message("{{user_input}}").build()
 *     ))
 *     .variables(Arrays.asList(
 *         PromptVariable.builder().variable("{{user_input}}").validation("").build()
 *     ))
 *     .tags(Arrays.asList(
 *         PromptTag.builder().tag("chatbot").build()
 *     ))
 *     .build();
 * PromptCreateResponse response = promptsClient.createInferencePrompt(request);
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-08-15
 */
@FeignClient(
        name = "sktai-agent-inference-prompts-client",
        url = "${sktai.api.base-url}/api/v1/agent",
        configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Agent Inference Prompts", description = "SKTAI Agent Inference Prompts Management API")
public interface SktaiAgentInferencePromptsClient {

    /**
     * 프롬프트 목록 조회
     *
     * <p>등록된 Inference Prompt들의 목록을 조회합니다.
     * 프로젝트별로 필터링하고 페이징, 정렬, 검색 기능을 지원합니다.</p>
     *
     * @param projectId    프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)
     * @param page         페이지 번호 (기본값: 1)
     * @param size         페이지 크기 (기본값: 10)
     * @param sort         정렬 기준
     * @param filter       필터 조건
     * @param search       검색어
     * @param release_only 릴리즈 전용 여부 (기본값: false)
     * @return 프롬프트 목록 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts")
    @Operation(
            summary = "프롬프트 목록 조회",
            description = "등록된 Inference Prompt들의 목록을 프로젝트별로 조회합니다. 페이징, 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 목록 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptsResponse getInferencePrompts(
            @Parameter(description = "프로젝트 ID") @RequestParam(required = false) String projectId,
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
            @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
            @Parameter(description = "검색어") @RequestParam(required = false) String search,
            @Parameter(description = "릴리즈 전용 여부") @RequestParam(defaultValue = "false") Boolean release_only
    );

    /**
     * 프롬프트 생성
     *
     * <p>새로운 Inference Prompt를 생성합니다.
     * 메시지, 변수, 태그를 포함하여 프롬프트를 정의할 수 있습니다.</p>
     *
     * @param request 프롬프트 생성 요청 데이터
     * @return 프롬프트 생성 응답 (프롬프트 UUID 포함)
     * @since 1.0
     */
    @PostMapping("/inference-prompts")
    @Operation(
            summary = "프롬프트 생성",
            description = "새로운 Inference Prompt를 생성합니다. 메시지, 변수, 태그를 포함하여 정의할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프롬프트 생성 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptCreateResponse createInferencePrompt(@RequestBody PromptCreateRequest request);

    /**
     * 성능 측정용 프롬프트 목록 조회
     *
     * <p>성능 측정을 위한 전용 엔드포인트입니다. (테스트 전용)</p>
     *
     * @param projectId    프로젝트 ID
     * @param ignoreOption 무시 옵션 (1: authz)
     * @param page         페이지 번호
     * @param size         페이지 크기
     * @param sort         정렬 기준
     * @param filter       필터 조건
     * @param search       검색어
     * @return 프롬프트 목록 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/perf")
    @Operation(
            summary = "성능 측정용 프롬프트 목록 조회",
            description = "성능 측정을 위한 전용 엔드포인트입니다. (테스트 전용)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 목록 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptsResponse getInferencePromptsPerf(
            @Parameter(description = "프로젝트 ID") @RequestParam(defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId,
            @Parameter(description = "무시 옵션 (1: authz)") @RequestParam(defaultValue = "1") Integer ignoreOption,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
            @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
            @Parameter(description = "검색어") @RequestParam(required = false) String search
    );

    /**
     * 프롬프트 상세 조회
     *
     * <p>특정 프롬프트의 상세 정보를 조회합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @return 프롬프트 상세 정보 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/{promptUuid}")
    @Operation(
            summary = "프롬프트 상세 조회",
            description = "특정 프롬프트의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 상세 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptResponse getInferencePrompt(@Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid);

    /**
     * 프롬프트 수정
     *
     * <p>기존 프롬프트를 수정합니다. 버전업 처리됩니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @param request    프롬프트 수정 요청 데이터
     * @return 프롬프트 수정 응답
     * @since 1.0
     */
    @PutMapping("/inference-prompts/{promptUuid}")
    @Operation(
            summary = "프롬프트 수정",
            description = "기존 프롬프트를 수정합니다. 버전업 처리됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 수정 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptUpdateOrDeleteResponse updateInferencePrompt(
            @Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid,
            @RequestBody PromptUpdateRequest request
    );

    /**
     * 프롬프트 삭제
     *
     * <p>특정 프롬프트를 삭제합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @since 1.0
     */
    @DeleteMapping("/inference-prompts/{promptUuid}")
    @Operation(
            summary = "프롬프트 삭제",
            description = "특정 프롬프트를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "프롬프트 삭제 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    void deleteInferencePrompt(@Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid);

    /**
     * 프롬프트 최신 버전 조회
     *
     * <p>특정 프롬프트의 최신 버전을 조회합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @return 최신 프롬프트 버전 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/versions/{promptUuid}/latest")
    @Operation(
            summary = "프롬프트 최신 버전 조회",
            description = "특정 프롬프트의 최신 버전을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최신 버전 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptVersionResponse getLatestInferencePromptVersion(@Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid);

    /**
     * 프롬프트 버전 목록 조회
     *
     * <p>특정 프롬프트의 모든 버전 목록을 조회합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @return 프롬프트 버전 목록 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/versions/{promptUuid}")
    @Operation(
            summary = "프롬프트 버전 목록 조회",
            description = "특정 프롬프트의 모든 버전 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "버전 목록 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptVersionsResponse getInferencePromptVersions(@Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid);

    /**
     * 프롬프트 메시지 조회
     *
     * <p>특정 버전의 프롬프트 메시지를 조회합니다.</p>
     *
     * @param versionId 프롬프트 버전 ID
     * @return 프롬프트 메시지 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/messages/{versionId}")
    @Operation(
            summary = "프롬프트 메시지 조회",
            description = "특정 버전의 프롬프트 메시지를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptMessagesResponse getInferencePromptMessages(@Parameter(description = "프롬프트 버전 ID") @PathVariable String versionId);

    /**
     * 프롬프트 변수 조회
     *
     * <p>특정 버전의 프롬프트 변수를 조회합니다.</p>
     *
     * @param versionId 프롬프트 버전 ID
     * @return 프롬프트 변수 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/variables/{versionId}")
    @Operation(
            summary = "프롬프트 변수 조회",
            description = "특정 버전의 프롬프트 변수를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변수 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptVariablesResponse getInferencePromptVariables(@Parameter(description = "프롬프트 버전 ID") @PathVariable String versionId);

    /**
     * 프롬프트 태그 조회 (버전별)
     *
     * <p>특정 버전의 프롬프트 태그를 조회합니다.</p>
     *
     * @param versionId 프롬프트 버전 ID
     * @return 프롬프트 태그 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/tags/{versionId}")
    @Operation(
            summary = "프롬프트 태그 조회 (버전별)",
            description = "특정 버전의 프롬프트 태그를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptTagsResponse getInferencePromptTagsByVersion(@Parameter(description = "프롬프트 버전 ID") @PathVariable String versionId);

    /**
     * 프롬프트 태그 목록 조회
     *
     * <p>모든 프롬프트 태그 목록을 조회합니다.</p>
     *
     * @return 프롬프트 태그 목록 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/list/tags")
    @Operation(
            summary = "프롬프트 태그 목록 조회",
            description = "모든 프롬프트 태그 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공")
    })
    PromptTagListResponse getInferencePromptTagsList();

    /**
     * 태그로 프롬프트 검색
     *
     * <p>태그를 기준으로 프롬프트 ID를 검색합니다.</p>
     *
     * @param filters 필터 조건 (태그)
     * @return 태그로 필터링된 프롬프트 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/search/tags")
    @Operation(
            summary = "태그로 프롬프트 검색",
            description = "태그를 기준으로 프롬프트 ID를 검색합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "태그 검색 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptFilterByTagsResponse searchInferencePromptsByTags(@Parameter(description = "검색할 태그 필터") @RequestParam String filters);

    /**
     * 프롬프트 복사
     *
     * <p>기존 프롬프트를 복사하여 새로운 프롬프트를 생성합니다.</p>
     *
     * @param promptUuid 복사할 프롬프트 UUID
     * @param request    프롬프트 복사 요청 데이터
     * @return 프롬프트 생성 응답 (새 프롬프트 UUID 포함)
     * @since 1.0
     */
    @PostMapping("/inference-prompts/copy/{promptUuid}")
    @Operation(
            summary = "프롬프트 복사",
            description = "기존 프롬프트를 복사하여 새로운 프롬프트를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프롬프트 복사 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptCreateResponse copyInferencePrompt(
            @Parameter(description = "복사할 프롬프트 UUID") @PathVariable String promptUuid,
            @RequestBody PromptCopyRequest request
    );

    /**
     * 프롬프트 댓글 목록 조회
     *
     * <p>특정 버전의 프롬프트에 대한 댓글 목록을 조회합니다.</p>
     *
     * @param versionId 프롬프트 버전 ID
     * @param page      페이지 번호
     * @param size      페이지 크기 (기본값: 10)
     * @return 댓글 목록 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/comments/{versionId}")
    @Operation(
            summary = "프롬프트 댓글 목록 조회",
            description = "특정 버전의 프롬프트에 대한 댓글 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptCommentsResponse getInferencePromptComments(
            @Parameter(description = "프롬프트 버전 ID") @PathVariable String versionId,
            @Parameter(description = "페이지 번호") @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") Integer size
    );

    /**
     * 프롬프트 댓글 생성
     *
     * <p>특정 버전의 프롬프트에 댓글을 생성합니다.</p>
     *
     * @param versionId 프롬프트 버전 ID
     * @param request   댓글 생성 요청 데이터
     * @return 댓글 생성 응답
     * @since 1.0
     */
    @PostMapping("/inference-prompts/comments/{versionId}")
    @Operation(
            summary = "프롬프트 댓글 생성",
            description = "특정 버전의 프롬프트에 댓글을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptCommentResponse createInferencePromptComment(
            @Parameter(description = "프롬프트 버전 ID") @PathVariable String versionId,
            @RequestBody PromptCommentCreateRequest request
    );

    /**
     * 프롬프트 하드 삭제
     *
     * <p>삭제 마크된 모든 프롬프트들을 데이터베이스에서 완전히 삭제합니다.</p>
     *
     * @apiNote 이 작업은 되돌릴 수 없으므로 주의해서 사용해야 합니다.
     * @since 1.0
     */
    @PostMapping("/inference-prompts/hard-delete")
    @Operation(
            summary = "프롬프트 하드 삭제",
            description = "삭제 마크된 모든 프롬프트들을 데이터베이스에서 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    })
    void hardDeleteInferencePrompts();

    /**
     * 빌트인 프롬프트 템플릿 조회
     *
     * <p>사전 정의된 빌트인 프롬프트 템플릿 목록을 조회합니다.</p>
     *
     * @return 빌트인 프롬프트 목록 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/templates/builtin")
    @Operation(
            summary = "빌트인 프롬프트 템플릿 조회",
            description = "사전 정의된 빌트인 프롬프트 템플릿 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "빌트인 템플릿 조회 성공")
    })
    BuiltinPromptsResponse getBuiltinInferencePromptTemplates();

    /**
     * 프롬프트 변수 테스트
     *
     * <p>프롬프트 변수 검증을 테스트합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @param request    변수 테스트 요청 데이터
     * @return 변수 검증 결과 응답
     * @since 1.0
     */
    @PostMapping("/inference-prompts/test/variables/{promptUuid}")
    @Operation(
            summary = "프롬프트 변수 테스트",
            description = "프롬프트 변수 검증을 테스트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "변수 테스트 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    CommonResponse testInferencePromptVariables(
            @Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid,
            @RequestBody TestPromptVariablesRequest request
    );

    /**
     * 프롬프트 댓글 수정
     *
     * <p>기존 프롬프트 댓글을 수정합니다.</p>
     *
     * @param commentUuid 댓글 UUID
     * @param request     댓글 수정 요청 데이터
     * @return 수정된 댓글 정보
     * @since 1.0
     */
    @PutMapping("/inference-prompts/comments/{commentUuid}")
    @Operation(
            summary = "프롬프트 댓글 수정",
            description = "기존 프롬프트 댓글의 내용을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "댓글 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptCommentResponse updateInferencePromptComment(
            @Parameter(description = "댓글 UUID") @PathVariable String commentUuid,
            @RequestBody PromptCommentUpdateRequest request
    );

    /**
     * 프롬프트 댓글 삭제
     *
     * <p>기존 프롬프트 댓글을 삭제합니다.</p>
     *
     * @param commentUuid 댓글 UUID
     * @return 삭제 결과 응답
     * @since 1.0
     */
    @DeleteMapping("/inference-prompts/comments/{commentUuid}")
    @Operation(
            summary = "프롬프트 댓글 삭제",
            description = "기존 프롬프트 댓글을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "댓글 삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    CommonResponse deleteInferencePromptComment(
            @Parameter(description = "댓글 UUID") @PathVariable String commentUuid
    );

    /**
     * 프롬프트 버전 삭제
     *
     * <p>특정 프롬프트 버전을 삭제합니다.</p>
     *
     * @param versionId 프롬프트 버전 ID
     * @return 삭제 결과 응답
     * @since 1.0
     */
    @DeleteMapping("/inference-prompts/versions/{versionId}")
    @Operation(
            summary = "프롬프트 버전 삭제",
            description = "특정 프롬프트 버전을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "버전 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "버전 삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "버전을 찾을 수 없음")
    })
    CommonResponse deleteInferencePromptVersion(
            @Parameter(description = "프롬프트 버전 ID") @PathVariable String versionId
    );

    /**
     * 프롬프트 통합 Internal API
     *
     * <p>프롬프트 통합을 위한 내부 API입니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @return 프롬프트 통합 정보
     * @since 1.0
     */
    @GetMapping("/inference-prompts/api/{promptUuid}")
    @Operation(
            summary = "프롬프트 통합 Internal API",
            description = "프롬프트 통합을 위한 내부 API로 메타데이터를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "통합 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음")
    })
    PromptIntegrationResponse getInferencePromptIntegrationApi(
            @Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid
    );

    /**
     * 프롬프트 변수 검증 Internal API
     *
     * <p>프롬프트 변수 검증을 위한 내부 API입니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @return 변수 검증 정보
     * @since 1.0
     */
    @GetMapping("/inference-prompts/api/variables/{promptUuid}")
    @Operation(
            summary = "프롬프트 변수 검증 Internal API",
            description = "프롬프트 변수 검증을 위한 내부 API입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변수 검증 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음")
    })
    PromptVariablesResponse getInferencePromptVariablesApi(
            @Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid
    );

    /**
     * 프롬프트 통합 테스트
     *
     * <p>프롬프트 통합의 테스트 및 예시를 제공합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @param request    프롬프트 테스트 요청 데이터
     * @return 테스트 결과 응답
     * @since 1.0
     */
    @PostMapping("/inference-prompts/test/{promptUuid}")
    @Operation(
            summary = "프롬프트 통합 테스트",
            description = "프롬프트 통합의 테스트 및 예시를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "프롬프트 테스트 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    CommonResponse testInferencePromptIntegration(
            @Parameter(description = "프롬프트 UUID") @PathVariable String promptUuid,
            @RequestBody PromptTestRequest request
    );

    /**
     * 태그 기반 프롬프트 검색 테스트
     *
     * <p>태그를 이용한 프롬프트 ID 검색의 테스트 및 예시를 제공합니다.</p>
     *
     * @param request 태그 검색 테스트 요청 데이터
     * @return 검색 결과 응답
     * @since 1.0
     */
    @PostMapping("/inference-prompts/test-search-tags/from-tags")
    @Operation(
            summary = "태그 기반 프롬프트 검색 테스트",
            description = "태그를 이용한 프롬프트 ID 검색의 테스트 및 예시를 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "태그 검색 테스트 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptFilterByTagsResponse testInferencePromptSearchByTags(
            @RequestBody TagSearchTestRequest request
    );

    /**
     * Inference Prompt Export 조회
     *
     * <p>Import를 위한 통합 데이터를 조회합니다.
     * messages, variables, tags를 모두 포함합니다.</p>
     *
     * @param promptUuid 프롬프트 UUID
     * @return Export 데이터 응답
     * @since 1.0
     */
    @GetMapping("/inference-prompts/prompt/{prompt_uuid}")
    @Operation(
            summary = "Inference Prompt Export 조회",
            description = "Import를 위한 통합 데이터를 조회합니다. messages, variables, tags를 모두 포함합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Export 조회 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptExportResponse getPromptExport(
            @Parameter(description = "프롬프트 UUID") @PathVariable("prompt_uuid") String promptUuid
    );

    /**
     * Inference Prompt Import (JSON)
     *
     * <p>JSON 데이터를 받아서 Inference Prompt를 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     *
     * @param promptUuid Prompt UUID (query parameter)
     * @param jsonData   JSON 형식의 Prompt 데이터
     * @return 생성된 Prompt 정보
     */
    @PostMapping("/inference-prompts/import")
    @Operation(
            summary = "Inference Prompt Import (JSON)",
            description = "JSON 데이터를 받아서 Inference Prompt를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prompt Import 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    PromptCreateResponse importPrompt(
            @Parameter(description = "Prompt UUID", required = true) @RequestParam("prompt_uuid") String promptUuid,
            @Parameter(description = "JSON 형식의 Prompt 데이터", required = true)
            @RequestBody Object jsonData);
}
