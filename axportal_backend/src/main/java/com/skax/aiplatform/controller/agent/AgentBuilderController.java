package com.skax.aiplatform.controller.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.agent.response.AgentAppInfoRes;
import com.skax.aiplatform.dto.agent.response.AgentBuilderRes;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.service.agent.AgentBuilderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 에이전트 관련 컨트롤러
 * 
 * <p>
 * 에이전트 빌더, 배포 및 관리 관련 API를 제공합니다.
 * </p>
 * 
 * @author System
 * @since 2025-09-10
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
@Tag(name = "Agent Builder", description = "Agent Builder 관리 API")
public class AgentBuilderController {

    private final AgentBuilderService agentBuilderService;
    private final GpoUsersMasRepository gpoUsersMasRepository;

    @Value("${sktai.api.phoenix-base-url}")
    private String phoenixBaseUrl;

    @org.springframework.beans.factory.annotation.Value("${phoenix.internal.api-key:eyJhbGciOiJlUzI1NilsInR5cCl6lkpXVCJ9.eyJqdGkiOiJBcGlLZXk6MiJ9.UbPbHp6QEghnONVIC5GhZYRsGJzw2L-kywmAaY1HVOE}")
    private String phoenixInternalApiKey;

    @org.springframework.beans.factory.annotation.Value("${phoenix.external.api-key:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJBcGlLZXk6NCJ9.5b-_s9oa-urSTZSU4wUXX_x_gE46FGOBjtl0qcw8tuw}")
    private String phoenixExternalApiKey;

    /**
     * Phoenix 인증 활성화 여부
     * 환경 변수: PHOENIX_ENABLE_AUTH
     * 기본값: false (인증 비활성화 - 로그인 없이 접근 가능)
     * true로 설정 시 Phoenix 로그인 필요
     * 
     * 사용 방법:
     * - Phoenix deployment.yaml에서 PHOENIX_ENABLE_AUTH=false 설정 (기본값)
     * - 또는 환경 변수 설정: export PHOENIX_ENABLE_AUTH=true (인증 활성화)
     * - 또는 JVM 옵션: -DPHOENIX_ENABLE_AUTH=true
     */
    @org.springframework.beans.factory.annotation.Value("${PHOENIX_ENABLE_AUTH:false}")
    private boolean phoenixEnableAuth;

    // ==================== Agent Builder 관련 메서드 ====================

    /**
     * 에이전트 빌더 목록 조회
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return 에이전트 빌더 목록
     */
    @GetMapping("/builder")
    @Operation(summary = "에이전트 빌더 목록 조회", description = "SKT AI Platform의 에이전트 그래프 목록을 조회합니다.")
    public AxResponseEntity<PageResponse<AgentBuilderRes>> getAgentBuilders(
            @RequestParam(value = "project_id", required = false) @Parameter(description = "프로젝트 ID (UUID)", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId,

            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "12") @Parameter(description = "페이지 크기", example = "12") Integer size,
            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건", example = "created_at,desc") String sort,
            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,
            @RequestParam(value = "search", required = false) @Parameter(description = "검색 키워드") String search) {

        log.debug("에이전트 빌더 목록 조회 요청: projectId={}, page={}, size={}, sort={}, filter={}, search={}",
                projectId, page, size, sort, filter, search);

        PageResponse<AgentBuilderRes> result = agentBuilderService.getAgentBuilders(projectId, page, size, sort, filter,
                search);

        log.debug("에이전트 빌더 목록 조회 완료: 총 {}개", result.getTotalElements());

        return AxResponseEntity.success(result);
    }

    /**
     * 에이전트 빌더 상세 조회
     *
     * @param agentId 에이전트 ID
     * @return 에이전트 빌더 상세 정보
     */
    @GetMapping("/builder/{agentId}")
    @Operation(summary = "에이전트 빌더 상세 조회", description = "특정 에이전트의 상세 정보를 조회합니다.")
    public AxResponseEntity<AgentBuilderRes> getAgentBuilder(
            @Parameter(description = "에이전트 ID") @PathVariable String agentId) {

        log.debug("에이전트 빌더 상세 조회 요청: agentId={}", agentId);

        AgentBuilderRes result = agentBuilderService.getAgentBuilder(agentId);

        log.debug("에이전트 빌더 상세 조회 완료: {}", result.getName());

        return AxResponseEntity.success(result);
    }

