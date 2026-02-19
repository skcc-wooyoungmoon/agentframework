package com.skax.aiplatform.controller.sample;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingScale;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyVerify;
import com.skax.aiplatform.client.sktai.serving.dto.request.McpServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.McpServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingScale;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.SharedAgentBackendUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.SharedBackendCreate;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyVerifyResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeysResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.CreateServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.PolicyPayload;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingModelView;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingUpdateResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.SharedAgentBackendRead;
import com.skax.aiplatform.client.sktai.serving.dto.response.SharedAgentBackendsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.SktaiOperationResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.response.AxResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Serving 샘플 컨트롤러
 * 
 * <p>SKTAI Serving API 기능을 테스트하고 데모하기 위한 샘플 컨트롤러입니다.
 * 모델 서빙, 에이전트 서빙, API 키 관리, MCP 서빙, 공유 백엔드 관리 등의 모든 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Model Serving</strong>: 일반 모델 서빙 관리 및 스케일링</li>
 *   <li><strong>Agent Serving</strong>: 대화형 AI 서빙 관리</li>
 *   <li><strong>API Key Management</strong>: 서빙 접근을 위한 API 키 관리</li>
 *   <li><strong>MCP Serving</strong>: MCP(Model Control Protocol) 서빙 관리</li>
 *   <li><strong>Shared Backend</strong>: 공유 백엔드 리소스 관리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 모델 서빙 생성
 * POST /api/sample/serving
 * 
 * // 서빙 목록 조회
 * GET /api/sample/serving?page=1&size=20
 * 
 * // 서빙 상세 정보 조회
 * GET /api/sample/serving/{servingId}
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see SktaiServingService SKTAI Serving 서비스
 */
@Tag(name = "Sample Serving API", description = "SKTAI Serving API 샘플 컨트롤러")
@RestController
@RequestMapping("/api/sample/serving")
@RequiredArgsConstructor
@Slf4j
public class SampleServingController {

    private final SktaiServingService sktaiServingService;

    // ==================== Model Serving Management ====================

