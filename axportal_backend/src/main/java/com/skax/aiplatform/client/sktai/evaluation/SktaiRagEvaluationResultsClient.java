package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.ResultsBatchUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResultsListResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResultResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResultsSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI RAG Evaluation Results Feign Client
 * 
 * <p>SKTAI Evaluation API의 RAG Evaluation Results 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-rag-evaluation-results-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiRagEvaluationResultsClient {

    @GetMapping("/api/v1/rag-evaluation-results")
    RagEvaluationResultsListResponse getRagEvaluationResults(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    @PostMapping("/api/v1/rag-evaluation-results")
    RagEvaluationResultResponse createRagEvaluationResult(@RequestBody Object request);

    @GetMapping("/api/v1/rag-evaluation-results/summary")
    RagEvaluationResultsSummaryResponse getRagEvaluationResultsSummary(@RequestParam(value = "filter", required = false) String filter);

    @GetMapping("/api/v1/rag-evaluation-results/{id}")
    RagEvaluationResultResponse getRagEvaluationResult(@PathVariable("id") Integer id);

    @DeleteMapping("/api/v1/rag-evaluation-results/{id}")
    void deleteRagEvaluationResult(@PathVariable("id") Integer id);

    @PostMapping("/api/v1/rag-evaluation-results/update")
    RagEvaluationResultsListResponse updateRagEvaluationResults(@RequestBody ResultsBatchUpdateRequest request);
}