    /**
     * 에이전트 이름/설명 수정
     *
     * @param agentId   에이전트 ID
     * @param updateReq 수정 요청 데이터 (name, description)
     * @return 수정된 에이전트 빌더 정보
     */
    @PutMapping("/builder/graphs/{agentId}/info")
    @Operation(summary = "에이전트 이름/설명 수정", description = "특정 에이전트의 이름과 설명을 수정합니다.")
    public AxResponseEntity<AgentBuilderRes> updateAgentInfo(
            @Parameter(description = "에이전트 ID") @PathVariable String agentId,
            @Parameter(description = "수정 요청 데이터") @RequestBody Map<String, Object> updateReq) {

        log.info("에이전트 이름/설명 수정 요청: agentId={}", agentId);
        log.info("요청 데이터 상세: name={}, description={}", updateReq.get("name"), updateReq.get("description"));

        // 빈 값 검증
        String name = (String) updateReq.get("name");
        if (name == null || name.trim().isEmpty()) {
            log.error("에이전트 이름이 비어있음: name={}", name);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "에이전트 이름은 필수입니다.");
        }

        AgentBuilderRes result = agentBuilderService.updateAgentInfo(agentId, updateReq);

        log.info("에이전트 이름/설명 수정 완료: name={}, description={}", result.getName(), result.getDescription());

