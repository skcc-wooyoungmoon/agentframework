package com.skax.aiplatform.client.lablup.api;

import com.skax.aiplatform.client.lablup.api.dto.request.GetEndpointRequest;
import com.skax.aiplatform.client.lablup.api.dto.response.GetEndpointResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetSessionLogResponse;
import com.skax.aiplatform.client.lablup.config.LablupClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Lablup 세션 관리 API Feign Client
 * 
 * <p>
 * Lablup Backend.AI 시스템의 세션(컨테이너) 관리를 위한 REST API 클라이언트입니다.
 * 컨테이너 로그 조회 등의 세션 관련 작업을 제공합니다.
 * </p>
 * 
 * <h3>지원 API:</h3>
 * <ul>
 * <li><strong>컨테이너 로그 조회</strong>: GET /session/{session_id}/logs</li>
 * <li><strong>엔드포인트 정보 조회</strong>: POST /graphql (endpoint 쿼리)</li>
 * </ul>
 * 
 * <h3>인증 방식:</h3>
 * <p>
 * Backend.AI API 클라이언트 표준 인증을 사용합니다:
 * </p>
 * <ul>
 * <li>X-BackendAI-Version 헤더</li>
 * <li>Date 헤더</li>
 * <li>Authorization 헤더 (API Key 기반 서명)</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@FeignClient(name = "lablup-session-client", url = "${lablup.api.backendai-base-url}", configuration = LablupClientConfig.class)
@Tag(name = "Lablup Session API", description = "Lablup 세션 관리 API")
public interface LablupSessionClient {

    /**
     * 컨테이너 로그 조회
     * 
     * <p>
     * 지정된 세션 ID의 컨테이너 로그를 조회합니다.
     * 멀티 노드 세션인 경우 kernel_id를 지정하여 서브 컨테이너의 로그를 가져올 수 있습니다.
     * </p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     * <li>세션 컨테이너의 실행 로그 조회</li>
     * <li>다른 사용자 세션 로그 조회 (owner_access_key 사용)</li>
     * <li>멀티 노드 세션의 서브 컨테이너 로그 조회 (kernel_id 사용)</li>
     * </ul>
     * 
     * @param sessionId      조회할 세션 ID
     * @param ownerAccessKey 다른 사용자 세션 조회를 위한 해당 사용자의 액세스 키 (선택사항)
     * @param kernelId       멀티 노드 세션에서 특정 서브 컨테이너(커널) ID (선택사항)
     * @return 세션 로그 조회 결과
     */
    @GetMapping(value = "/session/{sessionId}/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "컨테이너 로그 조회", description = "지정된 세션 ID의 컨테이너 로그를 조회합니다. 멀티 노드 세션의 경우 kernel_id로 서브 컨테이너 로그를 가져올 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "세션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GetSessionLogResponse getSessionLog(
            @Parameter(description = "세션 ID", required = true, example = "sess-12345678-1234-1234-1234-123456789abc") @PathVariable("sessionId") String sessionId,
            @Parameter(description = "다른 사용자 세션 조회를 위한 해당 사용자의 액세스 키") @RequestParam(value = "owner_access_key", required = false) String ownerAccessKey,
            @Parameter(description = "멀티 노드 세션에서 특정 서브 컨테이너(커널) ID") @RequestParam(value = "kernel_id", required = false) String kernelId);

    /**
     * 엔드포인트 정보 조회
     * 
     * <p>
     * GraphQL 쿼리를 사용하여 특정 엔드포인트의 상세 정보를 조회합니다.
     * 엔드포인트의 상태, 이미지 정보, 라우팅 정보 등을 확인할 수 있습니다.
     * </p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     * <li>엔드포인트의 상태 및 레플리카 정보 조회</li>
     * <li>이미지 및 런타임 variant 정보 확인</li>
     * <li>라우팅 정보 및 트래픽 비율 조회</li>
     * </ul>
     * 
     * @param request 엔드포인트 조회 요청 (GraphQL 쿼리 포함)
     * @return 엔드포인트 상세 정보
     */
    @PostMapping(value = "/admin/graphql", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "엔드포인트 정보 조회", description = "GraphQL endpoint 쿼리를 통해 엔드포인트의 상세 정보, 상태, 라우팅 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "GraphQL 쿼리 오류"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "엔드포인트를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GetEndpointResponse getEndpoint(
            @Parameter(description = "엔드포인트 조회 요청 (GraphQL 쿼리)", required = true) @RequestBody GetEndpointRequest request);
}