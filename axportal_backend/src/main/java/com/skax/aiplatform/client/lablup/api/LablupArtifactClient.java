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
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@FeignClient(
    name = "lablup-artifact-client",
    url = "${lablup.api.base-url}",
    configuration = LablupClientConfig.class
)
@Tag(name = "Lablup Artifact API", description = "Lablup 아티팩트 관리 API")
public interface LablupArtifactClient {
    
    /**
     * 아티팩트 스캔
     * 
     * <p>아티팩트 레지스트리에서 아티팩트를 스캔합니다.</p>
     * 
     * @param request 스캔 요청 정보
     * @return 스캔 결과
     */
    @PostMapping(value = "/artifact-registries/delegation/scan", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 스캔", 
        description = "아티팩트 레지스트리에서 아티팩트를 스캔합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스캔 요청 성공"), 
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ScanArtifactResponse scanArtifact(
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
    
    /**
     * 배치 아티팩트 모델 스캔
     * 
     * <p>여러 아티팩트 모델을 배치 형태로 스캔합니다.</p>
     * 
     * @param request 배치 스캔 요청 정보
     * @return 배치 스캔 결과
     */
    @PostMapping(value = "/artifact-registries/models/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "배치 아티팩트 모델 스캔", 
        description = "여러 아티팩트 모델을 배치 형태로 스캔합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "스캔 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    LablupResponse<BatchScanArtifactModelsResponse> batchScanArtifactModels(
        @RequestBody BatchScanArtifactModelsRequest request
    );
    
    /**
     * 아티팩트 검색
     * 
     * <p>다양한 조건으로 아티팩트를 검색합니다.</p>
     * 
     * @param request 검색 요청 정보
     * @return 검색 결과
     */
    @PostMapping(value = "/artifact-registries/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 검색", 
        description = "다양한 조건으로 아티팩트를 검색합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 검색 조건"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    LablupResponse<SearchArtifactsResponse> searchArtifacts(
        @RequestBody SearchArtifactsRequest request
    );
    
    /**
     * 아티팩트 가져오기
     * 
     * <p>외부 저장소나 레지스트리에서 아티팩트를 가져옵니다.</p>
     * 
     * @param request 가져오기 요청 정보
     * @return 가져오기 작업 결과
     */
    @PostMapping(value = "/artifacts/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 가져오기", 
        description = "외부 저장소나 레지스트리에서 아티팩트를 가져옵니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "가져오기 요청 접수"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ImportArtifactsResponse importArtifacts(
        @RequestBody ImportArtifactsRequest request
    );

    /**
     * 아티팩트 위임 가져오기
     * 
     * <p>레저버 레지스트리를 통해 리모트 레지스트리에서 아티팩트를 위임 가져옵니다.</p>
     * 
     * @param request 위임 가져오기 요청 정보
     * @return 가져오기 작업 결과
     */
    @PostMapping(value = "/artifact-registries/delegation/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 위임 가져오기", 
        description = "레저버 레지스트리를 통해 리모트 레지스트리에서 아티팩트를 위임 가져옵니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "가져오기 요청 접수"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ImportArtifactsResponse importArtifactsDelegation(
        @RequestBody ImportArtifactsDelegationRequest request
    );
    
    
    /**
     * 아티팩트 정리
     * 
     * <p>사용하지 않는 아티팩트 리비전을 정리합니다.</p>
     * 
     * @param request 정리 요청 정보
     * @return 정리 작업 결과
     */
    @PostMapping(value = "/artifacts/revisions/cleanup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 정리", 
        description = "사용하지 않는 아티팩트 리비전을 정리합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정리 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    LablupResponse<CleanupArtifactsResponse> cleanupArtifacts(
        @RequestBody CleanupArtifactsRequest request
    );
    
    /**
     * 아티팩트 가져오기 작업 취소
     * 
     * <p>진행 중인 아티팩트 가져오기 작업을 취소합니다.</p>
     * 
     * @param request 취소 요청 정보
     * @return 취소 작업 결과
     */
    @PostMapping(value = "/artifacts/task/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 가져오기 작업 취소", 
        description = "진행 중인 아티팩트 가져오기 작업을 취소합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "취소 요청 성공"),
        @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    CancelImportArtifactResponse cancelImportArtifact(
        @RequestBody CancelImportArtifactRequest request
    );
    
    /**
     * 아티팩트 업데이트
     * 
     * <p>기존 아티팩트의 정보를 업데이트합니다.</p>
     * 
     * @param artifactId 아티팩트 ID
     * @param request 업데이트 요청 정보
     * @return 업데이트 결과
     */
    @PutMapping(value = "/artifacts/{artifact_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "아티팩트 업데이트", 
        description = "기존 아티팩트의 정보를 업데이트합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업데이트 성공"),
        @ApiResponse(responseCode = "404", description = "아티팩트를 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    LablupResponse<UpdateArtifactResponse> updateArtifact(
        @Parameter(description = "아티팩트 ID", required = true) 
        @PathVariable("artifact_id") String artifactId,
        @RequestBody UpdateArtifactRequest request
    );
    
    /**
     * 아티팩트 리비전 README 조회
     * 
     * <p>특정 아티팩트 리비전의 README 파일을 조회합니다.</p>
     * 
     * @param artifactRevisionId 아티팩트 리비전 ID
     * @return README 내용
     */
    @GetMapping("/artifacts/revisions/{artifact_revision_id}/readme")
    @Operation(
        summary = "아티팩트 리비전 README 조회", 
        description = "특정 아티팩트 리비전의 README 파일을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "README 조회 성공"),
        @ApiResponse(responseCode = "404", description = "리비전 또는 README를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    LablupResponse<GetArtifactRevisionReadmeResponse> getArtifactRevisionReadme(
        @Parameter(description = "아티팩트 리비전 ID", required = true) 
        @PathVariable("artifact_revision_id") String artifactRevisionId
    );
    
    /**
     * 사전 서명된 다운로드 URL 조회
     * 
     * <p>객체 스토리지에서 파일을 다운로드하기 위한 사전 서명된 URL을 생성합니다.</p>
     * 
     * @param request 다운로드 URL 요청 정보
     * @return 사전 서명된 다운로드 URL
     */
    @PostMapping(value = "/object-storages/presigned/download", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "사전 서명된 다운로드 URL 조회", 
        description = "객체 스토리지에서 파일을 다운로드하기 위한 사전 서명된 URL을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "URL 생성 성공"),
        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    LablupResponse<GetPresignedDownloadUrlResponse> getPresignedDownloadUrl(
        @RequestBody GetPresignedDownloadUrlRequest request
    );
    
    /**
     * 사전 서명된 업로드 URL 조회
     * 
     * <p>객체 스토리지에 파일을 업로드하기 위한 사전 서명된 URL을 생성합니다.</p>
     * 
     * @param request 업로드 URL 요청 정보
     * @return 사전 서명된 업로드 URL
     */
    @PostMapping(value = "/object-storages/presigned/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "사전 서명된 업로드 URL 조회", 
        description = "객체 스토리지에 파일을 업로드하기 위한 사전 서명된 URL을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    LablupResponse<GetPresignedUploadUrlResponse> getPresignedUploadUrl(
        @RequestBody GetPresignedUploadUrlRequest request
    );
}