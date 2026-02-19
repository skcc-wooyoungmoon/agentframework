package com.skax.aiplatform.client.sktai.agentgateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.agentgateway.dto.request.BatchRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.InvokeRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamLogRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.AppsResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.BatchResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.InputSchemaResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.InvokeResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.OutputSchemaResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.StreamLogResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.StreamResponse;
import com.skax.aiplatform.client.sktai.agentgateway.service.SktaiAgentGatewayService;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Agent Gateway API Feign Client
 * 
 * <p>SKTAI Agent Gateway API와 통신하기 위한 Feign Client 인터페이스입니다.
 * Agent 추론 실행, 배치 처리, 스트리밍, 스키마 조회 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Apps 관리</strong>: Agent 애플리케이션 목록 조회</li>
 *   <li><strong>추론 실행</strong>: Agent 추론 요청 처리 (invoke)</li>
 *   <li><strong>배치 처리</strong>: 다중 요청 배치 실행</li>
 *   <li><strong>스트리밍</strong>: 실시간 응답 스트리밍 및 로그</li>
 *   <li><strong>스키마 조회</strong>: 입력/출력 스키마 정보 조회</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><code>GET /api/v1/agent_gateway/apps</code>: Apps 목록 조회</li>
 *   <li><code>POST /api/v1/agent_gateway/{agent_id}/invoke</code>: Agent 추론 실행</li>
 *   <li><code>POST /api/v1/agent_gateway/{agent_id}/batch</code>: 배치 추론 실행</li>
 *   <li><code>POST /api/v1/agent_gateway/{agent_id}/stream</code>: 스트리밍 추론</li>
 *   <li><code>POST /api/v1/agent_gateway/{agent_id}/stream_log</code>: 스트리밍 로그</li>
 *   <li><code>GET /api/v1/agent_gateway/{agent_id}/input_schema</code>: 입력 스키마 조회</li>
 *   <li><code>GET /api/v1/agent_gateway/{agent_id}/output_schema</code>: 출력 스키마 조회</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see SktaiAgentGatewayService 비즈니스 로직 서비스 래퍼
 */
@FeignClient(
    name = "sktai-agent-gateway-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Agent Gateway", description = "SKTAI Agent Gateway 추론 실행 API")
public interface SktaiAgentGatewayClient {

    /**
     * Apps 목록 조회
     * 
     * <p>사용 가능한 Agent 애플리케이션 목록을 조회합니다.
     * API 키와 앱 정보를 포함한 목록을 반환합니다.</p>
     * 
     * @return Apps 목록 정보
     */
    @GetMapping("/api/v1/agent_gateway/apps")
    @Operation(
        summary = "Apps 목록 조회",
        description = "사용 가능한 Agent 애플리케이션 목록을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Apps 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    AppsResponse getApps();

    /**
     * Agent 추론 실행
     * 
     * <p>지정된 Agent에 대해 추론을 실행합니다.
     * 단일 요청에 대한 즉시 응답을 반환합니다.</p>
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @param request 추론 요청 정보
     * @return 추론 실행 결과
     */
    @PostMapping(value = "/api/v1/agent_gateway/{agent_id}/invoke", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Agent 추론 실행",
        description = "지정된 Agent에 대해 추론을 실행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "추론 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Agent를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    InvokeResponse invoke(
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false, defaultValue = "") String routerPath,
        
        @RequestBody InvokeRequest request
    );

    /**
     * Agent 배치 추론 실행
     * 
     * <p>지정된 Agent에 대해 다중 요청을 배치로 실행합니다.
     * 여러 입력에 대한 결과를 배열로 반환합니다.</p>
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @param request 배치 추론 요청 정보
     * @return 배치 추론 실행 결과
     */
    @PostMapping(value = "/api/v1/agent_gateway/{agent_id}/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Agent 배치 추론 실행",
        description = "지정된 Agent에 대해 다중 요청을 배치로 실행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배치 추론 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Agent를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    BatchResponse batch(
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false, defaultValue = "") String routerPath,
        
        @RequestBody BatchRequest request
    );

