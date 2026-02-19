package com.skax.aiplatform.client.udp.dataiku;

import com.skax.aiplatform.client.udp.config.UdpDataikuFeignConfig;
import com.skax.aiplatform.client.udp.config.UdpFeignConfig;
import com.skax.aiplatform.client.udp.dataiku.dto.request.DataikuExecutionRequest;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuExecutionResponse;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader; // included via other imports

// no-op
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * UDP Dataiku API 클라이언트
 * 
 * <p>UDP 시스템의 Dataiku 시나리오 실행 및 상태 조회 API를 연동하는 Feign 클라이언트입니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>Dataiku 시나리오 실행</li>
 *   <li>실행 중인 시나리오 상태 조회</li>
 *   <li>Bearer 토큰 기반 인증</li>
 * </ul>
 * 
 * <h3>인증 방식:</h3>
 * <p>Dataiku API는 Bearer 토큰 인증을 사용하므로, 
 * 각 요청마다 Authorization 헤더에 토큰을 포함해야 합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@FeignClient(
    name = "udp-dataiku-client",
    url = "${udp.api.base-url}",
    configuration = UdpDataikuFeignConfig.class
)
@Tag(name = "UDP Dataiku API", description = "UDP Dataiku 시나리오 실행 및 상태 조회 API")
public interface UdpDataikuClient {

    /**
     * Dataiku 시나리오 실행 (원 경로)
     * 
     * <p>
     *   UDP 게이트웨이의 Dataiku 프록시 엔드포인트 규격에 맞춰 실행합니다.<br/>
     *   예: /udp/dataiku/{design|automation}/projects/{projectKey}/scenarios/{scenarioId}/run
     * </p>
     */
    @PostMapping(value = "/udp/dataiku/{environment}/projects/{projectKey}/scenarios/{scenarioId}/run",
                 consumes = "application/json",
                 produces = "application/json")
    @Operation(
        summary = "Dataiku 시나리오 실행",
        description = "Dataiku 시나리오를 실행합니다 (환경/프로젝트/시나리오 경로 가변)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "시나리오 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "시나리오를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    DataikuExecutionResponse executeScenario(
        @Parameter(description = "Bearer 토큰", required = true)
        @RequestHeader("Authorization") String authorization,

        @Parameter(description = "게이트웨이 API 키", required = true)
        @RequestHeader("x-cruz-api-key") String apiKey,

        @Parameter(description = "실행 환경 (design|automation)", required = true)
        @PathVariable("environment") String environment,

        @Parameter(description = "프로젝트 키", required = true)
        @PathVariable("projectKey") String projectKey,

        @Parameter(description = "시나리오 ID", required = true)
        @PathVariable("scenarioId") String scenarioId,

        @Parameter(description = "실행 요청 본문(JSON)", required = true)
        @RequestBody DataikuExecutionRequest body
    );

    /**
     * Dataiku 시나리오 실행 상태 조회
     * 
     * <p>실행 중인 시나리오의 현재 상태를 조회합니다.
     * Bearer 토큰 인증이 필요합니다.</p>
     * 
     * @param authorization Bearer 토큰 (예: "Bearer your-token-here")
     * @param runId 시나리오 실행 ID
     * @return 시나리오 실행 상태
     */
    @GetMapping(value = "/api/v1/dataiku/scenario/status/{runId}", 
                produces = "application/json")
    @Operation(
        summary = "Dataiku 시나리오 실행 상태 조회",
        description = "실행 중인 시나리오의 현재 상태를 조회합니다. Bearer 토큰 인증이 필요합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "실행 ID를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    DataikuStatusResponse getScenarioStatus(
        @Parameter(description = "Bearer 토큰", required = true, example = "Bearer your-token-here")
        @RequestHeader("Authorization") String authorization,
        
        @Parameter(description = "시나리오 실행 ID", required = true, example = "run_12345")
        @PathVariable("runId") String runId
    );

    /**
     * Dataiku Continuous Activities 조회
     * 
     * <p>프로젝트의 연속적인 활동(Continuous Activities) 목록을 조회합니다.</p>
     * 
     * @param authorization Bearer 토큰
     * @param apiKey 게이트웨이 API 키
     * @param environment 실행 환경 (design|automation)
     * @param projectKey 프로젝트 키
     * @return Continuous Activities 응답 (Object)
     */
    @GetMapping(value = "/udp/dataiku/{environment}/projects/{projectKey}/continuous-activities",
                 produces = "application/json")
    @Operation(
        summary = "Dataiku Continuous Activities 조회",
        description = "프로젝트의 연속적인 활동 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    Object getContinuousActivities(
        @Parameter(description = "Bearer 토큰", required = true)
        @RequestHeader("Authorization") String authorization,

        @Parameter(description = "게이트웨이 API 키", required = true)
        @RequestHeader("x-cruz-api-key") String apiKey,

        @Parameter(description = "실행 환경 (design|automation)", required = true)
        @PathVariable("environment") String environment,

        @Parameter(description = "프로젝트 키", required = true)
        @PathVariable("projectKey") String projectKey
    );
}