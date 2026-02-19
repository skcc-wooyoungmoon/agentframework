package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkResultCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ModelBenchmarkResultUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ResultsBatchUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResultResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResultsListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.ModelBenchmarkResultsSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Model Benchmark Results Feign Client
 * 
 * <p>SKTAI Evaluation API의 Model Benchmark Results 관련 엔드포인트와 통신하는 Feign Client입니다.
 * Model Benchmark 결과의 생성, 조회, 업데이트, 삭제 등의 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>결과 관리</strong>: 벤치마크 결과 생성, 조회, 삭제</li>
 *   <li><strong>백그라운드 업데이트</strong>: 결과 파일 기반 자동 업데이트</li>
 *   <li><strong>요약 정보</strong>: 벤치마크 결과 요약 통계</li>
 *   <li><strong>모델 정보 포함</strong>: 벤치마크 결과와 모델 정보 통합 조회</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiClientConfig SKTAI 공통 설정
 */
@FeignClient(
    name = "sktai-model-benchmark-results-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
@Tag(name = "Model Benchmark Results", description = "SKTAI Model Benchmark Results API")
public interface SktaiModelBenchmarkResultsClient {

    /**
     * Model Benchmark 결과 목록 조회
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 20)
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return Model Benchmark 결과 목록과 페이징 정보
     */
    @GetMapping("/api/v1/model-benchmark-results")
    @Operation(
        summary = "Model Benchmark 결과 목록 조회",
        description = "Model Benchmark 결과 목록을 페이징과 함께 조회합니다."
    )
    ModelBenchmarkResultsListResponse getModelBenchmarkResults(
        @Parameter(description = "페이지 번호", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지당 항목 수", example = "20")
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        
        @Parameter(description = "정렬 조건")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * Model Benchmark 결과 생성
     * 
     * @param request Model Benchmark 결과 생성 요청
     * @return 생성된 Model Benchmark 결과
     */
    @PostMapping("/api/v1/model-benchmark-results")
    @Operation(
        summary = "Model Benchmark 결과 생성",
        description = "새로운 Model Benchmark 결과를 생성합니다."
    )
    ModelBenchmarkResultResponse createModelBenchmarkResult(
        @Parameter(description = "Model Benchmark 결과 생성 요청", required = true)
        @RequestBody ModelBenchmarkResultCreateRequest request
    );

    /**
     * Model Benchmark 결과 요약 조회
     * 
     * @param filter 필터 조건
     * @return Model Benchmark 결과 요약 목록
     */
    @GetMapping("/api/v1/model-benchmark-results/summary")
    @Operation(
        summary = "Model Benchmark 결과 요약 조회",
        description = "Model Benchmark 결과의 요약 정보를 조회합니다."
    )
    ModelBenchmarkResultsSummaryResponse getModelBenchmarkResultsSummary(
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter
    );

    /**
     * Model Benchmark 결과 상세 조회
     * 
     * @param id Model Benchmark 결과 ID
     * @return Model Benchmark 결과 상세 정보 (모델 정보 포함)
     */
    @GetMapping("/api/v1/model-benchmark-results/{id}")
    @Operation(
        summary = "Model Benchmark 결과 상세 조회",
        description = "특정 Model Benchmark 결과의 상세 정보를 조회합니다."
    )
    ModelBenchmarkResultResponse getModelBenchmarkResult(
        @Parameter(description = "Model Benchmark 결과 ID", required = true, example = "1")
        @PathVariable("id") Integer id
    );

    /**
     * Model Benchmark 결과 삭제
     * 
     * @param id Model Benchmark 결과 ID
     */
    @DeleteMapping("/api/v1/model-benchmark-results/{id}")
    @Operation(
        summary = "Model Benchmark 결과 삭제",
        description = "특정 Model Benchmark 결과를 삭제합니다."
    )
    void deleteModelBenchmarkResult(
        @Parameter(description = "Model Benchmark 결과 ID", required = true, example = "1")
        @PathVariable("id") Integer id
    );

    /**
     * Model Benchmark 결과 백그라운드 업데이트
     * 
     * <p>결과 파일을 읽어서 데이터베이스에 업데이트합니다.</p>
     * 
     * @param request Model Benchmark 결과 업데이트 요청
     * @return 업데이트된 Model Benchmark 결과 목록
     */
    @PostMapping("/api/v1/model-benchmark-results/update")
    @Operation(
        summary = "Model Benchmark 결과 백그라운드 업데이트",
        description = "Model Benchmark 결과 파일을 읽어서 데이터베이스에 업데이트합니다."
    )
    ResultsBatchUpdateRequest updateModelBenchmarkResults(
        @Parameter(description = "Model Benchmark 결과 업데이트 요청", required = true)
        @RequestBody ModelBenchmarkResultUpdateRequest request
    );
}