    /**
     * Agent 스트리밍 추론 실행
     * 
     * <p>지정된 Agent에 대해 스트리밍 추론을 실행합니다.
     * 실시간으로 응답 데이터를 스트리밍합니다.</p>
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @param request 스트리밍 추론 요청 정보
     * @return 스트리밍 추론 실행 결과
     */
    @PostMapping(value = "/api/v1/agent_gateway/{agent_id}/stream", 
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = "text/event-stream")  // SSE 형식으로 응답 받기
    @Operation(
        summary = "Agent 스트리밍 추론 실행",
        description = "지정된 Agent에 대해 스트리밍 추론을 실행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스트리밍 추론 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Agent를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    StreamResponse stream(
        @Parameter(description = "Authorization 헤더 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @RequestHeader("Authorization") String authorization,
        
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false) String routerPath,
        
        @RequestBody StreamRequest request
    );

    /**
     * Agent 스트리밍 추론 실행 (Raw Response)
     * 
     * @param authorization Authorization 헤더
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로
     * @param request 스트리밍 요청 정보
     * @return 스트리밍 추론 실행 결과 (Raw String)
     */
    @PostMapping(value = "/api/v1/agent_gateway/{agent_id}/stream", 
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = "text/event-stream")
    @Operation(
        summary = "Agent 스트리밍 추론 실행 (Raw Response)",
        description = "Agent Gateway를 통해 스트리밍 추론을 실행하고 Raw 응답을 반환합니다."
    )
    String streamRaw(
        @Parameter(description = "Authorization 헤더 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @RequestHeader("Authorization") String authorization,
        
        @Parameter(description = "aip-user 헤더", required = false)
        @RequestHeader(value = "aip-user", required = false) String aipUser,
        
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false) String routerPath,
        
        @RequestBody StreamRequest request
    );

    /**
     * Agent 스트리밍 로그 실행
     * 
     * <p>지정된 Agent에 대해 스트리밍 로그를 실행합니다.
     * 실시간으로 로그와 응답 데이터를 스트리밍합니다.</p>
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @param request 스트리밍 로그 요청 정보
     * @return 스트리밍 로그 실행 결과
     */
    @PostMapping(value = "/api/v1/agent_gateway/{agent_id}/stream_log", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Agent 스트리밍 로그 실행",
        description = "지정된 Agent에 대해 스트리밍 로그를 실행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스트리밍 로그 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Agent를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    StreamLogResponse streamLog(
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false, defaultValue = "") String routerPath,
        
        @RequestBody StreamLogRequest request
    );

    /**
     * Agent 입력 스키마 조회
     * 
     * <p>지정된 Agent의 입력 스키마 정보를 조회합니다.
     * Agent가 허용하는 입력 형식과 필드 정보를 반환합니다.</p>
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @return 입력 스키마 정보
     */
    @GetMapping("/api/v1/agent_gateway/{agent_id}/input_schema")
    @Operation(
        summary = "Agent 입력 스키마 조회",
        description = "지정된 Agent의 입력 스키마 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "입력 스키마 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Agent를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    InputSchemaResponse getInputSchema(
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false, defaultValue = "") String routerPath
    );

    /**
     * Agent 출력 스키마 조회
     * 
     * <p>지정된 Agent의 출력 스키마 정보를 조회합니다.
     * Agent가 반환하는 출력 형식과 필드 정보를 반환합니다.</p>
     * 
     * @param agentId Agent 식별자
     * @param routerPath 라우터 경로 (선택사항)
     * @return 출력 스키마 정보
     */
    @GetMapping("/api/v1/agent_gateway/{agent_id}/output_schema")
    @Operation(
        summary = "Agent 출력 스키마 조회",
        description = "지정된 Agent의 출력 스키마 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "출력 스키마 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Agent를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    OutputSchemaResponse getOutputSchema(
        @Parameter(description = "Agent 식별자", required = true, example = "074c42fe-04ae-41a2-94f6-a3b6f722061e")
        @PathVariable("agent_id") String agentId,
        
        @Parameter(description = "라우터 경로", example = "")
        @RequestParam(value = "router_path", required = false, defaultValue = "") String routerPath
    );
}
