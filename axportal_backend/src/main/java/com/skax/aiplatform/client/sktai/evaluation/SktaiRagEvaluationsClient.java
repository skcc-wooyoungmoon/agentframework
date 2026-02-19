package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.RagEvaluationCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationsListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI RAG Evaluations Feign Client
 * 
 * <p>SKTAI Evaluation API의 RAG Evaluation 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-rag-evaluations-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiRagEvaluationsClient {

    @GetMapping("/api/v1/rag-evaluations")
    RagEvaluationsListResponse getRagEvaluations(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    @PostMapping("/api/v1/rag-evaluations")
    RagEvaluationResponse createRagEvaluation(@RequestBody RagEvaluationCreateRequest request);

    @GetMapping("/api/v1/rag-evaluations/{id}")
    RagEvaluationResponse getRagEvaluation(@PathVariable("id") Integer id);
}
