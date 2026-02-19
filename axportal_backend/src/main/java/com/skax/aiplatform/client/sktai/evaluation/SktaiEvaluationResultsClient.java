package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ResultsBatchUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResultsListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResultResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResultsSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Evaluation Results Feign Client
 * 
 * <p>SKTAI Evaluation API의 Evaluation Results 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-evaluation-results-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiEvaluationResultsClient {

    @GetMapping("/api/v1/evaluation-results")
    EvaluationResultsListResponse getEvaluationResults(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    @PostMapping("/api/v1/evaluation-results")
    EvaluationResultResponse createEvaluationResult(@RequestBody Object request);

    @GetMapping("/api/v1/evaluation-results/summary")
    EvaluationResultsSummaryResponse getEvaluationResultsSummary(@RequestParam(value = "filter", required = false) String filter);

    @GetMapping("/api/v1/evaluation-results/{id}")
    EvaluationResultResponse getEvaluationResult(@PathVariable("id") Integer id);

    @DeleteMapping("/api/v1/evaluation-results/{id}")
    void deleteEvaluationResult(@PathVariable("id") Integer id);

    @PostMapping("/api/v1/evaluation-results/update")
    EvaluationResultsListResponse updateEvaluationResults(@RequestBody ResultsBatchUpdateRequest request);
}