    /**
     * 모델 서빙 생성
     */
    @PostMapping
    @Operation(summary = "모델 서빙 생성", description = "새로운 모델 서빙을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "모델 서빙 생성 성공",
                    content = @Content(schema = @Schema(implementation = CreateServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public AxResponseEntity<CreateServingResponse> createServing(
            @Parameter(description = "모델 서빙 생성 요청", required = true)
            @Valid @RequestBody ServingCreate request) {
        
        log.info("모델 서빙 생성 요청 - name: {}, modelId: {}", request.getName(), request.getModelId());
        CreateServingResponse response = sktaiServingService.createServing(request);
        return AxResponseEntity.created(response, "모델 서빙이 성공적으로 생성되었습니다.");
    }

    /**
     * 모델 서빙 목록 조회
     */
    @GetMapping
    @Operation(summary = "모델 서빙 목록 조회", description = "페이징된 모델 서빙 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ServingsResponse.class)))
    })
    public AxResponseEntity<ServingsResponse> getServings(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션", example = "created_at:desc")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건")
            @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("모델 서빙 목록 조회 요청 - page: {}, size: {}", page, size);
        ServingsResponse response = sktaiServingService.getServings(page, size, sort, filter, search);
        return AxResponseEntity.ok(response, "모델 서빙 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 모델 서빙 상세 조회
     */
    @GetMapping("/{servingId}")
    @Operation(summary = "모델 서빙 상세 조회", description = "특정 모델 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 조회 성공",
                    content = @Content(schema = @Schema(implementation = ServingResponse.class))),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    public AxResponseEntity<ServingResponse> getServing(
            @Parameter(description = "조회할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId) {
        
        log.info("모델 서빙 상세 조회 요청 - servingId: {}", servingId);
        ServingResponse response = sktaiServingService.getServing(servingId);
        return AxResponseEntity.ok(response, "모델 서빙 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 모델 서빙 수정
     */
    @PutMapping("/{servingId}")
    @Operation(summary = "모델 서빙 수정", description = "기존 모델 서빙의 설정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 수정 성공",
                    content = @Content(schema = @Schema(implementation = ServingUpdateResponse.class))),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    public AxResponseEntity<ServingUpdateResponse> updateServing(
            @Parameter(description = "수정할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId,
            @Parameter(description = "모델 서빙 수정 요청", required = true)
            @Valid @RequestBody ServingUpdate request) {
        
        log.info("모델 서빙 수정 요청 - servingId: {}", servingId);
        ServingUpdateResponse response = sktaiServingService.updateServing(servingId, request);
        return AxResponseEntity.updated(response, "모델 서빙이 성공적으로 수정되었습니다.");
    }

    /**
     * 모델 서빙 삭제
     */
    @DeleteMapping("/{servingId}")
    @Operation(summary = "모델 서빙 삭제", description = "지정된 모델 서빙을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    public AxResponseEntity<Void> deleteServing(
            @Parameter(description = "삭제할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId) {
        
        log.info("모델 서빙 삭제 요청 - servingId: {}", servingId);
        sktaiServingService.deleteServing(servingId);
        return AxResponseEntity.deleted("모델 서빙이 성공적으로 삭제되었습니다.");
    }

    /**
     * 모델 서빙 스케일링
     */
    @PostMapping("/{servingId}/scale")
    @Operation(summary = "모델 서빙 스케일링", description = "모델 서빙의 레플리카 수를 조정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 스케일링 성공",
                    content = @Content(schema = @Schema(implementation = ServingResponse.class)))
    })
    public AxResponseEntity<ServingResponse> scaleServing(
            @Parameter(description = "스케일링할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId,
            @Parameter(description = "스케일링 요청", required = true)
            @Valid @RequestBody ServingScale request) {
        
        log.info("모델 서빙 스케일링 요청 - servingId: {}, replicas: {}", servingId, request.getReplicas());
        ServingResponse response = sktaiServingService.scaleServing(servingId, request);
        return AxResponseEntity.ok(response, "모델 서빙 스케일링이 성공적으로 완료되었습니다.");
    }

    /**
     * 모델 서빙 시작
     */
    @PostMapping("/{servingId}/start")
    @Operation(summary = "모델 서빙 시작", description = "중지된 모델 서빙을 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "모델 서빙 시작 요청 성공",
                    content = @Content(schema = @Schema(implementation = SktaiOperationResponse.class)))
    })
    public AxResponseEntity<SktaiOperationResponse> startServing(
            @Parameter(description = "시작할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId) {
        
        log.info("모델 서빙 시작 요청 - servingId: {}", servingId);
        SktaiOperationResponse response = sktaiServingService.startServing(servingId);
        return AxResponseEntity.ok(response, "모델 서빙 시작 요청이 성공적으로 처리되었습니다.");
    }

    /**
     * 모델 서빙 중지
     */
    @PostMapping("/{servingId}/stop")
    @Operation(summary = "모델 서빙 중지", description = "실행 중인 모델 서빙을 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "모델 서빙 중지 요청 성공",
                    content = @Content(schema = @Schema(implementation = SktaiOperationResponse.class)))
    })
    public AxResponseEntity<SktaiOperationResponse> stopServing(
            @Parameter(description = "중지할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId) {
        
        log.info("모델 서빙 중지 요청 - servingId: {}", servingId);
        SktaiOperationResponse response = sktaiServingService.stopServing(servingId);
        return AxResponseEntity.ok(response, "모델 서빙 중지 요청이 성공적으로 처리되었습니다.");
    }

    /**
     * 모델 서빙 모델 뷰 조회
     */
    @GetMapping("/{servingId}/view")
    @Operation(summary = "서빙 모델 뷰 조회", description = "지정된 서빙의 모델 뷰 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 뷰 조회 성공",
                    content = @Content(schema = @Schema(implementation = ServingModelView.class))),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    public AxResponseEntity<ServingModelView> getServingModelView(
            @Parameter(description = "조회할 서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId) {
        
        log.info("서빙 모델 뷰 조회 요청 - servingId: {}", servingId);
        ServingModelView response = sktaiServingService.getServingModelView(servingId);
        return AxResponseEntity.ok(response, "서빙 모델 뷰 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 모델 서빙 API 키 목록 조회
     */
    @GetMapping("/{servingId}/apikeys")
    @Operation(summary = "모델 서빙 API 키 목록 조회", description = "특정 모델 서빙에서 사용 가능한 API 키 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiKeysResponse.class)))
    })
    public AxResponseEntity<ApiKeysResponse> getServingApiKeys(
            @Parameter(description = "서빙 ID", required = true, example = "serving-123")
            @PathVariable String servingId,
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건")
            @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("모델 서빙 API 키 목록 조회 요청 - servingId: {}", servingId);
        ApiKeysResponse response = sktaiServingService.getServingApiKeys(servingId, page, size, sort, filter, search);
        return AxResponseEntity.ok(response, "모델 서빙 API 키 목록을 성공적으로 조회했습니다.");
    }

    // ==================== Agent Serving Management ====================

    /**
     * 에이전트 서빙 생성
     */
    @PostMapping("/agent")
    @Operation(summary = "에이전트 서빙 생성", description = "새로운 에이전트 서빙을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "에이전트 서빙 생성 성공",
                    content = @Content(schema = @Schema(implementation = AgentServingResponse.class)))
    })
    public AxResponseEntity<AgentServingResponse> createAgentServing(
            @Parameter(description = "에이전트 서빙 생성 요청", required = true)
            @Valid @RequestBody AgentServingCreate request) {
        
        log.info("에이전트 서빙 생성 요청 - name: {}, agentId: {}", request.getAgentServingName(), request.getAgentId());
        AgentServingResponse response = sktaiServingService.createAgentServing(request);
        return AxResponseEntity.created(response, "에이전트 서빙이 성공적으로 생성되었습니다.");
    }

    /**
     * 에이전트 서빙 목록 조회
     */
    @GetMapping("/agent")
    @Operation(summary = "에이전트 서빙 목록 조회", description = "페이징된 에이전트 서빙 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = AgentServingsResponse.class)))
    })
    public AxResponseEntity<AgentServingsResponse> getAgentServings(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건")
            @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("에이전트 서빙 목록 조회 요청 - page: {}, size: {}", page, size);
        AgentServingsResponse response = sktaiServingService.getAgentServings(page, size, sort, filter, search);
        return AxResponseEntity.ok(response, "에이전트 서빙 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 에이전트 서빙 상세 조회
     */
    @GetMapping("/agent/{agentServingId}")
    @Operation(summary = "에이전트 서빙 상세 조회", description = "특정 에이전트 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 조회 성공",
                    content = @Content(schema = @Schema(implementation = AgentServingResponse.class))),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    public AxResponseEntity<AgentServingResponse> getAgentServing(
            @Parameter(description = "조회할 에이전트 서빙 ID", required = true, example = "agent-123")
            @PathVariable String agentServingId) {
        
        log.info("에이전트 서빙 상세 조회 요청 - agentServingId: {}", agentServingId);
        AgentServingResponse response = sktaiServingService.getAgentServing(agentServingId);
        return AxResponseEntity.ok(response, "에이전트 서빙 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 에이전트 서빙 상세 정보 조회
     */
    @GetMapping("/agent/{agentServingId}/info")
    @Operation(summary = "에이전트 서빙 상세 정보 조회", description = "지정된 에이전트 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = AgentServingInfo.class))),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    public AxResponseEntity<AgentServingInfo> getAgentServingInfo(
            @Parameter(description = "조회할 에이전트 서빙 ID", required = true, example = "agent-123")
            @PathVariable String agentServingId) {
        
        log.info("에이전트 서빙 상세 정보 조회 요청 - agentServingId: {}", agentServingId);
        AgentServingInfo response = sktaiServingService.getAgentServingInfo(agentServingId);
        return AxResponseEntity.ok(response, "에이전트 서빙 상세 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 에이전트 서빙 수정
     */
    @PutMapping("/agent/{agentServingId}")
    @Operation(summary = "에이전트 서빙 수정", description = "기존 에이전트 서빙의 설정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 수정 성공",
                    content = @Content(schema = @Schema(implementation = AgentServingResponse.class)))
    })
    public AxResponseEntity<AgentServingResponse> updateAgentServing(
            @Parameter(description = "수정할 에이전트 서빙 ID", required = true, example = "agent-123")
            @PathVariable String agentServingId,
            @Parameter(description = "에이전트 서빙 수정 요청", required = true)
            @Valid @RequestBody AgentServingUpdate request) {
        
        log.info("에이전트 서빙 수정 요청 - agentServingId: {}", agentServingId);
        AgentServingResponse response = sktaiServingService.updateAgentServing(agentServingId, request);
        return AxResponseEntity.updated(response, "에이전트 서빙이 성공적으로 수정되었습니다.");
    }

    /**
     * 에이전트 서빙 삭제
     */
    @DeleteMapping("/agent/{agentServingId}")
    @Operation(summary = "에이전트 서빙 삭제", description = "지정된 에이전트 서빙을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 삭제 성공")
    })
    public AxResponseEntity<Void> deleteAgentServing(
            @Parameter(description = "삭제할 에이전트 서빙 ID", required = true, example = "agent-123")
            @PathVariable String agentServingId) {
        
        log.info("에이전트 서빙 삭제 요청 - agentServingId: {}", agentServingId);
        sktaiServingService.deleteAgentServing(agentServingId);
        return AxResponseEntity.deleted("에이전트 서빙이 성공적으로 삭제되었습니다.");
    }

    /**
     * 에이전트 서빙 스케일링
     */
    @PostMapping("/agent/{agentServingId}/scale")
    @Operation(summary = "에이전트 서빙 스케일링", description = "에이전트 서빙의 레플리카 수를 조정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 스케일링 성공",
                    content = @Content(schema = @Schema(implementation = AgentServingResponse.class)))
    })
    public AxResponseEntity<AgentServingResponse> scaleAgentServing(
            @Parameter(description = "스케일링할 에이전트 서빙 ID", required = true, example = "agent-123")
            @PathVariable String agentServingId,
            @Parameter(description = "스케일링 요청", required = true)
            @Valid @RequestBody AgentServingScale request) {
        
        log.info("에이전트 서빙 스케일링 요청 - agentServingId: {}, replicas: {}", agentServingId, request.getReplicas());
        AgentServingResponse response = sktaiServingService.scaleAgentServing(agentServingId, request);
        return AxResponseEntity.ok(response, "에이전트 서빙 스케일링이 성공적으로 완료되었습니다.");
    }

    // ==================== API Key Management ====================

    /**
     * API 키 생성
     */
    @PostMapping("/apikeys")
    @Operation(summary = "API 키 생성", description = "서빙 엔드포인트에 접근하기 위한 새로운 API 키를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "API 키 생성 성공",
                    content = @Content(schema = @Schema(implementation = ApiKeyResponse.class)))
    })
    public AxResponseEntity<ApiKeyResponse> createApiKey(
            @Parameter(description = "API 키 생성 요청", required = true)
            @Valid @RequestBody ApiKeyCreate request) {
        
        log.info("API 키 생성 요청 - servingId: {}, gatewayType: {}", 
                request.getServingId(), request.getGatewayType());
        ApiKeyResponse response = sktaiServingService.createApiKey(request);
        return AxResponseEntity.created(response, "API 키가 성공적으로 생성되었습니다.");
    }

    /**
     * API 키 목록 조회
     */
    @GetMapping("/apikeys")
    @Operation(summary = "API 키 목록 조회", description = "사용자가 생성한 모든 API 키의 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiKeysResponse.class)))
    })
    public AxResponseEntity<ApiKeysResponse> getApiKeys(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건")
            @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("API 키 목록 조회 요청 - page: {}, size: {}", page, size);
        ApiKeysResponse response = sktaiServingService.getApiKeys(page, size, sort, filter, search);
        return AxResponseEntity.ok(response, "API 키 목록을 성공적으로 조회했습니다.");
    }

    /**
     * API 키 상세 조회
     */
    @GetMapping("/apikeys/{apiKeyId}")
    @Operation(summary = "API 키 상세 조회", description = "특정 API 키의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiKeyResponse.class))),
            @ApiResponse(responseCode = "404", description = "API 키를 찾을 수 없음")
    })
    public AxResponseEntity<ApiKeyResponse> getApiKey(
            @Parameter(description = "조회할 API 키 ID", required = true, example = "apikey-123")
            @PathVariable String apiKeyId) {
        
        log.info("API 키 상세 조회 요청 - apiKeyId: {}", apiKeyId);
        ApiKeyResponse response = sktaiServingService.getApiKey(apiKeyId);
        return AxResponseEntity.ok(response, "API 키 정보를 성공적으로 조회했습니다.");
    }

