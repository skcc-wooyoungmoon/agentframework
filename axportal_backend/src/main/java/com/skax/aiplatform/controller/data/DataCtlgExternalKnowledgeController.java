package com.skax.aiplatform.controller.data;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.dto.data.request.*;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeChunksRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeFilesRes;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoListResponse;
import com.skax.aiplatform.client.udp.dataiku.dto.request.DataikuExecutionRequest;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuExecutionResponse;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.response.DataCtlgExternalKnowledgeCreateRes;
import com.skax.aiplatform.service.data.DataCtlgExternalKnowledgeService;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeTestResult;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.dto.model.request.GetModelServingReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * External Knowledge 컨트롤러
 * 
 * <p>
 * External Knowledge Repository 관련 조회, 상세조회, 추가, 수정, 삭제를 관리하는 컨트롤러입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/dataCtlg/knowledge/repos")
@RequiredArgsConstructor
@Validated
@Tag(name = "External Knowledge Management", description = "External Knowledge Repository 관리 API")
public class DataCtlgExternalKnowledgeController {

    private final DataCtlgExternalKnowledgeService dataCtlgExternalKnowledgeService;

    /**
     * External Knowledge Repository 목록 조회
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건/files
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return External Knowledge Repository 목록
     */
    @GetMapping("/external")
    @Operation(summary = "External Knowledge Repository 목록 조회", description = "등록된 External Knowledge Repository 목록을 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge Repository 목록 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExternalRepoListResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<ExternalRepoListResponse> getExternalRepos(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,

            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,

            @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건", example = "created_at,desc") String sort,

            @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,

            @RequestParam(value = "search", required = false) @Parameter(description = "검색 키워드") String search) {

        log.info("🔍 External Knowledge Repository 목록 조회 API 호출 - 페이지: {}, 크기: {}, 정렬: {}, 필터: {}, 검색: {}",
                page, size, sort, filter, search);

        // Service를 통한 External Repository 목록 조회
        ExternalRepoListResponse response = dataCtlgExternalKnowledgeService.getExternalRepos(page, size, sort, filter,
                search);

        log.info("✅ External Knowledge Repository 목록 조회 API 완료 - 응답 데이터 수: {}",
                response.getData() != null ? response.getData().size() : 0);

        return AxResponseEntity.ok(response, "External Knowledge Repository 목록 조회가 완료되었습니다.");
    }

