package com.skax.aiplatform.client.sktai.evaluation;

import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;
import com.skax.aiplatform.client.sktai.evaluation.dto.request.LogStatusUpdateRequest;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.RagEvaluationLogsResponse;
import com.skax.aiplatform.client.sktai.evaluation.dto.response.LogUpdateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * SKTAI RAG Evaluation Logs Feign Client
 * 
 * <p>SKTAI Evaluation API의 RAG Evaluation Logs 관련 엔드포인트와 통신하는 Feign Client입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@FeignClient(
    name = "sktai-rag-evaluation-logs-client",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiRagEvaluationLogsClient {

    @GetMapping("/api/v1/rag-evaluation-logs")
    RagEvaluationLogsResponse getRagEvaluationLogs(
        @RequestParam(value = "evaluationTaskId") Integer evaluationTaskId,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "20") Integer size
    );

    @PostMapping("/api/v1/rag-evaluation-logs/update")
    LogUpdateResponse updateRagEvaluationLogsStatus(@RequestBody LogStatusUpdateRequest request);
}