    /**
     * API 키 업데이트
     */
    @PutMapping("/apikeys/{apiKeyId}")
    @Operation(summary = "API 키 업데이트", description = "기존 API 키의 설정을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = ApiKeyResponse.class)))
    })
    public AxResponseEntity<ApiKeyResponse> updateApiKey(
            @Parameter(description = "업데이트할 API 키 ID", required = true, example = "apikey-123")
            @PathVariable String apiKeyId,
            @Parameter(description = "API 키 업데이트 요청", required = true)
            @Valid @RequestBody ApiKeyUpdate request) {
        
        log.info("API 키 업데이트 요청 - apiKeyId: {}", apiKeyId);
        ApiKeyResponse response = sktaiServingService.updateApiKey(apiKeyId, request);
        return AxResponseEntity.updated(response, "API 키가 성공적으로 업데이트되었습니다.");
    }

    /**
     * API 키 삭제
     */
    @DeleteMapping("/apikeys/{apiKeyId}")
    @Operation(summary = "API 키 삭제", description = "지정된 API 키를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 삭제 성공")
    })
    public AxResponseEntity<Void> deleteApiKey(
            @Parameter(description = "삭제할 API 키 ID", required = true, example = "apikey-123")
            @PathVariable String apiKeyId) {
        
        log.info("API 키 삭제 요청 - apiKeyId: {}", apiKeyId);
        sktaiServingService.deleteApiKey(apiKeyId);
        return AxResponseEntity.deleted("API 키가 성공적으로 삭제되었습니다.");
    }

    /**
     * API 키 검증
     */
    @PostMapping("/apikeys/verify")
    @Operation(summary = "API 키 검증", description = "API 키의 유효성을 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 검증 성공",
                    content = @Content(schema = @Schema(implementation = ApiKeyVerifyResponse.class)))
    })
    public AxResponseEntity<ApiKeyVerifyResponse> verifyApiKey(
            @Parameter(description = "API 키 검증 요청", required = true)
            @Valid @RequestBody ApiKeyVerify request) {
        
        log.info("API 키 검증 요청 - projectId: {}", request.getProjectId());
        ApiKeyVerifyResponse response = sktaiServingService.verifyApiKey(request);
        return AxResponseEntity.ok(response, "API 키 검증이 성공적으로 완료되었습니다.");
    }

    // ==================== MCP Serving Management ====================

    /**
     * MCP 서빙 생성
     */
    @PostMapping("/mcp")
    @Operation(summary = "MCP 서빙 생성", description = "새로운 MCP 서빙을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "MCP 서빙 생성 성공",
                    content = @Content(schema = @Schema(implementation = McpServingResponse.class)))
    })
    public AxResponseEntity<McpServingResponse> createMcpServing(
            @Parameter(description = "MCP 서빙 생성 요청", required = true)
            @Valid @RequestBody McpServingCreate request) {
        
        log.info("MCP 서빙 생성 요청 - deploymentName: {}", request.getDeploymentName());
        McpServingResponse response = sktaiServingService.createMcpServing(request);
        return AxResponseEntity.created(response, "MCP 서빙이 성공적으로 생성되었습니다.");
    }

    /**
     * MCP 서빙 목록 조회
     */
    @GetMapping("/mcp")
    @Operation(summary = "MCP 서빙 목록 조회", description = "페이징된 MCP 서빙 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = McpServingsResponse.class)))
    })
    public AxResponseEntity<McpServingsResponse> getMcpServings(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건")
            @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("MCP 서빙 목록 조회 요청 - page: {}, size: {}", page, size);
        McpServingsResponse response = sktaiServingService.getMcpServings(page, size, sort, filter, search);
        return AxResponseEntity.ok(response, "MCP 서빙 목록을 성공적으로 조회했습니다.");
    }

    /**
     * MCP 서빙 상세 조회
     */
    @GetMapping("/mcp/{mcpServingId}")
    @Operation(summary = "MCP 서빙 상세 조회", description = "특정 MCP 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 조회 성공",
                    content = @Content(schema = @Schema(implementation = McpServingInfo.class))),
            @ApiResponse(responseCode = "404", description = "MCP 서빙을 찾을 수 없음")
    })
    public AxResponseEntity<McpServingInfo> getMcpServing(
            @Parameter(description = "조회할 MCP 서빙 ID", required = true, example = "mcp-123")
            @PathVariable String mcpServingId) {
        
        log.info("MCP 서빙 상세 조회 요청 - mcpServingId: {}", mcpServingId);
        McpServingInfo response = sktaiServingService.getMcpServing(mcpServingId);
        return AxResponseEntity.ok(response, "MCP 서빙 정보를 성공적으로 조회했습니다.");
    }

    /**
     * MCP 서빙 업데이트
     */
    @PutMapping("/mcp/{mcpServingId}")
    @Operation(summary = "MCP 서빙 업데이트", description = "기존 MCP 서빙의 설정을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = SktaiOperationResponse.class)))
    })
    public AxResponseEntity<SktaiOperationResponse> updateMcpServing(
            @Parameter(description = "업데이트할 MCP 서빙 ID", required = true, example = "mcp-123")
            @PathVariable String mcpServingId,
            @Parameter(description = "MCP 서빙 업데이트 요청", required = true)
            @Valid @RequestBody McpServingUpdate request) {
        
        log.info("MCP 서빙 업데이트 요청 - mcpServingId: {}", mcpServingId);
        SktaiOperationResponse response = sktaiServingService.updateMcpServing(mcpServingId, request);
        return AxResponseEntity.ok(response, "MCP 서빙이 성공적으로 업데이트되었습니다.");
    }

    /**
     * MCP 서빙 삭제
     */
    @DeleteMapping("/mcp/{mcpServingId}")
    @Operation(summary = "MCP 서빙 삭제", description = "지정된 MCP 서빙을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 삭제 성공")
    })
    public AxResponseEntity<Void> deleteMcpServing(
            @Parameter(description = "삭제할 MCP 서빙 ID", required = true, example = "mcp-123")
            @PathVariable String mcpServingId) {
        
        log.info("MCP 서빙 삭제 요청 - mcpServingId: {}", mcpServingId);
        sktaiServingService.deleteMcpServing(mcpServingId);
        return AxResponseEntity.deleted("MCP 서빙이 성공적으로 삭제되었습니다.");
    }

    // ==================== Shared Backend Management ====================

    /**
     * Shared Backend 목록 조회
     */
    @GetMapping("/shared-backends")
    @Operation(summary = "Shared Backend 목록 조회", description = "모든 Shared Backend 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared Backend 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = SharedAgentBackendsResponse.class)))
    })
    public AxResponseEntity<SharedAgentBackendsResponse> getSharedBackends() {
        log.info("Shared Backend 목록 조회 요청");
        SharedAgentBackendsResponse response = sktaiServingService.getSharedBackends();
        return AxResponseEntity.ok(response, "Shared Backend 목록을 성공적으로 조회했습니다.");
    }

    /**
     * Shared Backend 조회
     */
    @GetMapping("/shared-backends/{projectId}")
    @Operation(summary = "Shared Backend 조회", description = "프로젝트 ID로 Shared Backend 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared Backend 조회 성공",
                    content = @Content(schema = @Schema(implementation = SharedAgentBackendRead.class))),
            @ApiResponse(responseCode = "404", description = "Shared Backend를 찾을 수 없음")
    })
    public AxResponseEntity<SharedAgentBackendRead> getSharedBackend(
            @Parameter(description = "프로젝트 ID", required = true, example = "project-123")
            @PathVariable String projectId) {
        
        log.info("Shared Backend 조회 요청 - projectId: {}", projectId);
        SharedAgentBackendRead response = sktaiServingService.getSharedBackend(projectId);
        return AxResponseEntity.ok(response, "Shared Backend 정보를 성공적으로 조회했습니다.");
    }

    /**
     * Shared Backend 생성
     */
    @PostMapping("/shared-backends/{projectId}")
    @Operation(summary = "Shared Backend 생성", description = "새로운 Shared Backend를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shared Backend 생성 성공",
                    content = @Content(schema = @Schema(implementation = SharedAgentBackendRead.class)))
    })
    public AxResponseEntity<SharedAgentBackendRead> createSharedBackend(
            @Parameter(description = "프로젝트 ID", required = true, example = "project-123")
            @PathVariable String projectId,
            @Parameter(description = "Shared Backend 생성 요청", required = true)
            @Valid @RequestBody SharedBackendCreate request) {
        
        log.info("Shared Backend 생성 요청 - projectId: {}", projectId);
        SharedAgentBackendRead response = sktaiServingService.createSharedBackend(projectId, request);
        return AxResponseEntity.created(response, "Shared Backend가 성공적으로 생성되었습니다.");
    }

    /**
     * Shared Backend 업데이트
     */
    @PutMapping("/shared-backends/{projectId}")
    @Operation(summary = "Shared Backend 업데이트", description = "기존 Shared Backend를 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared Backend 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = SharedAgentBackendRead.class)))
    })
    public AxResponseEntity<SharedAgentBackendRead> updateSharedBackend(
            @Parameter(description = "프로젝트 ID", required = true, example = "project-123")
            @PathVariable String projectId,
            @Parameter(description = "Shared Backend 업데이트 요청", required = true)
            @Valid @RequestBody SharedAgentBackendUpdate request) {
        
        log.info("Shared Backend 업데이트 요청 - projectId: {}", projectId);
        SharedAgentBackendRead response = sktaiServingService.updateSharedBackend(projectId, request);
        return AxResponseEntity.updated(response, "Shared Backend가 성공적으로 업데이트되었습니다.");
    }

    /**
     * Shared Backend 시작
     */
    @PostMapping("/shared-backends/{projectId}/start")
    @Operation(summary = "Shared Backend 시작", description = "지정된 Shared Backend를 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Shared Backend 시작 요청 성공",
                    content = @Content(schema = @Schema(implementation = SktaiOperationResponse.class)))
    })
    public AxResponseEntity<SktaiOperationResponse> startSharedBackend(
            @Parameter(description = "프로젝트 ID", required = true, example = "project-123")
            @PathVariable String projectId) {
        
        log.info("Shared Backend 시작 요청 - projectId: {}", projectId);
        SktaiOperationResponse response = sktaiServingService.startSharedBackend(projectId);
        return AxResponseEntity.ok(response, "Shared Backend 시작 요청이 성공적으로 처리되었습니다.");
    }

    /**
     * Shared Backend 중지
     */
    @PostMapping("/shared-backends/{projectId}/stop")
    @Operation(summary = "Shared Backend 중지", description = "지정된 Shared Backend를 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Shared Backend 중지 요청 성공",
                    content = @Content(schema = @Schema(implementation = SktaiOperationResponse.class)))
    })
    public AxResponseEntity<SktaiOperationResponse> stopSharedBackend(
            @Parameter(description = "프로젝트 ID", required = true, example = "project-123")
            @PathVariable String projectId) {
        
        log.info("Shared Backend 중지 요청 - projectId: {}", projectId);
        SktaiOperationResponse response = sktaiServingService.stopSharedBackend(projectId);
        return AxResponseEntity.ok(response, "Shared Backend 중지 요청이 성공적으로 처리되었습니다.");
    }

    // ==================== Policies Management ====================

    /**
     * 정책 목록 조회
     */
    @GetMapping("/policies")
    @Operation(summary = "정책 목록 조회", description = "서빙 접근 정책 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PolicyPayload.class)))
    })
    public AxResponseEntity<PolicyPayload> getPolicies(
            @Parameter(description = "페이지 번호", example = "1")
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션", example = "created_at:desc")
            @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "검색어", example = "policy")
            @RequestParam(value = "search", required = false) String search) {
        
        log.info("정책 목록 조회 요청 - page: {}, size: {}", page, size);
        PolicyPayload response = sktaiServingService.getPolicies(page, size, sort, search);
        return AxResponseEntity.ok(response, "정책 목록을 성공적으로 조회했습니다.");
    }
}