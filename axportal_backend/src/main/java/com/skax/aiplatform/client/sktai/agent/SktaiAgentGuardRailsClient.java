package com.skax.aiplatform.client.sktai.agent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailCreateReq;
import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailUpdateReq;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailCreateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailDetailRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailsRes;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKT AI 에이전트 - 가드레일 API Client
 */
@FeignClient(
    name = "sktai-agent-guardrails-client",
    url = "${sktai.api.base-url}/api/v1/agent",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKT AI Agent GuardRails", description = "SKTAI Agent Guard Rail Management API")
public interface SktaiAgentGuardRailsClient {

    /**
     * 가드레일 목록 조회
     *
     * <p>등록된 가드레일들의 목록을 조회합니다.
     * 프로젝트별로 필터링하고 페이징, 정렬, 검색 기능을 지원합니다.</p>
     *
     * @param projectId 프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)
     * @param page      페이지 번호 (기본값: 1)
     * @param size      페이지 크기 (기본값: 10)
     * @param sort      정렬 기준
     * @param filter    필터 조건
     * @param search    검색어
     * @return 가드레일 목록 응답
     */
    @GetMapping("/guardrails")
    @Operation(
        summary = "Guard Rail 목록 조회",
        description = "등록된 Guard Rail들의 목록을 프로젝트별로 조회합니다. 페이징, 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponse(responseCode = "200", description = "Guard Rail 목록 조회 성공")
    @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    SktGuardRailsRes getGuardRails(
        @Parameter(description = "프로젝트 ID") @RequestParam(defaultValue = "d89a7451-3d40-4bab-b4ee-6aecd55b4f32") String projectId,
        @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size,
        @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
        @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
        @Parameter(description = "검색어") @RequestParam(required = false) String search
    );

    /**
     * 가드레일 생성
     *
     * @param request 가드레일 생성 요청 데이터
     * @return 가드레일 생성 응답 (가드레일 UUID 포함)
     * @since 1.0
     */
    @PostMapping("/guardrails")
    @Operation(
        summary = "가드레일 생성",
        description = "새로운 가드레일을 생성합니다. 프롬프트, LLM 설정, 태그 등을 포함하여 정의할 수 있습니다."
    )
    @ApiResponse(responseCode = "201", description = "가드레일 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    SktGuardRailCreateRes createGuardRail(@RequestBody SktGuardRailCreateReq request);

    /**
     * 가드레일 상세 조회
     *
     * <p>특정 가드레일의 상세 정보를 조회합니다.
     * 가드레일의 기본 정보, LLM 설정, 태그 등 모든 정보를 포함합니다.</p>
     *
     * @param guardrailsId 가드레일 ID
     * @return 가드레일 상세 정보 응답
     * @since 1.0
     */
    @GetMapping("/guardrails/{guardrails_id}")
    @Operation(
        summary = "가드레일 상세 조회",
        description = "특정 가드레일의 상세 정보를 조회합니다. 가드레일의 기본 정보, LLM 설정, 태그 등 모든 정보를 포함합니다."
    )
    @ApiResponse(responseCode = "200", description = "가드레일 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "가드레일을 찾을 수 없음")
    @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    SktGuardRailDetailRes getGuardRail(@Parameter(description = "가드레일 ID") @PathVariable("guardrails_id") String guardrailsId);

    /**
     * 가드레일 수정
     *
     * <p>기존 가드레일의 정보를 수정합니다.
     * 이름, 설명, LLM 설정, 태그 등 모든 정보를 업데이트할 수 있습니다.</p>
     *
     * @param guardrailsId 가드레일 ID
     * @param request      가드레일 수정 요청 데이터
     * @return 가드레일 수정 응답
     * @since 1.0
     */
    @PutMapping("/guardrails/{guardrails_id}")
    @Operation(
        summary = "가드레일 수정",
        description = "기존 가드레일의 정보를 수정합니다. 이름, 설명, LLM 설정, 태그 등 모든 정보를 업데이트할 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "가드레일 수정 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "404", description = "가드레일을 찾을 수 없음")
    @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    SktGuardRailUpdateRes updateGuardRail(
        @Parameter(description = "가드레일 ID") @PathVariable("guardrails_id") String guardrailsId,
        @RequestBody SktGuardRailUpdateReq request
    );

    /**
     * 가드레일 삭제
     *
     * <p>특정 가드레일을 삭제합니다.
     * 삭제된 가드레일은 복구할 수 없으므로 신중하게 사용해야 합니다.</p>
     *
     * @param guardrailsId 가드레일 ID
     * @since 1.0
     */
    @DeleteMapping("/guardrails/{guardrails_id}")
    @Operation(
        summary = "가드레일 삭제",
        description = "특정 가드레일을 삭제합니다. 삭제된 가드레일은 복구할 수 없으므로 신중하게 사용해야 합니다."
    )
    @ApiResponse(responseCode = "204", description = "가드레일 삭제 성공")
    @ApiResponse(responseCode = "404", description = "가드레일을 찾을 수 없음")
    @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    void deleteGuardRail(@Parameter(description = "가드레일 ID") @PathVariable("guardrails_id") String guardrailsId);
    
    /**
     * Guard Rail Import (JSON)
     * 
     * <p>JSON 데이터를 받아서 Guard Rail을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param guardrailsId Guard Rail ID (query parameter)
     * @param jsonData JSON 형식의 Guard Rail 데이터
     * @return 생성된 Guard Rail 정보
     */
    @PostMapping("/guardrails/import")
    @Operation(
        summary = "Guard Rail Import (JSON)",
        description = "JSON 데이터를 받아서 Guard Rail을 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "Guard Rail Import 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    SktGuardRailCreateRes importGuardRail(
        @Parameter(description = "Guard Rail ID", required = true) @RequestParam("guardrails_id") String guardrailsId,
        @Parameter(description = "JSON 형식의 Guard Rail 데이터", required = true)
        @RequestBody Object jsonData);


    /**
     * Guard Rail 하드 삭제
     * 
     * <p>삭제 마크된 모든 Guard Rail들을 데이터베이스에서 완전히 삭제합니다.</p>
     * 
     * @apiNote 이 작업은 되돌릴 수 없으므로 주의해서 사용해야 합니다.
     */
    @PostMapping("/guardrails/hard-delete")
    @Operation(
        summary = "Guard Rail 하드 삭제",
        description = "삭제 마크된 모든 Guard Rail들을 데이터베이스에서 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다."
    )
    @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    void hardDeleteGuardRails();
}
