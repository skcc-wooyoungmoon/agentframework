package com.skax.aiplatform.controller.model;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.client.sktai.model.dto.request.ModelImportRequest;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelExportResponse;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelImportResponse;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import feign.FeignException;
import com.skax.aiplatform.dto.model.response.ModelDetailRes;
import com.skax.aiplatform.dto.model.response.ModelRes;
import com.skax.aiplatform.service.model.ModelService;

/**
 * 모델 관리 컨트롤러
 * 
 * <p>모델의 조회 기능을 제공하는 REST API 컨트롤러입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-01-16
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/models")
@Tag(name = "모델 관리", description = "모델 조회 및 관리 API")
public class ModelController {
    
    private final ModelService modelService;
    private final SktaiModelsService sktaiModelsService;
    
    /**
     * 모델 목록 조회
     * 
     * @param pageable 페이지 정보
     * @param sort     정렬 기준
     * @param filter   필터 조건
     * @param search   검색어
     * @param ids      모델 ID 목록
     * @return 페이징된 모델 목록
     */
    @GetMapping
    @Operation(summary = "모델 목록 조회", description = "등록된 모든 모델 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모델 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<ModelRes>> getModels(
            @PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "정렬 기준") @RequestParam(required = false) String sort,
            @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
            @Parameter(description = "검색어") @RequestParam(required = false) String search,
            @Parameter(description = "모델 ID 목록") @RequestParam(required = false) String ids) {
        
        log.info("=== 모델 목록 조회 API 호출 시작 ===");
        log.info("요청 파라미터 - page: {}, size: {}, sort: {}, filter: {}, search: {}, ids: {}", 
                pageable.getPageNumber(), pageable.getPageSize(), sort, filter, search, ids);
        
        try {
            PageResponse<ModelRes> models = modelService.getModels(pageable, sort, filter, search, ids);
            
            log.info("모델 목록 조회 성공 - 총 {}개 모델", models.getTotalElements());
            log.info("=== 모델 목록 조회 API 호출 완료 ===");
            
            return AxResponseEntity.ok(models, "모델 목록을 성공적으로 조회했습니다.");
            
        } catch (BusinessException e) {
            log.error("=== 모델 목록 조회 API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - page: {}, size: {}, sort: {}, filter: {}, search: {}, ids: {}", 
                    pageable.getPageNumber(), pageable.getPageSize(), sort, filter, search, ids, e);
            throw e;
        } catch (FeignException e) {
            log.error("=== 모델 목록 조회 API 호출 실패 (FeignException) ===");
            log.error("요청 파라미터 - page: {}, size: {}, sort: {}, filter: {}, search: {}, ids: {}", 
                    pageable.getPageNumber(), pageable.getPageSize(), sort, filter, search, ids, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 모델 목록 조회 API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - page: {}, size: {}, sort: {}, filter: {}, search: {}, ids: {}", 
                    pageable.getPageNumber(), pageable.getPageSize(), sort, filter, search, ids, e);
            throw e;
        }
    }
    
    /**
     * 모델 상세 조회
     * 
     * @param modelId 모델 ID
     * @return 모델 상세 정보
     */
    @GetMapping("/{modelId}")
    @Operation(summary = "모델 상세 조회", description = "지정된 ID의 모델 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모델 상세 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "모델을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<ModelDetailRes> getModelById(
            @PathVariable @Parameter(description = "모델 ID") String modelId) {
        
        log.info("=== 모델 상세 조회 API 호출 시작 ===");
        log.info("요청 파라미터 - modelId: {}", modelId);
        
        try {
            ModelDetailRes model = modelService.getModelById(modelId);
            
            log.info("모델 상세 조회 성공 - modelId: {}, name: {}", modelId, model.getName());
            log.info("=== 모델 상세 조회 API 호출 완료 ===");
            
            return AxResponseEntity.ok(model, "모델 상세 정보를 성공적으로 조회했습니다.");
            
        } catch (BusinessException e) {
            log.error("=== 모델 상세 조회 API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - modelId: {}", modelId, e);
            throw e;
        } catch (FeignException e) {
            log.error("=== 모델 상세 조회 API 호출 실패 (FeignException) ===");
            log.error("요청 파라미터 - modelId: {}", modelId, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 모델 상세 조회 API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - modelId: {}", modelId, e);
            throw e;
        }
    }
    
    /**
     * 모델 타입 목록 조회
     * 
     * @return 모델 타입 목록
     */
    @GetMapping("/types")
    @Operation(summary = "모델 타입 목록 조회", description = "사용 가능한 모든 모델 타입을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모델 타입 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Object> getModelTypes() {
        
        log.info("=== 모델 타입 목록 조회 API 호출 시작 ===");
        
        try {
            Object modelTypes = modelService.getModelTypes();
            
            log.info("모델 타입 목록 조회 성공");
            log.info("=== 모델 타입 목록 조회 API 호출 완료 ===");
            
            return AxResponseEntity.ok(modelTypes, "모델 타입 목록을 성공적으로 조회했습니다.");
            
        } catch (BusinessException e) {
            log.error("=== 모델 타입 목록 조회 API 호출 실패 (BusinessException) ===", e);
            throw e;
        } catch (FeignException e) {
            log.error("=== 모델 타입 목록 조회 API 호출 실패 (FeignException) ===", e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 모델 타입 목록 조회 API 호출 실패 (RuntimeException) ===", e);
            throw e;
        }
    }
    
    /**
     * 모델 태그 목록 조회
     * 
     * @return 모델 태그 목록
     */
    @GetMapping("/tags")
    @Operation(summary = "모델 태그 목록 조회", description = "사용 가능한 모든 모델 태그를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모델 태그 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Object> getModelTags() {
        
        log.info("=== 모델 태그 목록 조회 API 호출 시작 ===");
        
        try {
            Object modelTags = modelService.getModelTags();
            
            log.info("모델 태그 목록 조회 성공");
            log.info("=== 모델 태그 목록 조회 API 호출 완료 ===");
            
            return AxResponseEntity.ok(modelTags, "모델 태그 목록을 성공적으로 조회했습니다.");
            
        } catch (BusinessException e) {
            log.error("=== 모델 태그 목록 조회 API 호출 실패 (BusinessException) ===", e);
            throw e;
        } catch (FeignException e) {
            log.error("=== 모델 태그 목록 조회 API 호출 실패 (FeignException) ===", e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 모델 태그 목록 조회 API 호출 실패 (RuntimeException) ===", e);
            throw e;
        }
    }
    
    /**
     * 모델 Export
     * 
     * <p>지정된 모델의 Export용 데이터를 조회합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param modelId 모델 ID
     * @return Export용 Model 데이터
     */
    @GetMapping("/{modelId}/export")
    @Operation(
        summary = "모델 Export",
        description = "지정된 모델의 Export용 데이터를 조회합니다. 마이그레이션 등에서 사용됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "모델 Export 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "모델을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<ModelExportResponse> exportModel(
            @PathVariable @Parameter(description = "모델 ID", required = true) String modelId) {
        
        log.info("=== 모델 Export API 호출 시작 ===");
        log.info("요청 파라미터 - modelId: {}", modelId);
        
        try {
            ModelExportResponse response = sktaiModelsService.exportModel(modelId);
            
            log.info("모델 Export 성공 - modelId: {}", modelId);
            log.info("=== 모델 Export API 호출 완료 ===");
            
            return AxResponseEntity.ok(response, "모델 Export를 성공적으로 완료했습니다.");
            
        } catch (BusinessException e) {
            log.error("=== 모델 Export API 호출 실패 (BusinessException) ===");
            log.error("요청 파라미터 - modelId: {}", modelId, e);
            throw e;
        } catch (FeignException e) {
            log.error("=== 모델 Export API 호출 실패 (FeignException) ===");
            log.error("요청 파라미터 - modelId: {}", modelId, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 모델 Export API 호출 실패 (RuntimeException) ===");
            log.error("요청 파라미터 - modelId: {}", modelId, e);
            throw e;
        }
    }
    
    /**
     * 모델 Import
     * 
     * <p>JSON 데이터를 받아서 Model을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param request Model Import 요청 데이터
     * @return 생성된 Model 정보
     */
    @PostMapping("/import")
    @Operation(
        summary = "모델 Import",
        description = "JSON 데이터를 받아서 Model을 생성합니다. 마이그레이션 등에서 사용됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "모델 Import 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<ModelImportResponse> importModel(
            @RequestBody @Parameter(description = "Model Import 요청 데이터", required = true) ModelImportRequest request) {
        
        log.info("=== 모델 Import API 호출 시작 ===");
        log.info("요청 데이터 - modelId: {}", request.getModel() != null ? request.getModel().getId() : "null");
        
        try {
            ModelImportResponse response = sktaiModelsService.importModel(request);
            
            log.info("모델 Import 성공 - modelId: {}, status: {}", response.getId(), response.getStatus());
            log.info("=== 모델 Import API 호출 완료 ===");
            
            return AxResponseEntity.ok(response, "모델 Import를 성공적으로 완료했습니다.");
            
        } catch (BusinessException e) {
            log.error("=== 모델 Import API 호출 실패 (BusinessException) ===", e);
            throw e;
        } catch (FeignException e) {
            log.error("=== 모델 Import API 호출 실패 (FeignException) ===", e);
            throw e;
        } catch (RuntimeException e) {
            log.error("=== 모델 Import API 호출 실패 (RuntimeException) ===", e);
            throw e;
        }
    }
}