    /**
     * External Knowledge 상세 조회
     * 
     * @param knwId 지식 UUID
     * @return External Knowledge 상세 정보
     */
    @GetMapping("/external/{knwId}")
    @Operation(summary = "External Knowledge 상세 조회", description = "지정된 External Knowledge의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge 상세 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "External Knowledge를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<Object> getExternalKnowledge(
            @Parameter(description = "지식 UUID", required = true) @PathVariable("knwId") String knwId) {
        log.info("🔍 External Knowledge 상세 조회 API 호출 - knwId: {}", knwId);

        Object response = dataCtlgExternalKnowledgeService.getExternalKnowledge(knwId);

        log.info("✅ External Knowledge 상세 조회 API 완료 - knwId: {}", knwId);

        return AxResponseEntity.ok(response, "External Knowledge 상세 조회가 완료되었습니다.");
    }

    /**
     * External Knowledge 상세 조회 V2
     *
     * @param externalRepoId 지식 UUID
     * @return External Knowledge 상세 정보
     */
    @GetMapping("/external/v2/{externalRepoId}")
    @Operation(summary = "External Knowledge 상세 조회 V2", description = "지정된 External Knowledge의 상세 정보를 조회합니다. (V2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge 상세 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "External Knowledge를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<Object> getExternalKnowledgeV2(
            @Parameter(description = "지식 UUID", required = true) @PathVariable("externalRepoId") String externalRepoId) {
        log.info("🔍 External Knowledge 상세 조회 API V2 호출 - externalRepoId: {}", externalRepoId);

        Object response = dataCtlgExternalKnowledgeService.getExternalKnowledgeByExternalKnowledgeId(externalRepoId);

        log.info("✅ External Knowledge 상세 조회 API V2 완료 - externalRepoId: {}", externalRepoId);

        return AxResponseEntity.ok(response, "External Knowledge 상세 조회가 완료되었습니다. (V2)");
    }


    /**
     * External Knowledge 생성
     * 
     * @param request External Knowledge 생성 요청
     * @return External Knowledge 생성 응답
     */
    @PostMapping("/external")
    @Operation(summary = "External Knowledge 생성", description = "새로운 External Knowledge를 생성하고 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge 생성 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgExternalKnowledgeCreateRes.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<DataCtlgExternalKnowledgeCreateRes> createExternalKnowledge(
            @Valid @RequestBody DataCtlgExternalKnowledgeCreateReq request) {

        log.info("🚀 External Knowledge 생성 API 호출 - knwId: {}, knwNm: {}",
                request.getKnwId(), request.getKnwNm());

        // Service를 통한 External Knowledge 생성
        DataCtlgExternalKnowledgeCreateRes response = dataCtlgExternalKnowledgeService.createExternalKnowledge(request);

        log.info("✅ External Knowledge 생성 API 완료 - knwId: {}",
                response.getKnwId(), response.getRagChunkIndexNm());

        return AxResponseEntity.ok(response, "External Knowledge 생성이 완료되었습니다.");
    }

    /**
     * External Knowledge 테스트
     * 
     * @param request External Knowledge 테스트 요청
     * @return 테스트 결과
     */
    @PostMapping("/external/test")
    @Operation(summary = "External Knowledge 테스트", description = "External Knowledge 설정의 유효성을 테스트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge 테스트 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExternalKnowledgeTestResult.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<ExternalKnowledgeTestResult> testExternalKnowledge(
            @Valid @RequestBody DataCtlgExternalKnowledgeTestReq request) {

        log.info("🧪 External Knowledge 테스트 API 호출 - embeddingModel: {}, vectorDB: {}",
                request.getEmbeddingModel(), request.getVectorDB());

        // Service를 통한 External Knowledge 테스트
        ExternalKnowledgeTestResult response = dataCtlgExternalKnowledgeService.testExternalKnowledge(request);

        log.info("✅ External Knowledge 테스트 API 완료");

        return AxResponseEntity.ok(response, "External Knowledge 테스트가 완료되었습니다.");
    }

    /**
     * External Knowledge 수정
     * 
     * @param id      지식 ID (knwId 또는 expKnwId)
     * @param request External Knowledge 수정 요청
     * @return External Knowledge 수정 응답
     */
    @PutMapping("/external/{id}")
    @Operation(summary = "External Knowledge 수정", description = "External Knowledge의 정보(이름, 설명, 스크립트, 인덱스명)를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge 수정 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "External Knowledge를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<Object> updateExternalKnowledge(
            @Parameter(description = "지식 ID (knwId 또는 expKnwId)", required = true) @PathVariable("id") String id,
            @Valid @RequestBody @Parameter(description = "수정할 정보 (이름, 설명, 스크립트, 인덱스명)", required = true) DataCtlgExternalKnowledgeUpdateReq request) {
        log.info("✏️ External Knowledge 수정 API 호출 - id: {}", id);
        log.info("  - request: name={}, description={}, script={}, indexName={}",
                request.getName(), request.getDescription(),
                request.getScript() != null ? request.getScript().length() + "자" : "null",
                request.getIndexName());

        Object response = dataCtlgExternalKnowledgeService.updateExternalKnowledge(id, request);

        log.info("✅ External Knowledge 수정 API 완료 - id: {}", id);

        return AxResponseEntity.ok(response, "External Knowledge 수정이 완료되었습니다.");
    }

    /**
     * External Knowledge 삭제 (POST 방식)
     * 
     * @param request 삭제할 Knowledge 정보 목록
     * @return 삭제 완료 응답
     */
    @PostMapping("/external/delete")
    @Operation(summary = "External Knowledge 삭제", description = "External Knowledge를 ADXP, Elasticsearch, DB에서 완전히 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "External Knowledge 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "External Knowledge를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<Void> deleteExternalKnowledge(
            @Valid @RequestBody @Parameter(description = "삭제할 Knowledge 정보 목록", required = true) ExternalKnowledgeDeleteRequest request) {

        log.info("🗑️ External Knowledge 삭제 API 호출 - 항목 수: {}", request.getItems().size());

        // 각 항목에 대해 삭제 처리
        for (ExternalKnowledgeDeleteItem item : request.getItems()) {
            log.info("🗑️ External Knowledge 삭제 처리 - knwId: {}, expKnwId: {}, indexName: {}",
                    item.getKnwId(), item.getExpKnwId(), item.getRagChunkIndexNm());
            dataCtlgExternalKnowledgeService.deleteExternalKnowledgeWithInfo(
                    item.getKnwId(),
                    item.getExpKnwId(),
                    item.getRagChunkIndexNm());
        }

        log.info("✅ External Knowledge 삭제 API 완료 - 총 {}개 삭제", request.getItems().size());

        return AxResponseEntity.ok(null, "External Knowledge 삭제가 완료되었습니다.");
    }

    /**
     * External Knowledge 삭제 요청 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "External Knowledge 삭제 요청")
    public static class ExternalKnowledgeDeleteRequest {
        @Schema(description = "삭제할 Knowledge 정보 목록", required = true)
        private java.util.List<ExternalKnowledgeDeleteItem> items;
    }

    /**
     * External Knowledge 삭제 항목 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "External Knowledge 삭제 항목")
    public static class ExternalKnowledgeDeleteItem {
        @Schema(description = "지식 UUID (DB PK)", example = "550e8400-e29b-41d4-a716-446655440000")
        private String knwId;

        @Schema(description = "External Knowledge repo id (ADXP)", example = "277ddca6-b6b5-463b-8611-476c0be02658")
        private String expKnwId;

        @Schema(description = "RAG chunk index명 (Elasticsearch)", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000")
        private String ragChunkIndexNm;
    }

    /**
     * Dataiku 실행 (지식 데이터 가져오기)
     * 
     * @param request Dataiku 실행 요청
     * @return Dataiku 실행 응답
     */
    @PostMapping("/dataiku/execute")
    @Operation(summary = "Dataiku 실행", description = "지식 데이터 가져오기를 위해 Dataiku 시나리오를 실행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dataiku 실행 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataikuExecutionResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<DataikuExecutionResponse> executeDataiku(
            @Valid @RequestBody @Parameter(description = "Dataiku 실행 요청 (선택한 데이터 정보)", required = true) DataikuExecutionRequest request) {
        log.info("🚀 Dataiku 실행 API 호출");

        DataikuExecutionResponse response = dataCtlgExternalKnowledgeService.executeDataiku(request);

        log.info("✅ Dataiku 실행 API 완료");

        return AxResponseEntity.ok(response, "Dataiku 실행이 완료되었습니다.");
    }

    /**
     * 파일 목록 리스트 조회
     */
    @GetMapping("/files")
    public AxResponseEntity<ExternalKnowledgeFilesRes> getFiles(
            @ModelAttribute ExternalKnowledgeFilesReq request) {
        ExternalKnowledgeFilesRes res = dataCtlgExternalKnowledgeService.getFiles(request);
        return AxResponseEntity.ok(res, "파일 목록 조회 성공");
    }

    /**
     * 파일별 청크 조회
     */
    @GetMapping("/chunks")
    public AxResponseEntity<ExternalKnowledgeChunksRes> getFileChunks(
            @ModelAttribute ExternalKnowledgeChunksReq request) {
        ExternalKnowledgeChunksRes res = dataCtlgExternalKnowledgeService.getFileChunks(request);
        return AxResponseEntity.ok(res, "파일 청크 조회 성공");
    }

    
    /**
     * External Knowledge 데이터 적재 현황 조회
     * 
     * @param knwId 지식 UUID
     * @return External Knowledge 데이터 적재 현황
     */
    @GetMapping("/external/progress/{knwId}")
    @Operation(summary = "External Knowledge 데이터 적재 현황 조회", description = "지정된 External Knowledge의 데이터 적재 현황(fileLoadProgress, dataPipelineLoadStatus)을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "데이터 적재 현황 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "404", description = "External Knowledge를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public AxResponseEntity<Object> getExternalKnowledgeProgress(
            @Parameter(description = "지식 UUID", required = true) @PathVariable("knwId") String knwId) {
        log.info("🔍 External Knowledge 데이터 적재 현황 조회 API 호출 - knwId: {}", knwId);

        Object response = dataCtlgExternalKnowledgeService.getExternalKnowledgeProgress(knwId);

        log.info("✅ External Knowledge 데이터 적재 현황 조회 API 완료 - knwId: {}", knwId);

        return AxResponseEntity.ok(response, "External Knowledge 데이터 적재 현황 조회가 완료되었습니다.");
    }

    /**
     * 지식 Policy 설정
     *
     * @param knowledgeId  지식 ID (필수)
     * @param memberId    사용자 ID (필수)
     * @param projectName 프로젝트명 (필수)
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    @PostMapping("/{knowledge_id}/policy")
    @Operation(summary = "지식 Policy 설정", description = "지식의 Policy를 설정합니다.")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "지식 Policy 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류") })
    public AxResponseEntity<List<PolicyRequest>> setKnowledgePolicy(
            @PathVariable(value = "knowledge_id", required = true) @Parameter(description = "지식 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String knowledgeId,
            @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
            @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
        log.info("지식 Policy 설정 요청 - knowledgeId: {}, memberId: {}, projectName: {}", knowledgeId, memberId,
                projectName);
        List<PolicyRequest> policy = dataCtlgExternalKnowledgeService.setKnowledgePolicy(knowledgeId, memberId, projectName);
        return AxResponseEntity.ok(policy, "지식 Policy가 성공적으로 설정되었습니다.");
    }

    /**
     * 임베딩 모델 목록 조회
     * 
     * @param request 페이지 및 필터 정보
     * @return 임베딩 모델 목록
     */
    @GetMapping("/embedding-models")
    @Operation(summary = "임베딩 모델 목록 조회", description = "지식 생성에 사용할 임베딩 모델 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임베딩 모델 목록 조회 성공", 
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, 
                    schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @SecurityRequirement(name = "bearerAuth")
    public AxResponseEntity<PageResponse<ServingResponse>> getEmbeddingModels(
            @Valid GetModelServingReq request) {
        log.info("📋 임베딩 모델 목록 조회 API 호출 - page: {}, size: {}, filter: {}", 
                request.getPage(), request.getSize(), request.getFilter());

        PageResponse<ServingResponse> response = dataCtlgExternalKnowledgeService.getEmbeddingModels(request);

        log.info("✅ 임베딩 모델 목록 조회 API 완료 - 총 {}건", 
                response.getContent() != null ? response.getContent().size() : 0);

        return AxResponseEntity.okPage(response, "임베딩 모델 목록 조회가 완료되었습니다.");
    }

}
