package com.skax.aiplatform.client.lablup.api;

import com.skax.aiplatform.client.lablup.api.dto.request.*;
import com.skax.aiplatform.client.lablup.api.dto.response.*;
import com.skax.aiplatform.client.lablup.common.dto.LablupResponse;
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
 * Lablup 아티팩트 관리 API Feign Client
 * 
 * <p>Lablup 시스템의 아티팩트 관리를 위한 REST API 클라이언트입니다.
 * 첨부된 Reservoir Sync Manual 문서를 기반으로 정확한 API 엔드포인트를 구현합니다.</p>
 * 
 * <h3>지원 API:</h3>
 * <ul>
 *   <li><strong>Scan Artifact</strong>: /v1/artifact-registries/scan</li>
 *   <li><strong>Scan Single Artifact Model</strong>: /v1/artifact-registries/model/{model_id}</li>
 *   <li><strong>Batch Scan Artifact Models</strong>: /artifact-registries/models/batch</li>
 *   <li><strong>Search Artifacts</strong>: /artifact-registries/search</li>
 *   <li><strong>Import Artifacts</strong>: /artifacts/import</li>
 *   <li><strong>Cleanup Artifacts</strong>: /artifacts/revisions/cleanup</li>
 *   <li><strong>Cancel Import Artifact</strong>: /artifacts/task/cancel</li>
 *   <li><strong>Update Artifact</strong>: /artifacts/{artifact_id}</li>
 *   <li><strong>Get Artifact Revision Readme</strong>: /artifacts/revisions/{artifact_revision_id}/readme</li>
 *   <li><strong>Get Presigned Download URL</strong>: /object-storages/presigned/download</li>
 *   <li><strong>Get Presigned Upload URL</strong>: /object-storages/presigned/upload</li>
 * </ul>
 * 
 * @author 김예리
 * @since 2025-10-19
 * @version 1.0
 */
@FeignClient(
    name = "lablup-auth-client",
    url = "${lablup.api.base-url}",
    configuration = LablupClientConfig.class
)
@Tag(name = "Lablup Artifact API", description = "Lablup 아티팩트 관리 API")
public interface LablupAuthClient {
    
    /**
     * 인증 헤더 생성
     * 
     * <p>인증 헤더를 생성합니다.</p>
     * 
     * @param request 인증 요청 정보
     * @return 인증 결과
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "인증 헤더 생성", 
        description = "인증 헤더를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스캔 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    LablupResponse<ScanArtifactResponse> scanArtifact(
        @RequestBody ScanArtifactRequest request
    );
    
    /**
     * 단일 아티팩트 모델 스캔
     * 
     * <p>개별 아티팩트 모델에 대한 스캔을 수행합니다.</p>
     * 
     * @param modelId 모델 ID
     * @param request 모델 스캔 요청 정보
     * @return 모델 스캔 결과
     */
    @PostMapping(value = "/v1/artifact-registries/model/{model_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "단일 아티팩트 모델 스캔", 
        description = "개별 아티팩트 모델에 대한 스캔을 수행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스캔 요청 성공"),
        @ApiResponse(responseCode = "404", description = "모델을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    LablupResponse<ScanSingleArtifactModelResponse> scanSingleArtifactModel(
        @Parameter(description = "모델 ID", required = true) 
        @PathVariable("model_id") String modelId,
        @RequestBody ScanSingleArtifactModelRequest request
    );
    
    
}