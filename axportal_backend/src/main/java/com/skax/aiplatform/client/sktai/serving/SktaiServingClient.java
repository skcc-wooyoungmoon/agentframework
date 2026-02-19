package com.skax.aiplatform.client.sktai.serving;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingScale;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyVerify;
import com.skax.aiplatform.client.sktai.serving.dto.request.McpServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.McpServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.BackendAiServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingScale;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.SharedBackendCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.SharedAgentBackendUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.BackendAiServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeysResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyVerifyResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.PolicyPayload;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingModelView;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingUpdateResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.CreateServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.SharedAgentBackendRead;
import com.skax.aiplatform.client.sktai.serving.dto.response.SharedAgentBackendsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.SktaiOperationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Serving API Client
 * 
 * <p>
 * SKTAI Serving 시스템과의 통신을 담당하는 Feign Client입니다.
 * 모델 서빙, 에이전트 서빙, 공유 백엔드, API 키 관리 등의 기능을 제공합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Model Serving</strong>: 일반 모델 서빙 관리 (생성, 조회, 수정, 삭제, 스케일링)</li>
 * <li><strong>Agent Serving</strong>: 에이전트 모델 서빙 관리 (대화형 AI 서비스)</li>
 * <li><strong>Shared Backend</strong>: 공유 백엔드 관리 (여러 서빙이 공유하는 백엔드 리소스)</li>
 * <li><strong>API Key Management</strong>: API 키 관리 (생성, 조회, 삭제)</li>
 * </ul>
 * 
 * <h3>인증 방식:</h3>
 * <p>
 * OAuth2 Bearer Token 인증을 사용하며, SktaiClientConfig를 통해 공통 설정이 적용됩니다.
 * </p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiClientConfig Feign Client 공통 설정
 */