        return AxResponseEntity.success(result);
    }

    /**
     * 에이전트 그래프 전체 저장
     *
     * @param agentId 에이전트 ID
     * @param saveReq 그래프 저장 요청 데이터
     * @return 저장 결과
     */
    @PutMapping("/builder/graphs/{agentId}")
    @Operation(summary = "에이전트 그래프 전체 저장", description = "특정 에이전트의 전체 그래프 구조(노드, 엣지, 메타데이터)를 저장합니다.")
    public AxResponseEntity<AgentBuilderRes> saveAgentGraph(
            @Parameter(description = "에이전트 ID") @PathVariable String agentId,
            @Parameter(description = "그래프 저장 요청 데이터") @RequestBody Map<String, Object> saveReq) {

        log.info("에이전트 그래프 저장 요청: agentId={}, saveReq={}", agentId, saveReq);

        AgentBuilderRes result = agentBuilderService.saveAgentGraph(agentId, saveReq);

        log.info("에이전트 그래프 저장 완료: agentId={}", agentId);

        return AxResponseEntity.success(result);
    }

    /**
     * 에이전트 빌더 삭제
     *
     * @param agentId 에이전트 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/graphs/{agentId}")
    @Operation(summary = "에이전트 빌더 삭제", description = "특정 에이전트를 삭제합니다.")
    public AxResponseEntity<Void> deleteAgentBuilder(
            @Parameter(description = "에이전트 ID") @PathVariable String agentId) {

        log.info("에이전트 빌더 삭제 요청: agentId={}", agentId);

        // checkPortalAdminPermission();

        agentBuilderService.deleteAgentBuilder(agentId);

        log.info("에이전트 빌더 삭제 완료: agentId={}", agentId);

        return AxResponseEntity.success();
    }

    /**
     * 에이전트 그래프 실행 스트리밍 (실시간 SSE 스트리밍)
     */
    @PostMapping(value = "/builder/graphs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "에이전트 채팅")
    public ResponseEntity<org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody> executeAgentGraphStream(
            @RequestBody Map<String, Object> request) {

        org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody responseBody = outputStream -> {
            try {
                agentBuilderService.streamAgentGraph(request, outputStream);

                // 스트리밍 완료 후 명시적으로 종료 신호 전송
                writeSseDone(outputStream);
            } catch (BusinessException e) {
                log.error("에이전트 그래프 실행 스트리밍 비즈니스 오류: {}", e.getMessage(), e);
                writeSseError(outputStream, e.getMessage() != null ? e.getMessage() : "Business error",
                        "BusinessException");
                // 🔥 스트림 종료 신호 전송 (chunked encoding 완료를 위해 필수)
                writeSseDone(outputStream);
            } catch (IllegalArgumentException e) {
                log.error("에이전트 그래프 실행 스트리밍 파라미터 오류: {}", e.getMessage(), e);
                writeSseError(outputStream, e.getMessage() != null ? e.getMessage() : "Invalid parameter",
                        e.getClass().getSimpleName());
                // 🔥 스트림 종료 신호 전송
                writeSseDone(outputStream);
            } catch (NullPointerException e) {
                log.error("에이전트 그래프 실행 스트리밍 Null 포인터 오류: {}", e.getMessage(), e);
                writeSseError(outputStream, "필수 데이터가 누락되었습니다. 요청 데이터를 확인해주세요.", e.getClass().getSimpleName());
                // 🔥 스트림 종료 신호 전송
                writeSseDone(outputStream);
            } catch (org.springframework.web.client.RestClientException e) {
                log.error("에이전트 그래프 실행 스트리밍 외부 API 호출 오류: {}", e.getMessage(), e);
                String errorMessage = String.format("외부 API 호출 실패: %s",
                        e.getMessage() != null ? e.getMessage() : "Unknown API error");
                writeSseError(outputStream, errorMessage, e.getClass().getSimpleName());
                // 🔥 스트림 종료 신호 전송
                writeSseDone(outputStream);
            } catch (org.springframework.security.authorization.AuthorizationDeniedException e) {
                // 스트리밍 완료 후 발생하는 Spring Security 예외는 무시
                // 응답이 이미 커밋된 상태에서 발생하는 예외이므로 로그만 남기고 무시
                log.debug("스트리밍 완료 후 발생한 Spring Security 예외 (무시): {}", e.getMessage());
            } catch (Exception e) {
                log.error("에이전트 그래프 실행 스트리밍 오류: {} (예외 타입: {})", e.getMessage(), e.getClass().getName(), e);
                String errorMessage = String.format("에이전트 그래프 실행 중 오류가 발생했습니다: %s (오류 유형: %s)",
                        e.getMessage() != null ? e.getMessage() : "Unknown error", e.getClass().getSimpleName());
                writeSseError(outputStream, errorMessage, e.getClass().getSimpleName());
                // 🔥 스트림 종료 신호 전송
                writeSseDone(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("X-Accel-Buffering", "no")
                .body(responseBody);
    }

    /**
     * SSE 형식의 에러를 OutputStream에 직접 작성
     */
    private void writeSseError(java.io.OutputStream outputStream, String errorMessage, String errorType) {
        try {
            String sanitizedMessage = errorMessage != null ? errorMessage.replace("\"", "'") : "Unknown error";
            String errorJson = String.format("data: {\"error\": \"%s\", \"type\": \"%s\"}\n\n", sanitizedMessage,
                    errorType);
            outputStream.write(errorJson.getBytes("UTF-8"));
            outputStream.flush();
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            // 비동기 요청이 완료된 후 응답에 쓰려고 시도한 경우 무시
            log.debug("비동기 요청 완료 후 SSE 에러 응답 작성 불가 (무시): {}", e.getMessage());
        } catch (java.io.IOException e) {
            log.error("SSE 에러 응답 작성 실패", e);
        }
    }

    /**
     * SSE 형식의 완료 신호를 OutputStream에 직접 작성 (chunked encoding 완료를 위해 필수)
     */
    private void writeSseDone(java.io.OutputStream outputStream) {
        try {
            outputStream.write("data: [DONE]\n\n".getBytes("UTF-8"));
            outputStream.flush();
        } catch (org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
            // 비동기 요청이 완료된 후 응답에 쓰려고 시도한 경우 무시
            log.debug("비동기 요청 완료 후 SSE 완료 신호 작성 불가 (무시): {}", e.getMessage());
        } catch (java.io.IOException e) {
            log.error("SSE 완료 신호 작성 실패", e);
        }
    }
    /**
     * Phoenix 빈 응답 생성
     */
    private AxResponseEntity<Map<String, Object>> buildPhoenixEmptyResponse(String graphName) {
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("data", null);
        emptyResult.put("enableAuth", phoenixEnableAuth);
        String phoenixProjectUrl = phoenixBaseUrl.replace("/projects", "") + "/projects/" + graphName;
        emptyResult.put("phoenixUrl", phoenixProjectUrl);
        return AxResponseEntity.success(emptyResult);
    }

    /**
     * 에이전트 빌더 템플릿 목록 조회
     *
     * @return 에이전트 빌더 템플릿 목록
     */
    @GetMapping("/builder/templates")
    @Operation(summary = "에이전트 빌더 템플릿 목록 조회", description = "SKT AI Platform의 에이전트 빌더 템플릿 목록을 조회합니다.")
    public AxResponseEntity<?> getAgentBuilderTemplates() {
        log.info("에이전트 빌더 템플릿 목록 조회 요청");

        Object result = agentBuilderService.getAgentBuilderTemplates();

        log.info("에이전트 빌더 템플릿 목록 조회 완료");

        return AxResponseEntity.success(result);
    }

    /**
     * 특정 템플릿 상세 조회
     */
    @GetMapping("/builder/templates/{templateId}")
    @Operation(summary = "특정 템플릿 상세 조회", description = "SKT AI Platform의 특정 템플릿 상세 정보를 조회합니다.")
    public AxResponseEntity<?> getAgentBuilderTemplate(@PathVariable String templateId) {
        log.info("특정 템플릿 상세 조회 요청: templateId={}", templateId);

        Object result = agentBuilderService.getAgentBuilderTemplate(templateId);

        return AxResponseEntity.success(result);
    }

    /**
     * 에이전트 빌더 생성 (템플릿 기반)
     */
    @PostMapping("/builder/create-from-template")
    @Operation(summary = "에이전트 빌더 생성", description = "템플릿을 기반으로 새로운 에이전트 빌더를 생성합니다.")
    public AxResponseEntity<Object> createAgentFromTemplate(@RequestBody Map<String, Object> requestBody) {

        log.info("에이전트 빌더 생성 요청: {}", requestBody);

        Object result = agentBuilderService.createAgentFromTemplate(requestBody);
        return AxResponseEntity.ok(result, "에이전트가 성공적으로 생성되었습니다.");
    }

    /**
     * 에이전트 Lineage 조회
     *
     * @param graphUuid 그래프 UUID
     * @return Lineage 목록
     */
    @GetMapping("/builder/graphs/{graphUuid}/lineages")
    @Operation(summary = "에이전트 Lineage 조회", description = "특정 에이전트 그래프의 Lineage 관계를 조회합니다.")
    public AxResponseEntity<List<com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes>> getAgentLineages(
            @Parameter(description = "그래프 UUID") @PathVariable String graphUuid) {

        log.info("에이전트 Lineage 조회 요청: graphUuid={}", graphUuid);

        try {
            List<com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes> lineages = agentBuilderService
                    .getAgentLineages(graphUuid);
            return AxResponseEntity.success(lineages);
        } catch (BusinessException e) {
            log.error("에이전트 Lineage 조회 실패 (비즈니스 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.success(new ArrayList<>());
        } catch (IllegalArgumentException e) {
            log.error("에이전트 Lineage 조회 실패 (파라미터 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.success(new ArrayList<>());
        } catch (NullPointerException e) {
            log.error("에이전트 Lineage 조회 실패 (Null 포인터 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.success(new ArrayList<>());
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("에이전트 Lineage 조회 실패 (외부 API 호출 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.success(new ArrayList<>());
        } catch (RuntimeException e) {
            log.error("에이전트 Lineage 조회 실패 (런타임 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.success(new ArrayList<>());
        }
    }

    /**
     * 에이전트 배포 정보 조회 (AgentBuilder 버전)
     *
     * @param graphUuid 그래프 UUID
     * @return 배포 정보
     */
    @GetMapping("/builder/graphs/{graphUuid}/app")
    @Operation(summary = "에이전트 배포 정보 조회", description = "특정 에이전트의 배포 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "에이전트를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentAppInfoRes> getAgentDeployInfo(
            @Parameter(description = "그래프 UUID") @PathVariable String graphUuid) {

        log.info("에이전트 배포 정보 조회 요청: graphUuid={}", graphUuid);

        try {
            AgentAppInfoRes response = agentBuilderService.getAgentDeployInfo(graphUuid);
            return AxResponseEntity.ok(response, "에이전트 배포 정보 조회 완료");
        } catch (BusinessException e) {
            log.error("에이전트 배포 정보 조회 실패 (비즈니스 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.ok(null, "에이전트 배포 정보 조회 실패");
        } catch (IllegalArgumentException e) {
            log.error("에이전트 배포 정보 조회 실패 (파라미터 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.ok(null, "에이전트 배포 정보 조회 실패");
        } catch (NullPointerException e) {
            log.error("에이전트 배포 정보 조회 실패 (Null 포인터 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.ok(null, "에이전트 배포 정보 조회 실패");
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("에이전트 배포 정보 조회 실패 (외부 API 호출 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.ok(null, "에이전트 배포 정보 조회 실패");
        } catch (RuntimeException e) {
            log.error("에이전트 배포 정보 조회 실패 (런타임 오류): graphUuid={}, error={}", graphUuid, e.getMessage(), e);
            return AxResponseEntity.ok(null, "에이전트 배포 정보 조회 실패");
        }
    }

    /**
     * Phoenix 프로젝트 ID 조회
     *
     * @param type 리소스 타입 (graph/app)
     * @param id   그래프 또는 앱 ID
     * @return Phoenix 프로젝트 ID
     */
    @GetMapping("/builder/phoenix/project")
    @Operation(summary = "Phoenix 프로젝트 ID 조회", description = "그래프 또는 앱과 연동된 Phoenix Trace 프로젝트 ID를 조회합니다.")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.OPTIONS })
    public AxResponseEntity<Map<String, Object>> getPhoenixProjectIdentifier(
            @Parameter(description = "리소스 타입 (graph/app)", example = "graph") @RequestParam String type,
            @Parameter(description = "그래프 또는 앱 ID", example = "graph-uuid") @RequestParam String id) {

        log.info("Phoenix 프로젝트 ID 조회 요청: type={}, id={}", type, id);
        String projectId = agentBuilderService.getPhoenixProjectIdentifier(type, id);

        Map<String, Object> result = new HashMap<>();
        result.put("projectId", projectId);
        // 참고: Phoenix는 쿠키 기반 인증을 사용하므로 URL 파라미터로 API Key를 전달하지 않습니다.
        // PHOENIX_ENABLE_AUTH=false인 경우 로그인 없이 접근 가능합니다.
        // PHOENIX_ENABLE_AUTH=true인 경우 Phoenix 로그인 API를 호출하여 쿠키에 토큰을 저장해야 합니다.

        return AxResponseEntity.success(result);
    }

    /**
     * Agent Graph Export (Python 코드 조회)
     *
     * @param graphId        그래프 ID
     * @param credentialType 인증 타입 (token/password)
     * @return Python 코드
     */
    @GetMapping("/builder/graphs/{graphId}/export/code")
    @Operation(summary = "Agent Graph Export (Python 코드)", description = "Agent Graph를 Python 코드로 Export합니다.")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.OPTIONS })
    public AxResponseEntity<Map<String, Object>> exportAgentGraphCode(
            @Parameter(description = "그래프 ID", example = "graph-uuid") @PathVariable String graphId,
            @Parameter(description = "인증 타입 (token/password)", example = "token") @RequestParam(value = "credential_type", required = false, defaultValue = "token") String credentialType) {

        log.info("Agent Graph Export 요청: graphId={}, credentialType={}", graphId, credentialType);
        String code = agentBuilderService.exportAgentGraphCode(graphId, credentialType);

        Map<String, Object> result = new HashMap<>();
        result.put("data", code != null ? code : "");

        return AxResponseEntity.success(result);
    }

    /**
     * Phoenix 프로젝트 ID 조회 (프록시)
     * 
     * @param graphName Phoenix 그래프 이름 (graph-{projectId}_{graphId} 형식)
     * @return Phoenix 프로젝트 정보
     */
    @GetMapping("/builder/phoenix/project/{graphName}")
    @Operation(summary = "Phoenix 프로젝트 ID 조회", description = "Phoenix API를 통해 프로젝트 ID를 조회합니다 (CORS 우회용 프록시)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "Phoenix API 호출 실패")
    })
    public AxResponseEntity<Map<String, Object>> getPhoenixProjectId(
            @PathVariable("graphName") String graphName) {

        log.info("Phoenix 프로젝트 ID 조회 요청: graphName={}", graphName);

        try {
            // Phoenix API Key 설정 (환경에 따라 다름)
            String phoenixApiKey;

            // 내부망인지 확인 (환경변수 또는 프로파일로 판단)
            boolean isInternal = System.getProperty("spring.profiles.active", "").contains("internal") ||
                    System.getenv("PHOENIX_ENV") != null && System.getenv("PHOENIX_ENV").equals("internal");

            if (isInternal) {
                // 내부망 설정
                phoenixApiKey = phoenixInternalApiKey;
                log.info("내부망 Phoenix 설정 사용");
            } else {
                // 외부망 설정
                phoenixApiKey = phoenixExternalApiKey;
                log.info("외부망 Phoenix 설정 사용");
            }

            // 설정 파일에서 주입받은 phoenixBaseUrl 사용
            String phoenixUrl = String.format("%s/%s", phoenixBaseUrl, graphName);

            log.info("Phoenix API 호출: {} (인증 활성화: {})", phoenixUrl, phoenixEnableAuth);

            // RestTemplate을 사용하여 Phoenix API 호출
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

            // Phoenix API Key 인증 설정
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();

            headers.set("Authorization", "Bearer " + phoenixApiKey);
            headers.set("Accept", "application/json");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<Map<String, Object>> response = restTemplate.exchange(phoenixUrl,
                    org.springframework.http.HttpMethod.GET, entity,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });

            Map<String, Object> result = response.getBody();

            // Phoenix 인증 활성화 여부를 응답에 포함
            if (result != null) {
                result.put("enableAuth", phoenixEnableAuth);
                // Phoenix URL도 함께 반환 (프론트엔드에서 사용)
                String phoenixProjectUrl = phoenixBaseUrl.replace("/projects", "") + "/projects/" + graphName;
                result.put("phoenixUrl", phoenixProjectUrl);
            }

            log.info("Phoenix API 응답: {} (인증 활성화: {})", result, phoenixEnableAuth);

            return AxResponseEntity.success(result);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Phoenix 프로젝트가 없는 경우 에러 로그 출력하지 않음 (정상적인 상황)
            log.debug("Phoenix 프로젝트 없음 (HTTP 오류): graphName={}, status={}", graphName, e.getStatusCode());
            return buildPhoenixEmptyResponse(graphName);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Phoenix API 호출 실패 (외부 API 오류): graphName={}, error={}", graphName, e.getMessage(), e);
            return buildPhoenixEmptyResponse(graphName);
        } catch (IllegalArgumentException e) {
            log.error("Phoenix 프로젝트 ID 조회 실패 (파라미터 오류): graphName={}, error={}", graphName, e.getMessage(), e);
            return buildPhoenixEmptyResponse(graphName);
        } catch (NullPointerException e) {
            log.error("Phoenix 프로젝트 ID 조회 실패 (Null 포인터 오류): graphName={}, error={}", graphName, e.getMessage(), e);
            return buildPhoenixEmptyResponse(graphName);
        } catch (RuntimeException e) {
            log.error("Phoenix 프로젝트 ID 조회 실패 (런타임 오류): graphName={}, error={}", graphName, e.getMessage(), e);
            return buildPhoenixEmptyResponse(graphName);
        }
    }

   
    /**
     * 에이전트 빌더 Policy 설정
     *
     * @param agentId     에이전트 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return Void 설정된 Policy 목록
     */
    @PostMapping("/builder/{agent_id}/policy")
    @Operation(summary = "에이전트 빌더 Policy 설정", description = "에이전트 빌더의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "에이전트 빌더 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<Void> setAgentBuilderPolicy(
            @PathVariable(value = "agent_id", required = true) @Parameter(description = "에이전트 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String agentId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("에이전트 빌더 Policy 설정 요청 - agentId: {}, memberId: {}, projectName: {}", agentId, memberId,
                projectName);
        agentBuilderService.setAgentBuilderPolicy(agentId, memberId, projectName);
        return AxResponseEntity.ok(null, "에이전트 빌더 Policy가 성공적으로 설정되었습니다.");
    }

    /**
     * Phoenix 인증 설정 안내
     * 
     * 기본값: PHOENIX_ENABLE_AUTH=false (인증 비활성화 - 로그인 없이 접근 가능)
     * - Phoenix deployment.yaml에서 PHOENIX_ENABLE_AUTH=false 설정 (기본값)
     * - 백엔드도 동일한 환경 변수를 읽어서 프론트엔드에 전달
     * 
     * Phoenix 자동 로그인(인증 활성화)을 위해서는:
     * 
     * 1. Phoenix deployment.yaml에서 환경 변수 설정:
     * PHOENIX_ENABLE_AUTH=true
     * PHOENIX_OAUTH2_KEYCLOAK_CLIENT_ID="phoenix"
     * PHOENIX_OAUTH2_KEYCLOAK_CLIENT_SECRET="<client-secret>"
     * PHOENIX_OAUTH2_KEYCLOAK_OIDC_CONFIG_URL="https://<keycloak>/realms/<realm>/.well-known/openid-configuration"
     * 
     * 2. Keycloak에 Phoenix Client 등록:
     * - Client ID: phoenix
     * - Valid redirect URIs: https://adxp.mobigen.com/phoenix/*
     * - Web origins: https://adxp.mobigen.com
     * 
     * 3. AXPortal과 Phoenix가 같은 Keycloak Realm을 공유
     * 
     * 인증 활성화 시 프론트엔드에서 Phoenix URL로 직접 이동하면 Keycloak SSO가 작동합니다.
     * 인증 비활성화 시(기본값) Phoenix 로그인 없이 직접 접근 가능합니다.
     */

    @GetMapping("/builder/user")
    @Operation(summary = "사용자ID별 에이전트 빌더 목록 조회", description = "사용자ID별 SKT AI Platform의 에이전트 그래프 목록을 조회합니다.")
    public AxResponseEntity<PageResponse<AgentBuilderRes>> getAgentBuildersByUserId(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "12") @Parameter(description = "페이지 크기", example = "12") Integer size,
            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건", example = "created_at,desc") String sort,
            @RequestParam(value = "search", required = false) @Parameter(description = "검색 키워드") String search,
            @RequestParam(value = "member_id", required = false) @Parameter(description = "사용자 ID") String memberId) {

        log.debug("사용자ID별 에이전트 빌더 목록 조회 요청: memberId={}, page={}, size={}, sort={}, search={}",
                memberId, page, size, sort, search);
        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        String memberUuid = gpoUsersMasRepository.findByMemberId(memberId).orElse(null).getUuid();
        String filterData = "created_by:" + memberUuid;

        PageResponse<AgentBuilderRes> result = agentBuilderService.getAgentBuilders("24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", page, size, sort, filterData,
                search);

        log.debug("에이전트 빌더 목록 조회 완료: 총 {}개", result.getTotalElements());

        return AxResponseEntity.success(result);
    }

}