package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkTaskFilesDeleteRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkTaskFilesListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Model Benchmark Feign Client
 * 
 * <p>SKTAI Evaluation API의 Model Benchmark 관련 엔드포인트와 통신하는 Feign Client입니다.
 * Model Benchmark의 생성, 조회, 작업 파일 관리 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Model Benchmark 관리</strong>: 벤치마크 생성, 조회, 목록 조회</li>
 *   <li><strong>작업 파일 관리</strong>: 파일 업로드, 다운로드, 삭제</li>
 *   <li><strong>페이징 지원</strong>: 대용량 데이터에 대한 페이징 처리</li>
 *   <li><strong>필터링/검색</strong>: 조건부 조회 및 검색 기능</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * {@code
 * // Model Benchmark 목록 조회
 * ModelBenchmarkListResponse response = modelBenchmarkClient.getModelBenchmarks(1, 20, null, null, null);
 * 
 * // 새 Model Benchmark 생성
 * ModelBenchmarkCreateRequest request = ModelBenchmarkCreateRequest.builder()
 *     .name("Performance Test")
 *     .tasks("text_generation")
 *     .build();
 * ModelBenchmarkResponse benchmark = modelBenchmarkClient.createModelBenchmark(request);
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiClientConfig SKTAI 공통 설정
 */
@FeignClient(
    name = "sktai-model-benchmark-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "Model Benchmarks", description = "SKTAI Model Benchmark API")
public interface SktaiModelBenchmarkClient {

    /**
     * Model Benchmark 목록 조회
     * 
     * <p>등록된 Model Benchmark들의 목록을 페이징 처리하여 조회합니다.
     * 필터링, 정렬, 검색 기능을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 20)
     * @param sort 정렬 조건 (필드명:방향, 예: "name:asc")
     * @param filter 필터 조건 (필드:값, 예: "is_custom:true")
     * @param search 검색어 (이름, 태스크 등에서 검색)
     * @return Model Benchmark 목록과 페이징 정보
     * 
     * @apiNote 인증이 필요한 API입니다.
     * @since 1.0
     */
    @GetMapping("/api/v1/model-benchmarks")
    @Operation(
        summary = "Model Benchmark 목록 조회",
        description = "페이징, 필터링, 검색이 가능한 Model Benchmark 목록을 조회합니다."
    )
    ModelBenchmarkListResponse getModelBenchmarks(
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수", example = "20")
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        
        @Parameter(description = "정렬 조건 (필드명:방향)", example = "name:asc")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건 (필드:값)", example = "is_custom:true")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어", example = "performance")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * 새 Model Benchmark 생성
     * 
     * <p>새로운 Model Benchmark를 생성합니다.
     * 벤치마크 이름, 작업 목록, Few-shot 설정 등을 포함해야 합니다.</p>
     * 
     * @param request Model Benchmark 생성 요청 데이터
     * @return 생성된 Model Benchmark 정보
     * 
     * @apiNote 인증이 필요한 API입니다.
     * @since 1.0
     */
    @PostMapping("/api/v1/model-benchmarks")
    @Operation(
        summary = "Model Benchmark 생성",
        description = "새로운 Model Benchmark를 생성합니다."
    )
    ModelBenchmarkResponse createModelBenchmark(
        @Parameter(description = "Model Benchmark 생성 요청", required = true)
        @RequestBody ModelBenchmarkCreateRequest request
    );

    /**
     * Model Benchmark 상세 조회
     * 
     * <p>특정 Model Benchmark의 상세 정보를 조회합니다.</p>
     * 
     * @param id Model Benchmark ID
     * @return Model Benchmark 상세 정보
     * 
     * @apiNote 인증이 필요한 API입니다.
     * @since 1.0
     */
    @GetMapping("/api/v1/model-benchmarks/{id}")
    @Operation(
        summary = "Model Benchmark 상세 조회",
        description = "ID로 특정 Model Benchmark의 상세 정보를 조회합니다."
    )
    ModelBenchmarkResponse getModelBenchmark(
        @Parameter(description = "Model Benchmark ID", required = true, example = "1")
        @PathVariable("id") Integer id
    );

    /**
     * Model Benchmark 작업 파일 업로드
     * 
     * <p>Model Benchmark 작업에 사용할 파일들을 업로드합니다.
     * 허용되지 않는 확장자의 파일은 업로드되지 않습니다.</p>
     * 
     * @param id Model Benchmark ID
     * @param files 업로드할 파일들
     * @return 업로드 결과
     * 
     * @apiNote 인증이 필요한 API입니다.
     * @since 1.0
     */
    @PostMapping(value = "/api/v1/model-benchmarks/{id}/task-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Model Benchmark 작업 파일 업로드",
        description = "Model Benchmark 작업에 사용할 파일들을 업로드합니다. 허용되지 않는 확장자의 파일은 업로드되지 않습니다."
    )
    Object uploadModelBenchmarkTaskFiles(
        @Parameter(description = "Model Benchmark ID", required = true, example = "1")
        @PathVariable("id") Integer id,
        
        @Parameter(description = "업로드할 파일들", required = true)
        @RequestPart("files") MultipartFile[] files
    );

    /**
     * Model Benchmark 업로드된 작업 파일 목록 조회
     * 
     * <p>특정 Model Benchmark에 업로드된 작업 파일들의 목록을 조회합니다.</p>
     * 
     * @param id Model Benchmark ID
     * @return 업로드된 파일 목록
     * 
     * @apiNote 인증이 필요한 API입니다.
     * @since 1.0
     */
    @GetMapping("/api/v1/model-benchmarks/{id}/task-files")
    @Operation(
        summary = "Model Benchmark 업로드된 작업 파일 목록 조회",
        description = "특정 Model Benchmark에 업로드된 작업 파일들의 목록을 조회합니다."
    )
    ModelBenchmarkTaskFilesListResponse getModelBenchmarkTaskFiles(
        @Parameter(description = "Model Benchmark ID", required = true, example = "1")
        @PathVariable("id") Integer id
    );

    /**
     * Model Benchmark 작업 파일 삭제
     * 
     * <p>Model Benchmark에서 특정 작업 파일을 삭제합니다.</p>
     * 
     * @param id Model Benchmark ID
     * @param request 삭제할 파일 목록 요청
     * @return 삭제 결과
     * 
     * @apiNote 인증이 필요한 API입니다.
     * @since 1.0
     */
    @DeleteMapping("/api/v1/model-benchmarks/{id}/task-files")
    @Operation(
        summary = "Model Benchmark 작업 파일 삭제",
        description = "Model Benchmark에서 특정 작업 파일을 삭제합니다."
    )
    Object deleteModelBenchmarkTaskFile(
        @Parameter(description = "Model Benchmark ID", required = true, example = "1")
        @PathVariable("id") Integer id,
        
        @Parameter(description = "삭제할 파일 목록", required = true)
        @RequestBody ModelBenchmarkTaskFilesDeleteRequest request
    );
}