@FeignClient(name = "sktai-serving-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
@Tag(name = "SKTAI Serving API", description = "SKTAI 모델 서빙 및 에이전트 서빙 관리 API")
public interface SktaiServingClient {

    // ==================== Model Serving Management ====================

    /**
     * 새로운 모델 서빙 생성
     * 
     * <p>
     * 지정된 모델을 사용하여 새로운 서빙 인스턴스를 생성합니다.
     * 리소스 할당, 오토스케일링 설정, 보안 필터 등을 포함하여 설정할 수 있습니다.
     * </p>
     * 
     * @param request 모델 서빙 생성 요청 정보
     * @return 생성된 모델 서빙 정보
     */
    @PostMapping("/api/v1/servings")
    @Operation(summary = "모델 서빙 생성", description = "새로운 모델 서빙 인스턴스를 생성합니다. 모델 ID, 리소스 할당, 오토스케일링 설정 등을 포함합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "모델 서빙 생성 성공", content = @Content(schema = @Schema(implementation = CreateServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "서빙 이름 중복")
    })
    CreateServingResponse createServing(
            @RequestBody @Parameter(description = "모델 서빙 생성 요청 정보", required = true) ServingCreate request);

    /**
     * Backend.AI 연동 기반 모델 서빙 생성
     * 
     * <p>
     * Backend.AI 시스템과 연동하여 새로운 모델 서빙 인스턴스를 생성합니다.
     * Backend.AI의 런타임과 이미지를 사용하여 모델을 서빙합니다.
     * </p>
     * 
     * @param request Backend.AI 모델 서빙 생성 요청 정보
     * @return 생성된 Backend.AI 모델 서빙 정보
     */
    @PostMapping("/api/v1/backend-ai/servings")
    @Operation(summary = "Backend.AI 연동 기반 모델 서빙 생성", description = "Backend.AI 시스템과 연동하여 새로운 모델 서빙 인스턴스를 생성합니다. Backend.AI의 런타임과 이미지를 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Backend.AI 모델 서빙 생성 성공", content = @Content(schema = @Schema(implementation = BackendAiServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "서빙 이름 중복")
    })
    BackendAiServingResponse createBackendAiServing(
            @RequestBody @Parameter(description = "Backend.AI 모델 서빙 생성 요청 정보", required = true) BackendAiServingCreate request);

    /**
     * 모델 서빙 목록 조회
     * 
     * <p>
     * 사용자가 접근 가능한 모든 모델 서빙의 목록을 조회합니다.
     * 페이징, 정렬, 필터링을 지원합니다.
     * </p>
     * 
     * @param page   페이지 번호 (1부터 시작)
     * @param size   페이지 크기
     * @param sort   정렬 옵션 (예: "created_at", "name", "status")
     * @param filter 필터 조건 (예: "status:running", "model_id:gpt-4")
     * @param search 검색어 (서빙 이름 또는 설명에서 검색)
     * @return 모델 서빙 목록 (페이징된 결과)
     */
    @GetMapping("/api/v1/servings")
    @Operation(summary = "모델 서빙 목록 조회", description = "모든 모델 서빙 목록을 페이징된 형태로 조회합니다. 정렬, 필터링, 검색 기능을 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 목록 조회 성공", content = @Content(schema = @Schema(implementation = ServingsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ServingsResponse getServings(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 옵션 (created_at, name, status 등)", example = "created_at") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건", example = "status:running") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색어 (이름 또는 설명)", example = "gpt4") String search);

    /**
     * 특정 모델 서빙 상세 조회
     * 
     * <p>
     * 서빙 ID를 사용하여 특정 모델 서빙의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param servingId 조회할 서빙의 ID
     * @return 모델 서빙 상세 정보
     */
    @GetMapping("/api/v1/servings/{serving_id}")
    @Operation(summary = "모델 서빙 상세 조회", description = "서빙 ID를 사용하여 특정 모델 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 조회 성공", content = @Content(schema = @Schema(implementation = ServingResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    ServingResponse getServing(
            @PathVariable("serving_id") @Parameter(description = "조회할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);

    /**
     * 모델 서빙 정보 수정
     * 
     * <p>
     * 기존 모델 서빙의 설정을 수정합니다.
     * 리소스 할당, 오토스케일링 설정, 보안 필터 등을 업데이트할 수 있습니다.
     * </p>
     * 
     * @param servingId 수정할 서빙의 ID
     * @param request   모델 서빙 수정 요청 정보
     * @return 수정된 모델 서빙 정보
     */
    @PutMapping("/api/v1/servings/{serving_id}")
    @Operation(summary = "모델 서빙 정보 수정", description = "기존 모델 서빙의 설정을 수정합니다. 리소스 할당, 스케일링 설정 등을 업데이트할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 수정 성공", content = @Content(schema = @Schema(implementation = ServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    ServingUpdateResponse updateServing(
            @PathVariable("serving_id") @Parameter(description = "수정할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId,

            @RequestBody @Parameter(description = "모델 서빙 수정 요청 정보", required = true) ServingUpdate request);

    /**
     * 모델 서빙 삭제
     * 
     * <p>
     * 지정된 모델 서빙을 삭제합니다.
     * 실행 중인 모든 인스턴스가 중지되고 관련 리소스가 해제됩니다.
     * </p>
     * 
     * @param servingId 삭제할 서빙의 ID
     */
    @DeleteMapping("/api/v1/servings/{serving_id}")
    @Operation(summary = "모델 서빙 삭제", description = "지정된 모델 서빙을 삭제합니다. 모든 인스턴스가 중지되고 리소스가 해제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "모델 서빙 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "서빙이 사용 중임 (삭제 불가)")
    })
    void deleteServing(
            @PathVariable("serving_id") @Parameter(description = "삭제할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);

    /**
     * Backend.AI 모델 서빙 정보 수정
     * 
     * <p>
     * Backend.AI 기반 모델 서빙의 설정을 수정합니다.
     * 리소스 할당, 오토스케일링 설정, 보안 필터 등을 업데이트할 수 있습니다.
     * </p>
     * 
     * @param servingId 수정할 서빙의 ID
     * @param request   모델 서빙 수정 요청 정보
     * @return 수정된 모델 서빙 정보
     */
    @PutMapping("/api/v1/backend-ai/servings/{serving_id}")
    @Operation(summary = "Backend.AI 모델 서빙 정보 수정", description = "Backend.AI 기반 모델 서빙의 설정을 수정합니다. 리소스 할당, 스케일링 설정 등을 업데이트할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Backend.AI 모델 서빙 수정 성공", content = @Content(schema = @Schema(implementation = ServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    ServingUpdateResponse updateBackendAiServing(
            @PathVariable("serving_id") @Parameter(description = "수정할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId,

            @RequestBody @Parameter(description = "모델 서빙 수정 요청 정보", required = true) ServingUpdate request);

    /**
     * Backend.AI 모델 서빙 삭제
     * 
     * <p>
     * 지정된 Backend.AI 모델 서빙을 삭제합니다.
     * 실행 중인 모든 인스턴스가 중지되고 관련 리소스가 해제됩니다.
     * </p>
     * 
     * @param servingId 삭제할 서빙의 ID
     */
    @DeleteMapping("/api/v1/backend-ai/servings/{serving_id}")
    @Operation(summary = "Backend.AI 모델 서빙 삭제", description = "지정된 Backend.AI 모델 서빙을 삭제합니다. 모든 인스턴스가 중지되고 리소스가 해제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Backend.AI 모델 서빙 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "서빙이 사용 중임 (삭제 불가)")
    })
    void deleteBackendAiServing(
            @PathVariable("serving_id") @Parameter(description = "삭제할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);

    /**
     * 모델 서빙 스케일링
     * 
     * <p>
     * 모델 서빙의 레플리카 수를 조정합니다.
     * 트래픽 변화에 따라 인스턴스 수를 수동으로 조정할 때 사용합니다.
     * </p>
     * 
     * @param servingId 스케일링할 서빙의 ID
     * @param request   스케일링 요청 정보
     * @return 스케일링된 모델 서빙 정보
     */
    @PutMapping("/api/v1/servings/{serving_id}/autoscale")
    @Operation(summary = "모델 서빙 스케일링", description = "모델 서빙의 레플리카 수를 조정합니다. 트래픽 변화에 따른 수동 스케일링에 사용됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 스케일링 성공", content = @Content(schema = @Schema(implementation = ServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 스케일링 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    ServingResponse scaleServing(
            @PathVariable("serving_id") @Parameter(description = "스케일링할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId,

            @RequestBody @Parameter(description = "스케일링 요청 정보", required = true) ServingScale request);

    /**
     * 모델 서빙 시작
     * 
     * <p>
     * 중지된 모델 서빙을 시작합니다.
     * </p>
     * 
     * @param servingId 시작할 서빙의 ID
     * @return 시작 결과
     */
    @PostMapping("/api/v1/servings/{serving_id}/start")
    @Operation(summary = "모델 서빙 시작", description = "중지된 모델 서빙을 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "모델 서빙 시작 요청 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    SktaiOperationResponse startServing(
            @PathVariable("serving_id") @Parameter(description = "시작할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);

    /**
     * Backend.AI 모델 서빙 시작
     * 
     * <p>
     * 중지된 Backend.AI 모델 서빙을 시작합니다.
     * </p>
     * 
     * @param servingId 시작할 서빙의 ID
     * @return 시작 결과
     */
    @PostMapping("/api/v1/backend-ai/servings/{serving_id}/start")
    @Operation(summary = "Backend.AI 모델 서빙 시작", description = "중지된 Backend.AI 모델 서빙을 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Backend.AI 모델 서빙 시작 요청 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })  
    SktaiOperationResponse startBackendAiServing(
            @PathVariable("serving_id") @Parameter(description = "시작할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);
    /**
     * 모델 서빙 중지
     * 
     * <p>
     * 실행 중인 모델 서빙을 중지합니다.
     * </p>
     * 
     * @param servingId 중지할 서빙의 ID
     * @return 중지 결과
     */
    @PostMapping("/api/v1/servings/{serving_id}/stop")
    @Operation(summary = "모델 서빙 중지", description = "실행 중인 모델 서빙을 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 서빙 중지 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    SktaiOperationResponse stopServing(
            @PathVariable("serving_id") @Parameter(description = "중지할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);


    /**
     * Backend.AI 모델 서빙 중지
     * 
     * <p>
     * 실행 중인 Backend.AI 모델 서빙을 중지합니다.
     * </p>
     * 
     * @param servingId 중지할 서빙의 ID
     * @return 중지 결과
     */
    @PostMapping("/api/v1/backend-ai/servings/{serving_id}/stop")
    @Operation(summary = "Backend.AI 모델 서빙 중지", description = "실행 중인 Backend.AI 모델 서빙을 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Backend.AI 모델 서빙 중지 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    SktaiOperationResponse stopBackendAiServing(
            @PathVariable("serving_id") @Parameter(description = "중지할 서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId);
    /**
     * 모델 서빙의 사용 가능한 API 키 조회
     * 
     * <p>
     * 특정 모델 서빙에서 사용 가능한 Gateway API 키 목록을 조회합니다.
     * </p>
     * 
     * @param servingId 서빙 ID
     * @param page      페이지 번호 (1부터 시작)
     * @param size      페이지 크기
     * @param sort      정렬 옵션
     * @param filter    필터 조건
     * @param search    검색어
     * @return 사용 가능한 API 키 목록
     */
    @GetMapping("/api/v1/servings/{serving_id}/apikeys")
    @Operation(summary = "모델 서빙 API 키 목록 조회", description = "지정된 모델 서빙에서 사용 가능한 Gateway API 키 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공", content = @Content(schema = @Schema(implementation = ApiKeysResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    ApiKeysResponse getServingApiKeys(
            @PathVariable("serving_id") @Parameter(description = "서빙 ID", required = true, example = "srv-123e4567-e89b-12d3-a456-426614174000") String servingId,

            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색 조건") String search);

    /**
     * 모든 서빙 하드 삭제
     * 
     * <p>
     * 모든 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).
     * </p>
     * 
     * @return 하드 삭제 결과
     */
    @PostMapping("/api/v1/servings/hard-delete")
    @Operation(summary = "모든 서빙 하드 삭제", description = "모든 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    })
    void hardDeleteServings();

    // ==================== Agent Serving Management ====================

    /**
     * 새로운 에이전트 서빙 생성
     * 
     * <p>
     * 지정된 에이전트를 사용하여 새로운 에이전트 서빙 인스턴스를 생성합니다.
     * 대화형 AI 서비스를 위한 전용 서빙입니다.
     * </p>
     * 
     * @param request 에이전트 서빙 생성 요청 정보
     * @return 생성된 에이전트 서빙 정보
     */
    @PostMapping("/api/v1/agent_servings")
    @Operation(summary = "에이전트 서빙 생성", description = "새로운 에이전트 서빙 인스턴스를 생성합니다. 대화형 AI 서비스를 위한 전용 서빙입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "에이전트 서빙 생성 성공", content = @Content(schema = @Schema(implementation = AgentServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "에이전트 서빙 이름 중복")
    })
    AgentServingResponse createAgentServing(
            @RequestBody @Parameter(description = "에이전트 서빙 생성 요청 정보", required = true) AgentServingCreate request);

    /**
     * 에이전트 서빙 목록 조회
     * 
     * <p>
     * 사용자가 접근 가능한 모든 에이전트 서빙의 목록을 조회합니다.
     * </p>
     * 
     * @param page   페이지 번호 (1부터 시작)
     * @param size   페이지 크기
     * @param sort   정렬 옵션
     * @param filter 필터 조건
     * @param search 검색어
     * @return 에이전트 서빙 목록 (페이징된 결과)
     */
    @GetMapping("/api/v1/agent_servings")
    @Operation(summary = "에이전트 서빙 목록 조회", description = "모든 에이전트 서빙 목록을 페이징된 형태로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 목록 조회 성공", content = @Content(schema = @Schema(implementation = AgentServingsResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    AgentServingsResponse getAgentServings(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 옵션", example = "created_at") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건", example = "status:running") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색어", example = "conversational") String search);

    /**
     * 특정 에이전트 서빙 상세 조회
     * 
     * <p>
     * 에이전트 서빙 ID를 사용하여 특정 에이전트 서빙의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param agentServingId 조회할 에이전트 서빙의 ID
     * @return 에이전트 서빙 상세 정보
     */
    @GetMapping("/api/v1/agent_servings/{agent_serving_id}")
    @Operation(summary = "에이전트 서빙 상세 조회", description = "에이전트 서빙 ID를 사용하여 특정 에이전트 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 조회 성공", content = @Content(schema = @Schema(implementation = AgentServingResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    AgentServingResponse getAgentServing(
            @PathVariable("agent_serving_id") @Parameter(description = "조회할 에이전트 서빙 ID", required = true, example = "agt-srv-123e4567-e89b-12d3-a456-426614174000") String agentServingId);

    /**
     * 에이전트 서빙 정보 수정
     * 
     * <p>
     * 기존 에이전트 서빙의 설정을 수정합니다.
     * </p>
     * 
     * @param agentServingId 수정할 에이전트 서빙의 ID
     * @param request        에이전트 서빙 수정 요청 정보
     * @return 수정된 에이전트 서빙 정보
     */
    @PutMapping("/api/v1/agent_servings/{agent_serving_id}")
    @Operation(summary = "에이전트 서빙 정보 수정", description = "기존 에이전트 서빙의 설정을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 수정 성공", content = @Content(schema = @Schema(implementation = AgentServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    AgentServingResponse updateAgentServing(
            @PathVariable("agent_serving_id") @Parameter(description = "수정할 에이전트 서빙 ID", required = true, example = "agt-srv-123e4567-e89b-12d3-a456-426614174000") String agentServingId,

            @RequestBody @Parameter(description = "에이전트 서빙 수정 요청 정보", required = true) AgentServingUpdate request);

    /**
     * 에이전트 서빙 삭제
     * 
     * <p>
     * 지정된 에이전트 서빙을 삭제합니다.
     * 모든 활성 세션이 종료되고 관련 리소스가 해제됩니다.
     * </p>
     * 
     * @param agentServingId 삭제할 에이전트 서빙의 ID
     */
    @DeleteMapping("/api/v1/agent_servings/{agent_serving_id}")
    @Operation(summary = "에이전트 서빙 삭제", description = "지정된 에이전트 서빙을 삭제합니다. 모든 활성 세션이 종료됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "에이전트 서빙 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "활성 세션이 존재함 (삭제 불가)")
    })
    void deleteAgentServing(
            @PathVariable("agent_serving_id") @Parameter(description = "삭제할 에이전트 서빙 ID", required = true, example = "agt-srv-123e4567-e89b-12d3-a456-426614174000") String agentServingId);

    /**
     * 에이전트 서빙 스케일링
     * 
     * <p>
     * 에이전트 서빙의 레플리카 수를 조정합니다.
     * </p>
     * 
     * @param agentServingId 스케일링할 에이전트 서빙의 ID
     * @param request        스케일링 요청 정보
     * @return 스케일링된 에이전트 서빙 정보
     */
    @PutMapping("/api/v1/agent_servings/{agent_serving_id}/autoscale")
    @Operation(summary = "에이전트 서빙 스케일링", description = "에이전트 서빙의 레플리카 수를 조정합니다. 기존 세션은 유지됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 스케일링 성공", content = @Content(schema = @Schema(implementation = AgentServingResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 스케일링 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    AgentServingResponse scaleAgentServing(
            @PathVariable("agent_serving_id") @Parameter(description = "스케일링할 에이전트 서빙 ID", required = true, example = "agt-srv-123e4567-e89b-12d3-a456-426614174000") String agentServingId,

            @RequestBody @Parameter(description = "스케일링 요청 정보", required = true) AgentServingScale request);

    /**
     * 에이전트 서빙 시작
     * 
     * <p>
     * 중지된 에이전트 서빙을 시작합니다.
     * </p>
     * 
     * @param agentServingId 시작할 에이전트 서빙의 ID
     * @return 시작 결과
     */
    @PostMapping("/api/v1/agent_servings/{agent_serving_id}/start")
    @Operation(summary = "에이전트 서빙 시작", description = "중지된 에이전트 서빙을 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "에이전트 서빙 시작 요청 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    SktaiOperationResponse startAgentServing(
            @PathVariable("agent_serving_id") @Parameter(description = "시작할 에이전트 서빙 ID", required = true, example = "agt-srv-123e4567-e89b-12d3-a456-426614174000") String agentServingId);

    /**
     * 에이전트 서빙 중지
     * 
     * <p>
     * 실행 중인 에이전트 서빙을 중지합니다.
     * </p>
     * 
     * @param agentServingId 중지할 에이전트 서빙의 ID
     * @return 중지 결과
     */
    @PostMapping("/api/v1/agent_servings/{agent_serving_id}/stop")
    @Operation(summary = "에이전트 서빙 중지", description = "실행 중인 에이전트 서빙을 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에이전트 서빙 중지 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 서빙을 찾을 수 없음")
    })
    SktaiOperationResponse stopAgentServing(
            @PathVariable("agent_serving_id") @Parameter(description = "중지할 에이전트 서빙 ID", required = true, example = "agt-srv-123e4567-e89b-12d3-a456-426614174000") String agentServingId);

    /**
     * 에이전트 앱의 사용 가능한 API 키 조회
     * 
     * <p>
     * 특정 에이전트 앱에서 사용 가능한 Gateway API 키 목록을 조회합니다.
     * </p>
     * 
     * @param agentAppId 에이전트 앱 ID
     * @param page       페이지 번호 (1부터 시작)
     * @param size       페이지 크기
     * @param sort       정렬 옵션
     * @param filter     필터 조건
     * @param search     검색어
     * @return 사용 가능한 API 키 목록
     */
    @GetMapping("/api/v1/agent_servings/{agent_app_id}/apikeys")
    @Operation(summary = "에이전트 앱 API 키 목록 조회", description = "지정된 에이전트 앱에서 사용 가능한 Gateway API 키 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공", content = @Content(schema = @Schema(implementation = ApiKeysResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "에이전트 앱을 찾을 수 없음")
    })
    ApiKeysResponse getAgentAppApiKeys(
            @PathVariable("agent_app_id") @Parameter(description = "에이전트 앱 ID", required = true) String agentAppId,

            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색 조건") String search);

    /**
     * 모든 에이전트 서빙 하드 삭제
     * 
     * <p>
     * 모든 에이전트 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).
     * </p>
     * 
     * @return 하드 삭제 결과
     */
    @PostMapping("/api/v1/agent_servings/hard-delete")
    @Operation(summary = "모든 에이전트 서빙 하드 삭제", description = "모든 에이전트 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    })
    void hardDeleteAgentServings();

    // ==================== API Key Management ====================

    /**
     * 새로운 API 키 생성
     * 
     * <p>
     * 서빙 엔드포인트에 접근하기 위한 새로운 API 키를 생성합니다.
     * 특정 서빙에 대한 접근 권한과 사용량 제한을 설정할 수 있습니다.
     * </p>
     * 
     * @param request API 키 생성 요청 정보
     * @return 생성된 API 키 정보 (키 값 포함)
     */
    @PostMapping("/api/v1/apikeys")
    @Operation(summary = "API 키 생성", description = "서빙 엔드포인트 접근을 위한 새로운 API 키를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "API 키 생성 성공", content = @Content(schema = @Schema(implementation = ApiKeyResponse.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ApiKeyResponse createApiKey(
            @RequestBody @Parameter(description = "API 키 생성 요청 정보", required = true) ApiKeyCreate request);

    /**
     * API 키 목록 조회
     * 
     * <p>
     * 사용자가 생성한 모든 API 키의 목록을 조회합니다.
     * 보안상 실제 키 값은 마스킹되어 표시됩니다.
     * </p>
     * 
     * @param page   페이지 번호 (1부터 시작)
     * @param size   페이지 크기
     * @param sort   정렬 옵션
     * @param filter 필터 조건
     * @param search 검색어
     * @return API 키 목록 (페이징된 결과)
     */
    @GetMapping("/api/v1/apikeys")
    @Operation(summary = "API 키 목록 조회", description = "모든 API 키 목록을 페이징된 형태로 조회합니다. 키 값은 마스킹되어 표시됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공", content = @Content(schema = @Schema(implementation = ApiKeysResponse.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ApiKeysResponse getApiKeys(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 옵션", example = "created_at") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건", example = "status:active") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색어", example = "production") String search);

    /**
     * 특정 API 키 상세 조회
     * 
     * <p>
     * API 키 ID를 사용하여 특정 API 키의 상세 정보를 조회합니다.
     * 키 값은 보안상 마스킹되어 표시됩니다.
     * </p>
     * 
     * @param apiKeyId 조회할 API 키의 ID
     * @return API 키 상세 정보 (키 값 마스킹됨)
     */
    @GetMapping("/api/v1/apikeys/{api_key_id}")
    @Operation(summary = "API 키 상세 조회", description = "API 키 ID를 사용하여 특정 API 키의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 조회 성공", content = @Content(schema = @Schema(implementation = ApiKeyResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "API 키를 찾을 수 없음")
    })
    ApiKeyResponse getApiKey(
            @PathVariable("api_key_id") @Parameter(description = "조회할 API 키 ID", required = true, example = "key-123e4567-e89b-12d3-a456-426614174000") String apiKeyId);

    /**
     * API 키 업데이트
     * 
     * <p>
     * 기존 API 키의 설정을 업데이트합니다.
     * </p>
     * 
     * @param apiKeyId 업데이트할 API 키의 ID
     * @param request  API 키 업데이트 요청 정보
     * @return 업데이트된 API 키 정보
     */
    @PutMapping("/api/v1/apikeys/{api_key_id}")
    @Operation(summary = "API 키 업데이트", description = "기존 API 키의 설정을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 업데이트 성공", content = @Content(schema = @Schema(implementation = ApiKeyResponse.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ApiKeyResponse updateApiKey(
            @PathVariable("api_key_id") @Parameter(description = "업데이트할 API 키 ID", required = true, example = "key-123e4567-e89b-12d3-a456-426614174000") String apiKeyId,

            @RequestBody @Parameter(description = "API 키 업데이트 요청 정보", required = true) ApiKeyUpdate request);

    /**
     * API 키 삭제
     * 
     * <p>
     * 지정된 API 키를 삭제합니다.
     * 삭제된 키는 즉시 사용할 수 없게 됩니다.
     * </p>
     * 
     * @param apiKeyId 삭제할 API 키의 ID
     */
    @DeleteMapping("/api/v1/apikeys/{api_key_id}")
    @Operation(summary = "API 키 삭제", description = "지정된 API 키를 삭제합니다. 삭제된 키는 즉시 사용할 수 없게 됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "API 키 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "API 키를 찾을 수 없음")
    })
    void deleteApiKey(
            @PathVariable("api_key_id") @Parameter(description = "삭제할 API 키 ID", required = true, example = "key-123e4567-e89b-12d3-a456-426614174000") String apiKeyId);

    /**
     * API 키 검증
     * 
     * <p>
     * API 키의 유효성을 검증합니다.
     * </p>
     * 
     * @param request API 키 검증 요청 정보
     * @return 검증 결과
     */
    @PostMapping("/api/v1/apikeys/verify")
    @Operation(summary = "API 키 검증", description = "API 키의 유효성을 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공"),
            @ApiResponse(responseCode = "401", description = "검증 실패"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ApiKeyVerifyResponse verifyApiKey(
            @RequestBody @Parameter(description = "API 키 검증 요청 정보", required = true) ApiKeyVerify request);

    // ==================== MCP Serving Management ====================

    /**
     * 새로운 MCP 서빙 생성
     * 
     * @param request MCP 서빙 생성 요청 정보
     * @return 생성된 MCP 서빙 정보
     */
    @PostMapping("/api/v1/mcp_servings")
    @Operation(summary = "MCP 서빙 생성", description = "새로운 MCP (Model Context Protocol) 서빙을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "MCP 서빙 생성 성공", content = @Content(schema = @Schema(implementation = McpServingResponse.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    McpServingResponse createMcpServing(
            @RequestBody @Parameter(description = "MCP 서빙 생성 요청 정보", required = true) McpServingCreate request);

    /**
     * MCP 서빙 목록 조회
     * 
     * @param page   페이지 번호 (기본값: 1)
     * @param size   페이지 크기 (기본값: 10)
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색 조건
     * @return MCP 서빙 목록
     */
    @GetMapping("/api/v1/mcp_servings")
    @Operation(summary = "MCP 서빙 목록 조회", description = "페이징된 MCP 서빙 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 목록 조회 성공", content = @Content(schema = @Schema(implementation = McpServingsResponse.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    McpServingsResponse getMcpServings(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색 조건") String search);

    /**
     * MCP 서빙 상세 조회
     * 
     * @param mcpServingId MCP 서빙 ID
     * @return MCP 서빙 상세 정보
     */
    @GetMapping("/api/v1/mcp_servings/{mcp_serving_id}")
    @Operation(summary = "MCP 서빙 상세 조회", description = "MCP 서빙 ID를 사용하여 특정 MCP 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 조회 성공", content = @Content(schema = @Schema(implementation = McpServingInfo.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    McpServingInfo getMcpServing(
            @PathVariable("mcp_serving_id") @Parameter(description = "조회할 MCP 서빙 ID", required = true) String mcpServingId);

    /**
     * MCP 서빙 업데이트
     * 
     * @param mcpServingId MCP 서빙 ID
     * @param request      MCP 서빙 업데이트 요청 정보
     * @return 업데이트 결과
     */
    @PutMapping("/api/v1/mcp_servings/{mcp_serving_id}")
    @Operation(summary = "MCP 서빙 업데이트", description = "기존 MCP 서빙의 설정을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 업데이트 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SktaiOperationResponse updateMcpServing(
            @PathVariable("mcp_serving_id") @Parameter(description = "업데이트할 MCP 서빙 ID", required = true) String mcpServingId,

            @RequestBody @Parameter(description = "MCP 서빙 업데이트 요청 정보", required = true) McpServingUpdate request);

    /**
     * MCP 서빙 삭제
     * 
     * @param mcpServingId 삭제할 MCP 서빙 ID
     */
    @DeleteMapping("/api/v1/mcp_servings/{mcp_serving_id}")
    @Operation(summary = "MCP 서빙 삭제", description = "지정된 MCP 서빙을 삭제합니다 (삭제 마킹).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "MCP 서빙 삭제 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    void deleteMcpServing(
            @PathVariable("mcp_serving_id") @Parameter(description = "삭제할 MCP 서빙 ID", required = true) String mcpServingId);

    /**
     * MCP 서빙 시작
     * 
     * @param mcpServingId 시작할 MCP 서빙 ID
     * @return 시작 결과
     */
    @PostMapping("/api/v1/mcp_servings/{mcp_serving_id}/start")
    @Operation(summary = "MCP 서빙 시작", description = "지정된 MCP 서빙을 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "MCP 서빙 시작 요청 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SktaiOperationResponse startMcpServing(
            @PathVariable("mcp_serving_id") @Parameter(description = "시작할 MCP 서빙 ID", required = true) String mcpServingId);

    /**
     * MCP 서빙 중지
     * 
     * @param mcpServingId 중지할 MCP 서빙 ID
     * @return 중지 결과
     */
    @PostMapping("/api/v1/mcp_servings/{mcp_serving_id}/stop")
    @Operation(summary = "MCP 서빙 중지", description = "지정된 MCP 서빙을 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "MCP 서빙 중지 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SktaiOperationResponse stopMcpServing(
            @PathVariable("mcp_serving_id") @Parameter(description = "중지할 MCP 서빙 ID", required = true) String mcpServingId);

    /**
     * MCP 서빙 하드 삭제
     * 
     * @return 하드 삭제 결과
     */
    @PostMapping("/api/v1/mcp_servings/hard-delete")
    @Operation(summary = "MCP 서빙 하드 삭제", description = "모든 MCP 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "하드 삭제 성공")
    })
    void hardDeleteMcpServings();

    /**
     * MCP API 키 목록 조회
     * 
     * @param mcpId  MCP ID
     * @param page   페이지 번호 (기본값: 1)
     * @param size   페이지 크기 (기본값: 10)
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색 조건
     * @return 사용 가능한 API 키 목록
     */
    @GetMapping("/api/v1/mcp_servings/{mcp_id}/apikeys")
    @Operation(summary = "MCP API 키 목록 조회", description = "지정된 MCP에서 사용 가능한 Gateway API 키 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "API 키 목록 조회 성공", content = @Content(schema = @Schema(implementation = ApiKeysResponse.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ApiKeysResponse getMcpApiKeys(
            @PathVariable("mcp_id") @Parameter(description = "MCP ID", required = true) String mcpId,

            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색 조건") String search);

    // ==================== Shared Backend Management ====================

    /**
     * Shared Backend 목록 조회
     * 
     * @return Shared Backend 목록
     */
    @GetMapping("/api/v1/shared_backends")
    @Operation(summary = "Shared Backend 목록 조회", description = "모든 Shared Backend 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared Backend 목록 조회 성공", content = @Content(schema = @Schema(implementation = SharedAgentBackendsResponse.class)))
    })
    SharedAgentBackendsResponse getSharedBackends();

    /**
     * Shared Backend 프로젝트 조회
     * 
     * @param projectId 프로젝트 ID
     * @return Shared Backend 정보
     */
    @GetMapping("/api/v1/shared_backends/{project_id}")
    @Operation(summary = "Shared Backend 조회", description = "프로젝트 ID로 Shared Backend 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared Backend 조회 성공", content = @Content(schema = @Schema(implementation = SharedAgentBackendRead.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SharedAgentBackendRead getSharedBackend(
            @PathVariable("project_id") @Parameter(description = "프로젝트 ID", required = true, example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId);

    /**
     * Shared Backend 생성
     * 
     * @param projectId 프로젝트 ID
     * @param request   Shared Backend 생성 요청 정보
     * @return 생성된 Shared Backend 정보
     */
    @PostMapping("/api/v1/shared_backends/{project_id}")
    @Operation(summary = "Shared Backend 생성", description = "새로운 Shared Backend를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Shared Backend 생성 성공", content = @Content(schema = @Schema(implementation = SharedAgentBackendRead.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SharedAgentBackendRead createSharedBackend(
            @PathVariable("project_id") @Parameter(description = "프로젝트 ID", required = true, example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId,

            @RequestBody @Parameter(description = "Shared Backend 생성 요청 정보", required = true) SharedBackendCreate request);

    /**
     * Shared Backend 업데이트
     * 
     * @param projectId 프로젝트 ID
     * @param request   Shared Backend 업데이트 요청 정보
     * @return 업데이트된 Shared Backend 정보
     */
    @PutMapping("/api/v1/shared_backends/{project_id}")
    @Operation(summary = "Shared Backend 업데이트", description = "기존 Shared Backend를 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shared Backend 업데이트 성공", content = @Content(schema = @Schema(implementation = SharedAgentBackendRead.class))),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SharedAgentBackendRead updateSharedBackend(
            @PathVariable("project_id") @Parameter(description = "프로젝트 ID", required = true, example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId,

            @RequestBody @Parameter(description = "Shared Backend 업데이트 요청 정보", required = true) SharedAgentBackendUpdate request);

    /**
     * Shared Backend 시작
     * 
     * @param projectId 프로젝트 ID
     * @return 시작 결과
     */
    @PostMapping("/api/v1/shared_backends/{project_id}/start")
    @Operation(summary = "Shared Backend 시작", description = "지정된 Shared Backend를 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Shared Backend 시작 요청 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SktaiOperationResponse startSharedBackend(
            @PathVariable("project_id") @Parameter(description = "프로젝트 ID", required = true, example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId);

    /**
     * Shared Backend 중지
     * 
     * @param projectId 프로젝트 ID
     * @return 중지 결과
     */
    @PostMapping("/api/v1/shared_backends/{project_id}/stop")
    @Operation(summary = "Shared Backend 중지", description = "지정된 Shared Backend를 중지합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Shared Backend 중지 요청 성공"),
            @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    SktaiOperationResponse stopSharedBackend(
            @PathVariable("project_id") @Parameter(description = "프로젝트 ID", required = true, example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5") String projectId);

    /**
     * Shared Backend 버전 업그레이드
     * 
     * @return 버전 업그레이드 결과
     */
    @PostMapping("/api/v1/shared_backends/version-up")
    @Operation(summary = "Shared Backend 버전 업그레이드", description = "시작된 모든 Shared Backend의 버전을 업그레이드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "버전 업그레이드 요청 성공")
    })
    SktaiOperationResponse versionUpSharedBackends();

    // ==============================================
    // 추가된 OpenAPI 스펙 엔드포인트들
    // ==============================================

    /**
     * Serving 모델 뷰 조회
     * 
     * @param servingId 서빙 ID
     * @return 서빙 모델 뷰 정보
     */
    @GetMapping("/api/v1/servings/{serving_id}/view")
    @Operation(summary = "Serving 모델 뷰 조회", description = "지정된 서빙의 모델 뷰 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모델 뷰 조회 성공", content = @Content(schema = @Schema(implementation = ServingModelView.class))),
            @ApiResponse(responseCode = "404", description = "서빙을 찾을 수 없음")
    })
    ServingModelView getServingModelView(
            @Parameter(description = "조회할 서빙 ID", required = true, example = "serving-123") @PathVariable("serving_id") String servingId);

    /**
     * Agent Serving 정보 조회 (상세)
     * 
     * @param agentServingId Agent 서빙 ID
     * @return Agent 서빙 상세 정보
     */
    @GetMapping("/api/v1/agent_servings/{agent_serving_id}/info")
    @Operation(summary = "Agent Serving 상세 정보 조회", description = "지정된 Agent 서빙의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agent 서빙 정보 조회 성공", content = @Content(schema = @Schema(implementation = AgentServingInfo.class))),
            @ApiResponse(responseCode = "404", description = "Agent 서빙을 찾을 수 없음")
    })
    AgentServingInfo getAgentServingInfo(
            @Parameter(description = "조회할 Agent 서빙 ID", required = true, example = "agent-123") @PathVariable("agent_serving_id") String agentServingId);

    /**
     * 정책 목록 조회
     * 
     * @param page   페이지 번호 (기본: 1)
     * @param size   페이지 크기 (기본: 20)
     * @param sort   정렬 옵션
     * @param search 검색어
     * @return 정책 목록
     */
    @GetMapping("/api/v1/policies")
    @Operation(summary = "정책 목록 조회", description = "서빙 접근 정책 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 목록 조회 성공", content = @Content(schema = @Schema(implementation = PolicyPayload.class)))
    })
    PolicyPayload getPolicies(
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(value = "size", defaultValue = "20") Integer size,
            @Parameter(description = "정렬 옵션", example = "created_at:desc") @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "검색어", example = "policy") @RequestParam(value = "search", required = false) String search);
}
