package com.skax.aiplatform.client.sktai.agent;

import com.skax.aiplatform.client.sktai.agent.dto.response.DefaultStatusResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.DefaultInfoResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.TestTracingResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ProfileFilesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ProfileFileDeleteResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * SKTAI Agent Default API Feign Client
 * 
 * <p>SKTAI Agent 시스템의 기본 정보, 상태 조회, 테스트 추적, 프로파일 관리 등을 위한 Feign Client입니다.
 * 시스템 정보, 기본 설정, 상태 확인, 성능 분석 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>시스템 상태</strong>: Agent 시스템의 전반적인 상태 확인</li>
 *   <li><strong>기본 정보</strong>: 시스템 버전, 설정 정보 조회</li>
 *   <li><strong>테스트 추적</strong>: 클라이언트/앱별 테스트 추적 및 모니터링</li>
 *   <li><strong>프로파일 관리</strong>: 코드 프로파일 파일 목록 조회 및 삭제</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><code>GET /api/v1/agent/default/status</code>: 시스템 상태 조회</li>
 *   <li><code>GET /api/v1/agent/default/info</code>: 시스템 정보 조회</li>
 *   <li><code>GET /test-tracing/{client_id}/{app_id}</code>: 테스트 추적 조회</li>
 *   <li><code>GET /profile_requests</code>: 프로파일 파일 목록 조회</li>
 *   <li><code>DELETE /profile_requests/{filename}</code>: 프로파일 파일 삭제</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.1
 */
@FeignClient(
    name = "sktai-agent-default-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiAgentDefaultClient {

    /**
     * Agent 시스템 상태 조회
     * 
     * <p>SKTAI Agent 시스템의 전반적인 상태를 조회합니다.
     * 시스템 가용성, 성능 지표, 연결 상태 등을 확인할 수 있습니다.</p>
     * 
     * @return 시스템 상태 정보
     */
    @Operation(
        summary = "Agent 시스템 상태 조회",
        description = "SKTAI Agent 시스템의 전반적인 상태를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "시스템 상태 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/api/v1/agent/default/status")
    DefaultStatusResponse getSystemStatus();

    /**
     * Agent 시스템 정보 조회
     * 
     * <p>SKTAI Agent 시스템의 기본 정보를 조회합니다.
     * 버전 정보, 설정 값, 지원 기능 등을 확인할 수 있습니다.</p>
     * 
     * @return 시스템 정보
     */
    @Operation(
        summary = "Agent 시스템 정보 조회",
        description = "SKTAI Agent 시스템의 기본 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "시스템 정보 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/api/v1/agent/default/info")
    DefaultInfoResponse getSystemInfo();

    /**
     * 테스트 추적 조회
     * 
     * <p>특정 클라이언트와 애플리케이션에 대한 테스트 추적 정보를 조회합니다.
     * 테스트 실행 상태, 성능 지표, 디버깅 정보 등을 확인할 수 있습니다.</p>
     * 
     * @param clientId 클라이언트 식별자
     * @param appId 애플리케이션 식별자
     * @return 테스트 추적 정보
     */
    @Operation(
        summary = "테스트 추적 조회",
        description = "특정 클라이언트와 애플리케이션에 대한 테스트 추적 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "테스트 추적 정보 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "클라이언트 또는 앱을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/test-tracing/{client_id}/{app_id}")
    TestTracingResponse getTestTracing(
        @Parameter(description = "클라이언트 식별자", required = true, example = "client-001")
        @PathVariable("client_id") String clientId,
        
        @Parameter(description = "애플리케이션 식별자", required = true, example = "app-456")
        @PathVariable("app_id") String appId
    );

    /**
     * 프로파일 파일 목록 조회
     * 
     * <p>현재 시스템에 저장된 코드 프로파일 파일들의 목록을 조회합니다.
     * 파일명, 크기, 생성일시, 타입 등의 정보를 확인할 수 있습니다.</p>
     * 
     * @return 프로파일 파일 목록
     */
    @Operation(
        summary = "프로파일 파일 목록 조회",
        description = "현재 시스템에 저장된 코드 프로파일 파일들의 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로파일 파일 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/profile_requests")
    ProfileFilesResponse getProfileFiles();

    /**
     * 프로파일 파일 삭제
     * 
     * <p>지정된 파일명의 코드 프로파일 파일을 삭제합니다.
     * 삭제 후에는 해당 파일의 데이터를 복구할 수 없습니다.</p>
     * 
     * @param filename 삭제할 프로파일 파일명
     * @return 삭제 결과 정보
     */
    @Operation(
        summary = "프로파일 파일 삭제",
        description = "지정된 파일명의 코드 프로파일 파일을 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로파일 파일 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일명"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/profile_requests/{filename}")
    ProfileFileDeleteResponse deleteProfileFile(
        @Parameter(description = "삭제할 프로파일 파일명", required = true, example = "profile_20250822_001.json")
        @PathVariable("filename") String filename
    );
}
