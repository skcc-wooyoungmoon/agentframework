package com.skax.aiplatform.client.sktai.model;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelProviderCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelProviderUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelProviderRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelProvidersRead;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Model Providers API FeignClient
 * 
 * <p>SKTAI Model 시스템의 Model Provider 관리를 위한 Feign 클라이언트입니다.
 * Model Provider는 모델을 제공하는 조직, 회사 또는 서비스를 나타냅니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Provider 등록</strong>: 새로운 모델 제공자 등록</li>
 *   <li><strong>Provider 조회</strong>: 등록된 모델 제공자 목록 및 상세 조회</li>
 *   <li><strong>Provider 수정</strong>: 기존 모델 제공자 정보 업데이트</li>
 *   <li><strong>Provider 삭제</strong>: 모델 제공자 삭제 (soft delete)</li>
 * </ul>
 * 
 * <h3>페이징 및 검색:</h3>
 * <ul>
 *   <li>페이지네이션을 통한 대용량 데이터 처리</li>
 *   <li>정렬, 필터링, 검색 기능 지원</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-model-providers-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Model Providers", description = "SKTAI Model Provider 관리 API")
public interface SktaiModelProvidersClient {

    /**
     * Model Provider 등록
     * 
     * <p>새로운 모델 제공자를 등록합니다.
     * 모델 제공자는 모델을 제공하는 조직, 회사 또는 서비스를 나타냅니다.</p>
     * 
     * @param request Provider 생성 요청 정보
     * @return 생성된 Provider 정보
     */
    @PostMapping("/api/v1/models/providers")
    @Operation(
        summary = "Model Provider 등록",
        description = "새로운 모델 제공자를 등록합니다. Provider는 모델을 제공하는 조직이나 서비스를 나타냅니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Provider 등록 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelProviderRead registerModelProvider(@RequestBody ModelProviderCreate request);

    /**
     * Model Provider 목록 조회
     * 
     * <p>등록된 모든 모델 제공자 목록을 페이징하여 조회합니다.
     * 정렬, 필터링, 검색 기능을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 Provider 목록
     */
    @GetMapping("/api/v1/models/providers")
    @Operation(
        summary = "Model Provider 목록 조회",
        description = "등록된 모든 모델 제공자 목록을 페이징하여 조회합니다. 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelProvidersRead readModelProviders(
            @Parameter(description = "페이지 번호", example = "1", required = false)
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            
            @Parameter(description = "페이지 크기", example = "10", required = false)
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            
            @Parameter(description = "정렬 기준 (예: name,asc)")
            @RequestParam(value = "sort", required = false) String sort,
            
            @Parameter(description = "필터 조건")
            @RequestParam(value = "filter", required = false) String filter,
            
            @Parameter(description = "검색어")
            @RequestParam(value = "search", required = false) String search
    );

    /**
     * Model Provider 목록 조회(미페이징)
     * 
     * <p>등록된 모든 모델 제공자 목록을 페이징하여 조회합니다.
     * 정렬, 필터링, 검색 기능을 지원합니다.</p>
     * 
     * @return 페이징된 Provider 목록
     */
    @GetMapping("/api/v1/models/providers")
    @Operation(
        summary = "Model Provider 목록 조회",
        description = "등록된 모든 모델 제공자 목록을 조회합니다. 정렬, 필터링, 검색 기능을 지원합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelProvidersRead readModelProvidersNoPaged();


    /**
     * Model Provider 상세 조회
     * 
     * <p>지정된 ID의 모델 제공자 상세 정보를 조회합니다.</p>
     * 
     * @param providerId Provider ID (UUID 형식)
     * @return Provider 상세 정보
     */
    @GetMapping("/api/v1/models/providers/{provider_id}")
    @Operation(
        summary = "Model Provider 상세 조회",
        description = "지정된 ID의 모델 제공자 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Provider를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelProviderRead readModelProvider(
            @Parameter(description = "Provider ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("provider_id") String providerId
    );

    /**
     * Model Provider 수정
     * 
     * <p>지정된 ID의 모델 제공자 정보를 수정합니다.
     * 이름, 설명, 로고 등의 정보를 업데이트할 수 있습니다.</p>
     * 
     * @param providerId Provider ID (UUID 형식)
     * @param request Provider 수정 요청 정보
     * @return 수정된 Provider 정보
     */
    @PutMapping("/api/v1/models/providers/{provider_id}")
    @Operation(
        summary = "Model Provider 수정",
        description = "지정된 ID의 모델 제공자 정보를 수정합니다. 이름, 설명, 로고 등을 업데이트할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Provider를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelProviderRead editModelProvider(
            @Parameter(description = "Provider ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("provider_id") String providerId,
            @RequestBody ModelProviderUpdate request
    );

    /**
     * Model Provider 삭제
     * 
     * <p>지정된 ID의 모델 제공자를 삭제합니다.
     * 실제로는 deleted 플래그를 설정하는 soft delete 방식입니다.</p>
     * 
     * @param providerId Provider ID (UUID 형식)
     */
    @DeleteMapping("/api/v1/models/providers/{provider_id}")
    @Operation(
        summary = "Model Provider 삭제",
        description = "지정된 ID의 모델 제공자를 삭제합니다. Soft delete 방식으로 deleted 플래그를 설정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Provider 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Provider를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    void removeModelProvider(
            @Parameter(description = "Provider ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable("provider_id") String providerId
    );
}
