package com.skax.aiplatform.client.sktai.data;

import java.util.List;
import java.util.Map;
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
import com.skax.aiplatform.client.sktai.data.dto.request.DatasourceCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasourceUpdate;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSourceCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.Datasource;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceFileList;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceList;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * SKTAI Data Datasources API Feign Client
 *
 * <p>
 * SKTAI Data 시스템의 데이터소스 관리를 위한 Feign Client입니다.
 * 다양한 데이터소스의 연결, 관리, 설정 등의 기능을 제공합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>데이터소스 CRUD: 데이터소스 생성, 조회, 수정, 삭제</li>
 *   <li>데이터소스 목록: 페이징된 데이터소스 목록 조회</li>
 *   <li>파일 관리: 데이터소스 파일 업로드, 다운로드, 목록 조회</li>
 *   <li>메타데이터 관리: 파일 메타데이터 수정 및 조회</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.1
 */
@FeignClient(name = "sktai-data-datasources-client", url = "${sktai.api.base-url}", configuration = SktaiClientConfig.class)
@Tag(name = "SKTAI Data Datasources", description = "SKTAI 데이터소스 관리 API")
    public interface SktaiDataDatasourcesClient {

    /**
     * 데이터소스 목록 조회
     *
     * @param page   페이지 번호 (기본값: 1)
     * @param size   페이지 크기 (기본값: 10)
     * @param sort   정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 데이터소스 목록
     */
    @GetMapping("/api/v1/datasources")
    @Operation(summary = "데이터소스 목록 조회", description = "페이징된 데이터소스 목록을 조회합니다. 검색 및 필터링 기능을 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    DatasourceList getDatasources(
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(description = "페이지 크기", example = "10") @RequestParam(value = "size", defaultValue = "10") Integer size,

            @Parameter(description = "정렬 기준 (예: created_at:desc)") @RequestParam(value = "sort", required = false) String sort,

            @Parameter(description = "필터 조건") @RequestParam(value = "filter", required = false) String filter,

            @Parameter(description = "검색어") @RequestParam(value = "search", required = false) String search);

    /**
     * 새로운 데이터소스 생성
     *
     * @param request 데이터소스 생성 요청
     * @return 생성된 데이터소스 정보
     */
    @PostMapping("/api/v1/datasources")
    @Operation(summary = "데이터소스 생성", description = "새로운 데이터소스를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    DataSourceCreateResponse createDatasource(
            @Parameter(description = "데이터소스 생성 요청", required = true) @RequestBody DatasourceCreate request);

    /**
     * 데이터소스 상세 조회
     *
     * @param datasourceId 데이터소스 ID
     * @return 데이터소스 상세 정보
     */
    @GetMapping("/api/v1/datasources/{datasourceId}")
    @Operation(summary = "데이터소스 상세 조회", description = "지정된 데이터소스의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    DatasourceDetail getDatasource(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable UUID datasourceId);

    /**
     * 데이터소스 수정
     *
     * @param datasourceId 데이터소스 ID
     * @param request      데이터소스 수정 요청
     * @return 수정된 데이터소스 정보
     */
    @PutMapping("/api/v1/datasources/{datasourceId}")
    @Operation(summary = "데이터소스 수정", description = "지정된 데이터소스를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Datasource updateDatasource(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable UUID datasourceId,

            @Parameter(description = "데이터소스 수정 요청", required = true) @RequestBody DatasourceUpdate request);

    /**
     * 데이터소스 삭제
     *
     * @param datasourceId 데이터소스 ID
     */
    @DeleteMapping("/api/v1/datasources/{datasourceId}")
    @Operation(summary = "데이터소스 삭제", description = "지정된 데이터소스를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void deleteDatasource(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable UUID datasourceId);

    // ===== 추가된 API 메서드들 =====

    /**
     * 파일 업로드
     *
     * @param files 업로드할 파일들
     * @return 업로드 결과
     */
    @PostMapping(value = "/api/v1/datasources/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "데이터소스용 파일들을 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Map<String, Object> uploadFiles(
            @Parameter(description = "업로드할 파일들", required = true) @RequestPart("files") List<MultipartFile> files);

    /**
     * 파일 다운로드
     *
     * @param datasourceFileId 데이터소스 파일 ID
     * @return 파일 리소스
     */
    @GetMapping("/api/v1/datasources/download/{datasource_file_id}")
    @Operation(summary = "파일 다운로드", description = "지정된 데이터소스 파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<Resource> downloadFile(
            @Parameter(description = "데이터소스 파일 ID", required = true) @PathVariable("datasource_file_id") String datasourceFileId);

    /**
     * API Key로 파일 다운로드
     *
     * @param datasourceFileId 데이터소스 파일 ID
     * @return 파일 리소스
     */
    @GetMapping("/api/v1/datasources/download/apikey/{datasource_file_id}")
    @Operation(summary = "API Key로 파일 다운로드", description = "API Key를 사용하여 지정된 데이터소스 파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<Resource> downloadFileByApikey(
            @Parameter(description = "데이터소스 파일 ID", required = true) @PathVariable("datasource_file_id") String datasourceFileId);

    /**
     * 데이터소스 파일 목록 조회
     *
     * <p>지정된 데이터소스에 속한 파일들의 목록을 페이징하여 조회합니다.
     * 파일명 검색, 상태 필터링, 정렬 등의 기능을 지원합니다.</p>
     *
     * <h3>지원 기능:</h3>
     * <ul>
     *   <li><strong>페이징</strong>: page, size 파라미터로 페이징 처리</li>
     *   <li><strong>정렬</strong>: created_at, file_name, file_size 기준 정렬</li>
     *   <li><strong>필터링</strong>: 파일 상태(active, inactive 등) 필터</li>
     *   <li><strong>검색</strong>: 파일명 기반 텍스트 검색</li>
     * </ul>
     *
     * @param datasourceId 데이터소스 ID (UUID 형식)
     * @param page 페이지 번호 (기본값: 1, 1부터 시작)
     * @param size 페이지 크기 (기본값: 20, 최대: 100)
     * @param sort 정렬 기준 (예: "created_at:desc", "file_name:asc")
     * @param filter 필터 조건 (예: "status:active", "type:csv")
     * @param search 검색어 (파일명 기반 부분 일치 검색)
     * @return 페이징된 파일 목록과 메타데이터
     */
    @GetMapping("/api/v1/datasources/{datasource_id}/files")
    @Operation(
            summary = "데이터소스 파일 목록 조회",
            description = """
            지정된 데이터소스에 속한 파일들의 목록을 페이징하여 조회합니다.
            
            **정렬 옵션:**
            - created_at:desc (생성일 내림차순) - 기본값
            - created_at:asc (생성일 오름차순)
            - file_name:asc (파일명 오름차순)
            - file_name:desc (파일명 내림차순)
            - file_size:desc (파일크기 내림차순)
            - file_size:asc (파일크기 오름차순)
            
            **필터 옵션:**
            - is_deleted:false (삭제되지 않은 파일만)
            - is_deleted:true (삭제된 파일만)
            
            **검색:**
            - 파일명 기반 부분 일치 검색 지원
            - 대소문자 구분 없음
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (잘못된 datasource_id 형식 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    DatasourceFileList listDatasourceFiles(
            @Parameter(
                    description = "데이터소스 고유 식별자 (UUID 형식)",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable("datasource_id") String datasourceId,

            @Parameter(
                    description = "페이지 번호 (1부터 시작)",
                    example = "1"
            )
            @RequestParam(value = "page", defaultValue = "1") Integer page,

            @Parameter(
                    description = "페이지 크기 (1~100 사이의 값)",
                    example = "20"
            )
            @RequestParam(value = "size", defaultValue = "20") Integer size
    );

    /**
     * 모든 데이터소스 완전 삭제
     *
     * @return 삭제 결과
     */
    @PostMapping("/api/v1/datasources/hard-delete")
    @Operation(summary = "모든 데이터소스 완전 삭제", description = "시스템의 모든 데이터소스를 완전히 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 부족"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Map<String, Object> hardDeleteAllDatasources();

    /**
     * 데이터소스 완전 삭제
     *
     * @param datasourceId 데이터소스 ID
     */
    @DeleteMapping("/api/v1/datasources/{datasource_id}/hard_delete")
    @Operation(summary = "데이터소스 완전 삭제", description = "지정된 데이터소스를 완전히 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void hardDeleteDatasource(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable("datasource_id") String datasourceId);

    /**
     * 파일 메타데이터 수정
     *
     * @param datasourceId 데이터소스 ID
     * @param fileId       파일 ID
     * @param metadata     수정할 메타데이터
     * @return 수정된 메타데이터
     */
    @PutMapping("/api/v1/datasources/{datasource_id}/files/{file_id}/metadata")
    @Operation(summary = "파일 메타데이터 수정", description = "지정된 파일의 메타데이터를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Map<String, Object> updateFileMetadata(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable("datasource_id") String datasourceId,

            @Parameter(description = "파일 ID", required = true) @PathVariable("file_id") String fileId,

            @Parameter(description = "수정할 메타데이터", required = true) @RequestBody Map<String, Object> metadata);

    /**
     * 데이터소스 작업을 Taskmanager로 수정
     *
     * @param datasourceId 데이터소스 ID
     * @param taskUpdate   작업 수정 요청
     * @return 수정된 작업 정보
     */
    @PutMapping("/api/v1/datasources/{datasource_id}/tasks")
    @Operation(summary = "데이터소스 작업 수정 (Taskmanager)", description = "Taskmanager를 통해 데이터소스 작업을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Map<String, Object> updateDatasourceTaskByTaskmanager(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable("datasource_id") String datasourceId,

            @Parameter(description = "작업 수정 요청", required = true) @RequestBody Map<String, Object> taskUpdate);

    /**
     * 데이터소스 작업 생성
     *
     * @param datasourceId 데이터소스 ID
     * @param taskRequest  작업 생성 요청
     * @return 생성된 작업 정보
     */
    @PostMapping("/api/v1/datasources/{datasource_id}/tasks")
    @Operation(summary = "데이터소스 작업 생성", description = "지정된 데이터소스에 새로운 작업을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Map<String, Object> createDatasourceTask(
            @Parameter(description = "데이터소스 ID", required = true) @PathVariable("datasource_id") String datasourceId,

            @Parameter(description = "작업 생성 요청", required = true) @RequestBody Map<String, Object> taskRequest);
}
