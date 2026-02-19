package com.skax.aiplatform.client.sktai.data;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.data.dto.request.DataSetUpdate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetCreate;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetList;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetPreview;
import com.skax.aiplatform.client.sktai.data.dto.response.Dataset;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetHardDeleteResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTag;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTaskResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetUpdateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Data Datasets API Feign Client
 * 
 * <p>
 * SKTAI Data 시스템의 데이터셋 관리를 위한 Feign Client입니다.
 * AI 학습을 위한 데이터셋의 생성, 조회, 수정, 삭제 등의 기능을 제공합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Dataset CRUD</strong>: 데이터셋 생성, 조회, 수정, 삭제</li>
 * <li><strong>파일 업로드</strong>: 멀티파트 파일 업로드를 통한 데이터셋 생성</li>
 * <li><strong>데이터 미리보기</strong>: 데이터셋 내용 미리보기</li>
 * <li><strong>태그 관리</strong>: 데이터셋 태그 추가/삭제</li>
 * <li><strong>하드 삭제</strong>: 백그라운드에서 완전 삭제</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(name = "sktai-data-datasets-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
@Tag(name = "SKTAI Data Datasets", description = "SKTAI Data Datasets Management API")
public interface SktaiDataDatasetsClient {

        /**
         * 데이터셋 목록 조회
         * 
         * @param page   페이지 번호 (기본값: 1)
         * @param size   페이지 크기 (기본값: 10)
         * @param sort   정렬 조건
         * @param filter 필터 조건
         * @param search 검색어
         * @return 데이터셋 목록
         */
        @GetMapping("/api/v1/datasets")
        @Operation(summary = "데이터셋 목록 조회", description = "등록된 데이터셋들의 목록을 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 목록 조회 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DataSetList getDatasets(
                        @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
                        @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") Integer size,
                        @Parameter(description = "정렬 조건") @RequestParam(required = false) String sort,
                        @Parameter(description = "필터 조건") @RequestParam(required = false) String filter,
                        @Parameter(description = "검색어") @RequestParam(required = false) String search);

        /**
         * 데이터셋 생성
         * 
         * @param request 데이터셋 생성 요청
         * @return 생성된 데이터셋
         */
        @Operation(summary = "데이터셋 생성", description = "새로운 데이터셋을 생성합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "데이터셋 생성 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        @PostMapping("/api/v1/datasets")
        DatasetCreateResponse createDataset(@RequestBody DatasetCreate request);

        /**
         * 데이터셋 상세 조회
         * 
         * @param datasetId 데이터셋 ID
         * @return 데이터셋 상세 정보
         */
        @GetMapping("/api/v1/datasets/{dataset_id}")
        @Operation(summary = "데이터셋 상세 조회", description = "지정된 데이터셋의 상세 정보를 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 조회 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DataSetDetail getDatasetById(@Parameter(description = "데이터셋 ID") @PathVariable("dataset_id") UUID datasetId);

        /**
         * 데이터셋 수정
         * 
         * @param datasetId 데이터셋 ID
         * @param request   수정 요청
         * @return 수정 결과
         */
        @PutMapping("/api/v1/datasets/{dataset_id}")
        @Operation(summary = "데이터셋 수정", description = "데이터셋 정보를 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 수정 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DatasetUpdateResponse updateDataset(
                        @Parameter(description = "데이터셋 ID") @PathVariable("dataset_id") UUID datasetId,
                        @RequestBody DataSetUpdate request);

        /**
         * 데이터셋 삭제 (소프트 삭제)
         * 
         * @param datasetId 데이터셋 ID
         */
        @DeleteMapping("/api/v1/datasets/{dataset_id}")
        @Operation(summary = "데이터셋 삭제", description = "데이터셋을 삭제 상태로 표시합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "데이터셋 삭제 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        void deleteDataset(@Parameter(description = "데이터셋 ID") @PathVariable("dataset_id") UUID datasetId);

        /**
         * 데이터셋 하드 삭제
         * 
         * @return 삭제 결과
         */
        @PostMapping("/api/v1/datasets/hard-delete")
        @Operation(summary = "데이터셋 하드 삭제", description = "삭제 표시된 모든 데이터셋을 백그라운드에서 완전 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "하드 삭제 요청 성공")
        })
        DatasetHardDeleteResponse hardDeleteAllDatasets();

        /**
         * 파일 업로드를 통한 데이터셋 생성
         * 
         * @param file        업로드할 파일
         * @param name        데이터셋 이름
         * @param type        데이터셋 타입
         * @param status      상태
         * @param description 설명
         * @param tags        태그
         * @param projectId   프로젝트 ID
         * @param createdBy   생성자
         * @param updatedBy   수정자
         * @param payload     페이로드
         * @return 생성된 데이터셋
         */
        @PostMapping(value = "/api/v1/datasets/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "파일 업로드", description = "파일을 직접 업로드하여 데이터셋을 생성합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        Dataset uploadFile(
                        @Parameter(description = "업로드할 파일") @RequestPart("file") MultipartFile file,
                        @Parameter(description = "데이터셋 이름") @RequestPart("name") String name,
                        @Parameter(description = "데이터셋 타입") @RequestPart("type") String type,
                        @Parameter(description = "상태") @RequestPart(value = "status", required = false) String status,
                        @Parameter(description = "설명") @RequestPart(value = "description", required = false) String description,
                        @Parameter(description = "태그") @RequestPart(value = "tags", required = false) String tags,
                        @Parameter(description = "프로젝트 ID") @RequestPart("project_id") String projectId,
                        @Parameter(description = "생성자") @RequestPart(value = "created_by", required = false) String createdBy,
                        @Parameter(description = "수정자") @RequestPart(value = "updated_by", required = false) String updatedBy,
                        @Parameter(description = "페이로드") @RequestPart(value = "payload", required = false) String payload);

        /**
         * 데이터셋 미리보기
         * 
         * @param datasetId 데이터셋 ID
         * @param chunksize 청크 크기
         * @return 데이터셋 미리보기
         */
        @GetMapping("/api/v1/datasets/{dataset_id}/previews")
        @Operation(summary = "데이터셋 미리보기", description = "지정된 데이터셋의 데이터를 미리보기합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "미리보기 조회 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DataSetPreview getDatasetPreviews(
                        @Parameter(description = "데이터셋 ID") @PathVariable("dataset_id") UUID datasetId,
                        @Parameter(description = "청크 크기") @RequestParam Integer chunksize);

        /**
         * 데이터셋 태그 업데이트
         * 
         * @param datasetId 데이터셋 ID
         * @param tags      태그 목록
         * @return 업데이트된 데이터셋
         */
        @PutMapping("/api/v1/datasets/{dataset_id}/tags")
        @Operation(summary = "태그 업데이트", description = "데이터셋의 태그를 업데이트합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "태그 업데이트 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DatasetUpdateResponse updateTags(
                        @Parameter(description = "데이터셋 ID") @PathVariable("dataset_id") UUID datasetId,
                        @RequestBody List<DatasetTag> tags);

        /**
         * 데이터셋 태그 삭제
         * 
         * @param datasetId 데이터셋 ID
         * @param sktaiTags 삭제할 태그 목록
         * @return 업데이트된 데이터셋
         */
        @DeleteMapping("/api/v1/datasets/{dataset_id}/tags")
        @Operation(summary = "태그 삭제", description = "데이터셋의 태그를 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "태그 삭제 성공"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DatasetUpdateResponse deleteTags(

                        @Parameter(description = "데이터셋 ID") @PathVariable("dataset_id") UUID datasetId,
                        @RequestBody List<DatasetTag> tags);

        /**
         * 데이터셋 소스 아카이브 다운로드
         * 
         * <p>
         * 데이터셋의 원본 파일을 압축한 아카이브(ZIP/TAR)를 다운로드합니다.
         * 지원되는 데이터셋 타입: model_benchmark, rag_evaluation, custom
         * </p>
         * 
         * <h3>주요 특징:</h3>
         * <ul>
         * <li><strong>원본 파일 보존</strong>: 업로드된 원본 파일을 그대로 압축하여 제공</li>
         * <li><strong>압축 포맷</strong>: ZIP 또는 TAR 형식으로 압축</li>
         * <li><strong>대용량 지원</strong>: 스트리밍 방식으로 대용량 파일 다운로드</li>
         * </ul>
         * 
         * <h3>사용 시나리오:</h3>
         * <ul>
         * <li>데이터셋 백업 및 아카이빙</li>
         * <li>외부 시스템으로 데이터셋 전송</li>
         * <li>로컬 환경에서 원본 데이터 분석</li>
         * </ul>
         * 
         * <h3>제한사항:</h3>
         * <ul>
         * <li>지원 타입: model_benchmark, rag_evaluation, custom만 가능</li>
         * <li>네트워크 대역폭과 저장 공간 고려 필요</li>
         * <li>대용량 데이터셋의 경우 압축 시간 소요</li>
         * </ul>
         * 
         * @param datasetId 데이터셋 ID (UUID 형식)
         * @return 파일 스트림을 포함한 응답 엔티티
         */
        @GetMapping(value = "/api/v1/datasets/{dataset_id}/source-archive", produces = { "application/zip",
                        "application/x-tar" })
        @Operation(summary = "데이터셋 소스 아카이브 다운로드", description = """
                        데이터셋의 원본 파일을 압축한 아카이브(ZIP/TAR)를 다운로드합니다.

                        **지원 데이터셋 타입:**
                        - model_benchmark
                        - rag_evaluation
                        - custom

                        **응답 형식:**
                        - Content-Type: application/zip 또는 application/x-tar
                        - Content-Disposition: attachment; filename="dataset-{uuid}.zip"
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "소스 아카이브 다운로드 성공 (파일 스트림 반환)", content = @Content(mediaType = "application/zip", schema = @Schema(type = "string", format = "binary"))),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류 (UUID 형식 오류 또는 지원하지 않는 데이터셋 타입)")
        })
        ResponseEntity<Resource> getDatasetSourceArchive(
                        @Parameter(description = "데이터셋 고유 식별자 (UUID 형식)", example = "123e4567-e89b-12d3-a456-426614174000", required = true) @PathVariable("dataset_id") UUID datasetId);

        /**
         * 데이터셋 Task 조회
         * 
         * <p>
         * 지정된 데이터셋의 Task 정보를 조회합니다.
         * OpenAPI 스펙: GET /api/v1/datasets/{dataset_id}/task
         * </p>
         * 
         * @param datasetId 데이터셋 ID
         * @return 데이터셋 Task 정보
         */
        @GetMapping("/api/v1/datasets/{dataset_id}/task")
        @Operation(summary = "데이터셋 Task 조회", description = "지정된 데이터셋의 Task 정보를 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Task 조회 성공"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
        })
        DatasetTaskResponse getDatasetTask(
                        @Parameter(description = "데이터셋 ID", required = true) @PathVariable("dataset_id") UUID datasetId);
}
