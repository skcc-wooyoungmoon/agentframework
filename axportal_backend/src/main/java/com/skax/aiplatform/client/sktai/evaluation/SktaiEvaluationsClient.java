package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.EvaluationCreateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.EvaluationsListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI Evaluations Feign Client
 * 
 * <p>SKTAI Evaluation API의 일반 평가 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-evaluations-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiEvaluationsClient {

    @GetMapping("/api/v1/evaluations")
    EvaluationsListResponse getEvaluations(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "20") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
    );

    @PostMapping("/api/v1/evaluations")
    EvaluationResponse createEvaluation(@RequestBody EvaluationCreateRequest request);

    @GetMapping("/api/v1/evaluations/{id}")
    EvaluationResponse getEvaluation(@PathVariable("id") Integer id);
}
