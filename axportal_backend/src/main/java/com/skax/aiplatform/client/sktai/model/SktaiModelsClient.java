package com.skax.aiplatform.client.sktai.model;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.model.dto.request.*;
import com.skax.aiplatform.client.sktai.model.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * SKTAI Models API FeignClient
 * 
 * <p>SKTAI Model 시스템의 Model 관리를 위한 Feign 클라이언트입니다.
 * AI 모델의 전체 라이프사이클을 관리합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>모델 등록</strong>: 새로운 AI 모델 등록</li>
 *   <li><strong>모델 조회</strong>: 등록된 모델 목록 및 상세 조회</li>
 *   <li><strong>모델 수정</strong>: 기존 모델 정보 업데이트</li>
 *   <li><strong>모델 삭제</strong>: 모델 삭제 및 복구</li>
 *   <li><strong>파일 업로드</strong>: 모델 파일 업로드</li>
 *   <li><strong>모델 조회</strong>: 다중 모델 lookup</li>
 *   <li><strong>하드 삭제</strong>: 완전 삭제</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-models-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "SKTAI Models", description = "SKTAI Model 관리 API")
public interface SktaiModelsClient {

    /**
     * Model 등록
     * 
     * @param request Model 생성 요청 정보
     * @return 생성된 Model 정보
     */
    @PostMapping("/api/v1/models")
    @Operation(summary = "Model 등록", description = "새로운 AI 모델을 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Model 등록 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead registerModel(@RequestBody ModelCreate request);

    /**
     * Model 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @param ids 모델 ID 목록
     * @return 페이징된 Model 목록
     */
    @GetMapping("/api/v1/models")
    @Operation(summary = "Model 목록 조회", description = "등록된 모든 AI 모델 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelsRead readModels(
            @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "정렬 기준") @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건") @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어") @RequestParam(value = "search", required = false) String search,
            @Parameter(description = "모델 ID 목록") @RequestParam(value = "ids", required = false) String ids
    );

    /**
     * Model Types 조회
     * 
     * @return 모델 타입 목록
     */
    @GetMapping("/api/v1/models/types")
    @Operation(summary = "Model Types 조회", description = "사용 가능한 모든 모델 타입을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Types 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    List<String> readModelTypes();

    /**
     * Model Tags 조회
     * 
     * @return 모델 태그 목록
     */
    @GetMapping("/api/v1/models/tags")
    @Operation(summary = "Model Tags 조회", description = "사용 가능한 모든 모델 태그를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Tags 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    List<String> readModelTags();

    /**
     * Model 상세 조회
     * 
     * @param modelId Model ID
     * @return Model 상세 정보
     */
    @GetMapping("/api/v1/models/{model_id}")
    @Operation(summary = "Model 상세 조회", description = "지정된 ID의 모델 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelRead readModel(@Parameter(description = "Model ID") @PathVariable("model_id") String modelId);

    /**
     * Model 수정
     * 
     * @param modelId Model ID
     * @param request Model 수정 요청 정보
     * @return 수정된 Model 정보
     */
    @PutMapping("/api/v1/models/{model_id}")
    @Operation(summary = "Model 수정", description = "지정된 ID의 모델 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead editModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelUpdate request
    );

    /**
     * Model 삭제
     * 
     * @param modelId Model ID
     */
    @DeleteMapping("/api/v1/models/{model_id}")
    @Operation(summary = "Model 삭제", description = "지정된 ID의 모델을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Model 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    void removeModel(@Parameter(description = "Model ID") @PathVariable("model_id") String modelId);

    /**
     * Model에 태그 추가
     * 
     * @param modelId Model ID
     * @param tags 추가할 태그 목록
     * @return 업데이트된 Model 정보
     */
    @PutMapping("/api/v1/models/{model_id}/tags")
    @Operation(summary = "Model에 태그 추가", description = "지정된 모델에 태그를 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 추가 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead addTagsToModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelTagRequest[] tags
    );

    /**
     * Model에서 태그 제거
     * 
     * @param modelId Model ID
     * @param tags 제거할 태그 목록
     * @return 업데이트된 Model 정보
     */
    @DeleteMapping("/api/v1/models/{model_id}/tags")
    @Operation(summary = "Model에서 태그 제거", description = "지정된 모델에서 태그를 제거합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "태그 제거 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead removeTagsFromModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelTagRequest[] tags
    );

    /**
     * Model에 작업 추가
     * 
     * @param modelId Model ID
     * @param tasks 추가할 작업 목록
     * @return 업데이트된 Model 정보
     */
    @PutMapping("/api/v1/models/{model_id}/tasks")
    @Operation(summary = "Model에 작업 추가", description = "지정된 모델이 수행할 수 있는 작업을 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "작업 추가 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead addTasksToModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelTaskRequest[] tasks
    );

    /**
     * Model에서 작업 제거
     * 
     * @param modelId Model ID
     * @param tasks 제거할 작업 목록
     * @return 업데이트된 Model 정보
     */
    @DeleteMapping("/api/v1/models/{model_id}/tasks")
    @Operation(summary = "Model에서 작업 제거", description = "지정된 모델이 수행할 수 있는 작업을 제거합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "작업 제거 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead removeTasksFromModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelTaskRequest[] tasks
    );

    /**
     * Model에 언어 추가
     * 
     * @param modelId Model ID
     * @param languages 추가할 언어 목록
     * @return 업데이트된 Model 정보
     */
    @PutMapping("/api/v1/models/{model_id}/languages")
    @Operation(summary = "Model에 언어 추가", description = "지정된 모델이 지원하는 언어를 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "언어 추가 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead addLanguagesToModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelLanguageRequest[] languages
    );

    /**
     * Model에서 언어 제거
     * 
     * @param modelId Model ID
     * @param languages 제거할 언어 목록
     * @return 업데이트된 Model 정보
     */
    @DeleteMapping("/api/v1/models/{model_id}/languages")
    @Operation(summary = "Model에서 언어 제거", description = "지정된 모델이 지원하는 언어를 제거합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "언어 제거 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead removeLanguagesFromModel(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelLanguageRequest[] languages
    );

    /**
     * Model 복구
     * 
     * @param modelId Model ID
     */
    @PutMapping("/api/v1/models/{model_id}/recovery")
    @Operation(summary = "Model 복구", description = "삭제된 모델을 복구합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Model 복구 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    void recoverModel(@Parameter(description = "Model ID") @PathVariable("model_id") String modelId);

    // === Model Version 관리 ===

    /**
     * Model Version 등록
     * 
     * @param modelId Model ID
     * @param request Model Version 생성 요청
     * @return 생성된 Model Version 정보
     */
    @PostMapping("/api/v1/models/{model_id}/versions")
    @Operation(summary = "Model Version 등록", description = "지정된 모델의 새로운 버전을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Model Version 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelVersionRead registerModelVersion(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelVersionCreate request
    );

    /**
     * Model Version 목록 조회
     * 
     * @param modelId Model ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @param ids Version ID 목록
     * @return 페이징된 Model Version 목록
     */
    @GetMapping("/api/v1/models/{model_id}/versions")
    @Operation(summary = "Model Version 목록 조회", description = "지정된 모델의 모든 버전을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Version 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelVersionsRead readModelVersions(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "정렬 기준") @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건") @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어") @RequestParam(value = "search", required = false) String search,
            @Parameter(description = "Version ID 목록") @RequestParam(value = "ids", required = false) String ids
    );

    /**
     * Version 조회 (버전 ID만으로)
     * 
     * @param versionId Version ID
     * @return Version 정보
     */
    @GetMapping("/api/v1/models/versions/{version_id}")
    @Operation(summary = "Version 조회", description = "지정된 버전 ID로 버전 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Version 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Version을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelVersionRead readVersion(@Parameter(description = "Version ID") @PathVariable("version_id") String versionId);

    /**
     * Model Version 조회 (모델 ID + 버전 ID)
     * 
     * @param modelId Model ID
     * @param versionId Version ID
     * @return Model Version 정보
     */
    @GetMapping("/api/v1/models/{model_id}/versions/{version_id}")
    @Operation(summary = "Model Version 조회", description = "특정 모델의 특정 버전을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Version 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model 또는 Version을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelVersionRead readModelVersion(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "Version ID") @PathVariable("version_id") String versionId
    );

    /**
     * Model Version 삭제
     * 
     * @param modelId Model ID
     * @param versionId Version ID
     */
    @DeleteMapping("/api/v1/models/{model_id}/versions/{version_id}")
    @Operation(summary = "Model Version 삭제", description = "지정된 모델의 특정 버전을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Model Version 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model 또는 Version을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    void removeModelVersion(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "Version ID") @PathVariable("version_id") String versionId
    );

    /**
     * Model Version 수정
     * 
     * @param modelId Model ID
     * @param versionId Version ID
     * @param request Model Version 수정 요청
     * @return 수정된 Model Version 정보
     */
    @PutMapping("/api/v1/models/{model_id}/versions/{version_id}")
    @Operation(summary = "Model Version 수정", description = "지정된 모델의 특정 버전을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Version 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model 또는 Version을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelVersionRead editModelVersion(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "Version ID") @PathVariable("version_id") String versionId,
            @RequestBody ModelVersionUpdate request
    );

    /**
     * Version을 Base Model로 승격
     * 
     * @param versionId Version ID
     * @param request 승격 요청
     * @return 승격된 Model 정보
     */
    @PutMapping("/api/v1/models/versions/{version_id}/promote")
    @Operation(summary = "Version을 Base Model로 승격", description = "지정된 버전을 기본 모델로 승격시킵니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Version 승격 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Version을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelRead promoteVersionToBaseModel(
            @Parameter(description = "Version ID") @PathVariable("version_id") String versionId,
            @RequestBody ModelPromoteRequest request
    );

    // === Model Endpoint 관리 ===

    /**
     * Model Endpoint 등록
     * 
     * @param modelId Model ID
     * @param request Model Endpoint 생성 요청
     * @return 생성된 Model Endpoint 정보
     */
    @PostMapping("/api/v1/models/{model_id}/endpoints")
    @Operation(summary = "Model Endpoint 등록", description = "지정된 모델의 새로운 엔드포인트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Model Endpoint 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "입력값 검증 실패")
    })
    ModelEndpointRead registerModelEndpoint(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @RequestBody ModelEndpointCreate request
    );

    /**
     * Model Endpoint 목록 조회
     * 
     * @param modelId Model ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 Model Endpoint 목록
     */
    @GetMapping("/api/v1/models/{model_id}/endpoints")
    @Operation(summary = "Model Endpoint 목록 조회", description = "지정된 모델의 모든 엔드포인트를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Endpoint 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelEndpointsRead readModelEndpoints(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "정렬 기준") @RequestParam(value = "sort", required = false) String sort,
            @Parameter(description = "필터 조건") @RequestParam(value = "filter", required = false) String filter,
            @Parameter(description = "검색어") @RequestParam(value = "search", required = false) String search
    );

    /**
     * Model Endpoint 조회
     * 
     * @param modelId Model ID
     * @param endpointId Endpoint ID
     * @return Model Endpoint 정보
     */
    @GetMapping("/api/v1/models/{model_id}/endpoints/{endpoint_id}")
    @Operation(summary = "Model Endpoint 조회", description = "지정된 모델의 특정 엔드포인트를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Endpoint 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model 또는 Endpoint를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelEndpointRead readModelEndpoint(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "Endpoint ID") @PathVariable("endpoint_id") String endpointId
    );

    /**
     * Model Endpoint 삭제
     * 
     * @param modelId Model ID
     * @param endpointId Endpoint ID
     */
    @DeleteMapping("/api/v1/models/{model_id}/endpoints/{endpoint_id}")
    @Operation(summary = "Model Endpoint 삭제", description = "지정된 모델의 특정 엔드포인트를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Model Endpoint 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model 또는 Endpoint를 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    void removeModelEndpoint(
            @Parameter(description = "Model ID") @PathVariable("model_id") String modelId,
            @Parameter(description = "Endpoint ID") @PathVariable("endpoint_id") String endpointId
    );

    /**
     * Model 파일 업로드
     * 
     * @param file 업로드할 모델 파일
     * @return 업로드 결과
     */
    @PostMapping(value = "/api/v1/models/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Model 파일 업로드", description = "모델 등록을 위한 파일을 업로드합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "파일 검증 실패")
    })
    Object uploadModelFile(@Parameter(description = "모델 파일") @RequestPart("file") MultipartFile file);

    /**
     * Model 하드 삭제
     * 
     * @return 삭제 결과
     */
    @PostMapping("/api/v1/models/hard-delete")
    @Operation(summary = "Model 하드 삭제", description = "삭제 플래그가 설정된 모든 모델을 완전히 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "하드 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    Object hardRemoveModel();

    /**
     * Model Lookup
     * 
     * @param request Model lookup 요청
     * @return 조회 결과
     */
    @PostMapping("/api/v1/models/lookup")
    @Operation(summary = "Model Lookup", description = "여러 모델을 (model_id, version_id) 쌍으로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "요청 검증 실패")
    })
    ModelLookupResponse lookupModels(@RequestBody ModelLookupRequest request);
    
    /**
     * Model Export
     * 
     * <p>지정된 모델의 Export용 데이터를 조회합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param modelId Model ID
     * @return Export용 Model 데이터
     */
    @GetMapping("/api/v1/models/{model_id}/export")
    @Operation(
        summary = "Model Export",
        description = "지정된 모델의 Export용 데이터를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Model Export 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "Model을 찾을 수 없음"),
        @ApiResponse(responseCode = "422", description = "파라미터 검증 실패")
    })
    ModelExportResponse exportModel(
        @Parameter(description = "Model ID") @PathVariable("model_id") String modelId
    );
    
    /**
     * Model Import
     * 
     * <p>JSON 데이터를 받아서 Model을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param request Model Import 요청 데이터
     * @return 생성된 Model 정보
     */
    @PostMapping("/api/v1/models/import")
    @Operation(
        summary = "Model Import",
        description = "JSON 데이터를 받아서 Model을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Model Import 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    ModelImportResponse importModel(
        @Parameter(description = "Model Import 요청 데이터", required = true)
        @RequestBody ModelImportRequest request
    );
}
